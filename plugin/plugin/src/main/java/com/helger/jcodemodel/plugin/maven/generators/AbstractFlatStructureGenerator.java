/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helger.jcodemodel.plugin.maven.generators;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JInvocation;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JNarrowedClass;
import com.helger.jcodemodel.JPrimitiveType;
import com.helger.jcodemodel.JReferencedClass;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.ICodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.ConcreteTypes;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.EFieldOption;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.EFieldVisibility;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.EEncapsulation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.Encapsulated;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.IFieldCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.PackageCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.SimpleField;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public abstract class AbstractFlatStructureGenerator implements ICodeModelBuilder
{
  private String m_sRootPackage = "";
  /**
   * all the classes we created, by local name
   */
  private final Map <String, JDefinedClass> definedClasses = new HashMap <> ();

  /**
   * all the classes we created, by simple name . eg "my.own.LittleClass" would be stored as
   * "LittleClass" .
   */
  private final Map <String, Set <JDefinedClass>> simpleDefinedClasses = new HashMap <> ();

  /**
   * classes and package options stored by local name, added as we create them.
   */
  private final Map <String, FieldOptions> pathOptions = new HashMap <> ();

  /**
   * fully qualified class name to their "lastUpdated" field
   */
  private final Map <String, JFieldVar> classLastUpdated = new HashMap <> ();

  protected abstract Stream <IFlatStructRecord> loadSource (@Nullable InputStream source);

  public void setRootPackage (final String rootPackage)
  {
    m_sRootPackage = rootPackage;
  }

  public String getRootPackage ()
  {
    return m_sRootPackage;
  }

  protected ConcreteTypes concrete;

  public void configure (final Map <String, String> params)
  {
    ICodeModelBuilder.super.configure (params);
    concrete = ConcreteTypes.from (params);
  }

  public void build (final JCodeModel model, final InputStream source) throws JCodeModelException
  {
    final List <IFlatStructRecord> records = loadSource (source).toList ();
    createClasses (model, records);
    updateParentOptions (records);
    applyInheritance (model, records);
    createFields (model, records);
    createConstructors (model, records);
    applyRedirects (model, records);
  }

  /**
   * create the classes files, so that we can link them dynamically ; and the packages
   */
  protected void createClasses (final JCodeModel model, final List <IFlatStructRecord> records)
  {
    for (final IFlatStructRecord rec : records)
    {
      if (rec instanceof final ClassCreation cc)
      {
        ensureClass (model, cc.localName (), cc.options ());
      }
      else
        if (rec instanceof final PackageCreation pc)
        {
          final String localName = pc.localName () == null ? "" : pc.localName ().replaceAll ("^\\.", "");
          pathOptions.put (localName, pc.options ());
        }
        else
          if (rec instanceof final IFieldCreation fc)
          {
            ensureClass (model, fc.localName (), null);
          }
    }
  }

  /**
   * ensure a jdefinedclass exists for given name
   */
  protected JDefinedClass ensureClass (final JCodeModel model, final String localName, final FieldOptions options)
  {
    final JDefinedClass clazz = definedClasses.computeIfAbsent (localName, n -> {
      try
      {
        return model._class (expandClassName (n));
      }
      catch (final JCodeModelException e)
      {
        throw new RuntimeException (e);
      }
    });
    final String simpleName = localName.replaceAll (".*\\.", "");
    simpleDefinedClasses.computeIfAbsent (simpleName, n -> new HashSet <> ()).add (clazz);
    if (options == null)
    {
      pathOptions.computeIfAbsent (localName, cn -> new FieldOptions ());
    }
    else
    {
      pathOptions.put (localName, options);
    }
    return clazz;
  }

  /**
   * link each class options and package option to its parent package option
   */
  protected void updateParentOptions (final List <IFlatStructRecord> records)
  {
    for (final Entry <String, FieldOptions> e : pathOptions.entrySet ())
    {
      if (!e.getKey ().isBlank ())
      {
        e.getValue ().setParent (findParentOption (e.getKey ()));
      }
    }
  }

  /**
   * find the FieldOptions associated to the longest leading path of the child. eg. if child is
   * my.own.LittleClass, and there is a fieldoptions stored for "my" and one for "my.own", then this
   * would return the fieldoptions associated to my.own.
   */
  protected FieldOptions findParentOption (final String fullChildName)
  {
    String search = fullChildName;
    FieldOptions found = null;
    do
    {
      // remove last element of the path
      final int idx = search.lastIndexOf ('.');
      search = idx > -1 ? search.substring (0, idx) : null;
      found = pathOptions.get (search);
    } while (found == null && search != null && !search.isBlank ());
    if (found == null)
    {
      found = pathOptions.get ("");
    }
    return found;
  }

  /**
   * make the classes extends or implement their parent classes, if any
   */
  protected void applyInheritance (final JCodeModel model, final List <IFlatStructRecord> records)
  {
    for (final IFlatStructRecord rec : records)
    {
      if (rec instanceof final ClassCreation cc)
      {
        if (cc.parentType () != null &&
            cc.parentType ().baseClassName () != null &&
            !cc.parentType ().baseClassName ().isBlank ())
        {
          final AbstractJType parentType = resolveConcreteType (model, cc.parentType ());
          if (parentType == null)
          {
            throw new RuntimeException ("can't resolve type " + cc.parentType () + " as parent of " + cc.localName ());
          }
          if (parentType instanceof final JPrimitiveType jpt)
          {
            throw new RuntimeException ("class " + cc.localName () + " cannot extend the primitive class " + jpt);
          }
          final AbstractJClass parentJClass = (AbstractJClass) parentType;
          final JDefinedClass ownerClass = definedClasses.get (cc.localName ());
          if (parentJClass.isInterface ())
          {
            ownerClass._implements (parentJClass);
          }
          else
          {
            ownerClass._extends (parentJClass);
          }
        }
      }
    }
  }

  protected void createFields (final JCodeModel model, final List <IFlatStructRecord> records)
  {
    for (final IFlatStructRecord rec : records)
    {
      if (rec instanceof final SimpleField af)
      {
        final JDefinedClass owner = Objects.requireNonNull (definedClasses.get (af.localName ()),
                                                            "can't find defined class " +
                                                                                                  af.localName () +
                                                                                                  " for field " +
                                                                                                  af);
        final FieldOptions ownerOptions = pathOptions.get (af.localName ());
        Objects.requireNonNull (ownerOptions,
                                "can't find options for class " +
                                              af.localName () +
                                              " known classes are " +
                                              pathOptions.keySet ());
        af.options ().setParent (ownerOptions);

        final AbstractJType fieldType = resolveType (model, af.fieldType ());
        if (fieldType == null)
        {
          throw new RuntimeException ("can't resolve type " +
                                      af.fieldClassName () +
                                      " for field " +
                                      af.localName () +
                                      "::" +
                                      af.fieldName ());
        }
        addField (owner, fieldType, af.fieldName (), af.options (), model);
      }
    }
  }

  protected AbstractJType resolveType (final JCodeModel model, final Encapsulated enc)
  {
    AbstractJType ret = resolveType (model, enc.baseClassName ());
    for (final EEncapsulation e : enc.encapsulations ())
    {
      ret = e.apply (ret, model);
    }
    return ret;
  }

  protected AbstractJType resolveConcreteType (final JCodeModel model, final Encapsulated enc)
  {
    AbstractJType ret = resolveType (model, enc.baseClassName ());
    for (final EEncapsulation e : enc.encapsulations ())
    {
      ret = e.applyConcrete (ret, model, concrete);
    }
    return ret;
  }

  /**
   * resolve a name to a class. The order of searching is :
   * <ol>
   * <li>a created class with that exact local name</li>
   * <li>a created class with that exact simple name. If several classes exist with that simple
   * name, throws an exception</li>
   * <li>a static class with that exact full name</li>
   * <li>a static class with that exact full name in package java.lang</li>
   * <li>a static class with that exact full name in package java.util</li>
   * </ol>
   *
   * @param model
   *        code model
   * @param typeName
   *        type name
   * @return resolved type or <code>null</code>.
   */
  @Nullable
  protected AbstractJType resolveType (final JCodeModel model, final String typeName)
  {
    AbstractJType defined = definedClasses.get (typeName);
    if (defined == null)
    {
      final Set <JDefinedClass> set = simpleDefinedClasses.get (typeName);
      if (set != null && !set.isEmpty ())
      {
        if (set.size () > 1)
        {
          throw new UnsupportedOperationException ("several classes stored for simple name " + typeName + " : " + set);
        }
        defined = set.iterator ().next ();
      }
    }
    if (defined != null)
    {
      return defined;
    }
    Class <?> staticResolved = staticAlias (typeName);
    for (final String prefix : new String [] { null, "java.lang", "java.util" })
    {
      if (staticResolved != null)
      {
        break;
      }
      try
      {
        staticResolved = Class.forName ((prefix == null || prefix.isBlank () ? "" : prefix + ".") + typeName);
      }
      catch (final ClassNotFoundException e)
      {}
    }
    return staticResolved == null ? null : model._ref (staticResolved);
  }

  protected AbstractJType resolveType (final JCodeModel model,
                                       final String typeName,
                                       final List <EEncapsulation> encapsulations)
  {
    AbstractJType ret = resolveType (model, typeName);
    if (ret == null)
      return null;

    for (final EEncapsulation e : encapsulations)
      ret = e.apply (ret, model);

    return ret;
  }

  /**
   * convert an alias to a static class
   * 
   * @param alias
   *        alias to resolve
   * @return corresponding static class, or null if alias does not match any
   */
  @Nullable
  protected Class <?> staticAlias (@Nonnull final String alias)
  {
    return switch (alias)
    {
      case "bool", "boolean" -> boolean.class;
      case "Bool", "Boolean" -> Boolean.class;
      case "char", "character" -> char.class;
      case "Char", "Character" -> Character.class;
      case "double" -> double.class;
      case "Double" -> Double.class;
      case "float" -> float.class;
      case "Float" -> Float.class;
      case "int" -> int.class;
      case "Int", "Integer" -> Integer.class;
      case "date", "instant", "datetime" -> Instant.class;
      case "long" -> long.class;
      case "Long" -> Long.class;
      case "obj", "object" -> Object.class;
      case "string", "String" -> String.class;
      default -> null;
    };
  }

  protected void addField (@Nonnull final JDefinedClass jdc,
                           final AbstractJType type,
                           final String fieldName,
                           @Nonnull final FieldOptions options,
                           final JCodeModel model)
  {
    int fieldMods = options.getVisibility().m_nJMod | (options.isFinal() ? JMod.FINAL : 0);
    final JFieldVar fv = jdc.field(fieldMods,
                                    type,
                                    fieldName);
    if (options.isSetter () && !options.isFinal ())
      addSetter (fv, jdc, model, options);

    if (options.isGetter ())
      addGetter (fv, jdc);
  }

  protected void addGetter (@Nonnull final JFieldVar fv, @Nonnull final JDefinedClass jdc)
  {
    final AbstractJType retType = fv.type ();
    final String methName = "get" +
                            Character.toUpperCase (fv.name ().charAt (0)) +
                            (fv.name ().length () < 2 ? "" : fv.name ().substring (1));
    final JMethod meth = jdc.method (JMod.PUBLIC, retType, methName);
    meth.body ()._return (fv);
    meth.javadoc ().add ("@return the {@link #" + fv.name () + "}");
  }

  protected void addSetter (@Nonnull final JFieldVar fv,
                            @Nonnull final JDefinedClass jdc,
                            @Nonnull final JCodeModel model,
                            @Nonnull final FieldOptions options)
  {
    final AbstractJType paramType = fv.type ();
    final String methName = "set" +
                            Character.toUpperCase (fv.name ().charAt (0)) +
                            (fv.name ().length () < 2 ? "" : fv.name ().substring (1));
    final JMethod meth = jdc.method (JMod.PUBLIC, model.VOID, methName);
    final JVar param = meth.param (paramType, fv.name ());
    meth.body ().assign (JExpr.refthis (fv), param);
    if (options.isLastUpdated ())
    {
      final JFieldVar lastUpdated = classLastUpdated.computeIfAbsent (jdc.fullName (),
                                                                      n -> addLastUpdated (jdc, model, options));
      meth.body ().assign (JExpr.refthis (lastUpdated), model.ref (Instant.class).staticInvoke ("now"));
    }
    meth.javadoc ().add ("set the {@link #" + fv.name () + "}");
  }

  protected JFieldVar addLastUpdated (@Nonnull final JDefinedClass jdc,
                                      @Nonnull final JCodeModel model,
                                      @Nonnull final FieldOptions fieldOptions)
  {
    final FieldOptions ownerOptions = fieldOptions.getParent ();
    final JFieldVar lastUpdated = jdc.field (ownerOptions.getVisibility ().m_nJMod,
                                             model.ref (Instant.class),
                                             "lastUpdated",
                                             JExpr._null ());
    lastUpdated.javadoc ().add ("last time the class was directly set a field using a setter");
    if (ownerOptions.isGetter ())
    {
      addGetter (lastUpdated, jdc);
    }
    return lastUpdated;
  }

  //
  // apply constructors
  //

  /**
   * create constructors for classes that have a final field or extends a class without no-arg
   * constructor
   */
  protected void createConstructors (final JCodeModel model, final List <IFlatStructRecord> records)
  {
    final Set <JDefinedClass> done = new HashSet <> ();
    for (final JDefinedClass createdClass : definedClasses.values ())
    {
      createConstructors (model, createdClass, done);
    }
  }

  protected void createConstructors (final JCodeModel model,
                                     @Nonnull final JDefinedClass createdClass,
                                     @Nonnull final Set <JDefinedClass> done)
  {
    if (done.contains (createdClass))
      return;

    AbstractJClass parent = createdClass._extends ();
    if (parent != null)
    {
      if (parent instanceof final JNarrowedClass narrowed)
        parent = narrowed.basis ();

      // TODO convert to pattern matching post java 21
      if (parent instanceof final JDefinedClass parentClass)
      {
        createConstructors (model, createdClass, parentClass, done);
      }
      else
        if (parent instanceof final JReferencedClass parentClass)
        {
          createConstructors (model, createdClass, parentClass);
        }
        else
        {
          throw new UnsupportedOperationException ("can't create constructor for abstractjclass " + parent);
        }
    }
    else
    {
      createConstructors (model, createdClass);
    }
    done.add (createdClass);
  }

  @Nonnull
  protected List <JFieldVar> extractFinalFields (@Nonnull final JDefinedClass createdClass)
  {
    return createdClass.fields ()
                       .values ()
                       .stream ()
                       .filter (jfv -> jfv.mods ().isFinal ())
                       // TODO test is field is assigned
                       .toList ();
  }

  protected void createConstructors (final JCodeModel model,
                                     @Nonnull final JDefinedClass createdClass,
                                     @Nonnull final JDefinedClass parentClass,
                                     @Nonnull final Set <JDefinedClass> done)
  {
    createConstructors (model, parentClass, done);
    final int [] lowestArgs = { Integer.MAX_VALUE };
    final List <JMethod> selectedConstructors = new ArrayList <> ();
    parentClass.constructorsStream ().forEach (constructor -> {
      final int params = constructor.params ().size ();
      if (params < lowestArgs[0])
      {
        lowestArgs[0] = params;
        selectedConstructors.clear ();
        selectedConstructors.add (constructor);
      }
      else
        if (params == lowestArgs[0])
        {
          selectedConstructors.add (constructor);
        }
    });
    if (selectedConstructors.isEmpty () || lowestArgs[0] == 0)
    {
      createConstructors (model, createdClass);
      return;
    }
    final List <JFieldVar> finalFields = extractFinalFields (createdClass);
    selectedConstructors.stream ().forEach (sc -> {
      final JMethod newConstructor = createdClass.constructor (JMod.PUBLIC);
      final JBlock body = newConstructor.body ();
      final JInvocation supercall = JExpr.invoke ("super");
      for (final JVar jv : sc.params ())
      {
        supercall.arg (newConstructor.param (jv.type (), jv.name ()));
      }
      body.add (supercall);
      for (final JFieldVar jfv : finalFields)
      {
        body.add (JExpr.assign (JExpr.refthis (jfv), newConstructor.param (jfv.type (), jfv.name ())));
      }
    });
  }

  protected void createConstructors (final JCodeModel model,
                                     final JDefinedClass createdClass,
                                     final JReferencedClass parentClass)
  {
    final Class <?> parent = parentClass.getReferencedClass ();
    if (parent.equals (Object.class))
    {
      createConstructors (model, createdClass);
      return;
    }

    int lowestArgs = Integer.MAX_VALUE;
    final List <Constructor <?>> selectedConstructors = new ArrayList <> ();
    for (final Constructor <?> c : parent.getDeclaredConstructors ())
    {
      if ((c.getModifiers () & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0)
      {
        final int params = c.getParameterCount ();
        if (params < lowestArgs)
        {
          lowestArgs = params;
          selectedConstructors.clear ();
          selectedConstructors.add (c);
        }
        else
          if (params == lowestArgs)
          {
            selectedConstructors.add (c);
          }
      }
    }
    if (selectedConstructors.isEmpty () || lowestArgs == 0)
    {
      createConstructors (model, createdClass);
      return;
    }
    final List <JFieldVar> finalFields = extractFinalFields (createdClass);
    selectedConstructors.stream ().forEach (sc -> {
      final JMethod newConstructor = createdClass.constructor (JMod.PUBLIC);
      final JBlock body = newConstructor.body ();
      final JInvocation supercall = JExpr.invoke ("super");
      for (final Parameter p : sc.getParameters ())
      {
        supercall.arg (newConstructor.param (model.ref (p.getType ()), p.getName ()));
      }
      body.add (supercall);
      for (final JFieldVar jfv : finalFields)
      {
        body.add (JExpr.assign (JExpr.refthis (jfv), newConstructor.param (jfv.type (), jfv.name ())));
      }
    });
  }

  /**
   * create the constructors for a class that does not have super class, or that super class has an
   * empty constructor.
   */
  protected void createConstructors (final JCodeModel model, final JDefinedClass createdClass)
  {
    final List <JFieldVar> finalFields = extractFinalFields (createdClass);
    if (finalFields.isEmpty ())
    {
      return;
    }
    final JMethod newConstructor = createdClass.constructor (JMod.PUBLIC);
    final JBlock body = newConstructor.body ();
    for (final JFieldVar jfv : finalFields)
    {
      body.add (JExpr.assign (JExpr.refthis (jfv), newConstructor.param (jfv.type (), jfv.name ())));
    }
  }

  //
  // apply redirect
  //

  protected void applyRedirects (final JCodeModel model, final List <IFlatStructRecord> records)
  {
    for (final IFlatStructRecord rec : records)
    {
      if (rec instanceof final SimpleField af)
      {
        if (af.options ().isRedirect ())
        {
          // can't redirect calls to a field encapsulated, eg String[] or List<Double>

          if (af.fieldType ().encapsulations ().size () > 0)
          {
            continue;
          }
          final JDefinedClass fieldOwner = Objects.requireNonNull (definedClasses.get (af.localName ()),
                                                                   "can't find defined class " +
                                                                                                         af.localName () +
                                                                                                         " for field " +
                                                                                                         af);
          final AbstractJType fieldType = resolveType (model, af.fieldType ().baseClassName ());
          if (fieldType instanceof final JDefinedClass jdc)
          {
            applyRedirect (model, af, fieldOwner, jdc);
          }
          else
            if (fieldType instanceof final JReferencedClass jrc)
            {
              applyRedirect (model, af, fieldOwner, jrc.getReferencedClass ());
            }
            else
            {
              throw new UnsupportedOperationException ("can't apply redirect to " +
                                                       fieldType +
                                                       " " +
                                                       af.fieldClassName () +
                                                       "::" +
                                                       af.fieldName ());
            }
        }
      }
    }
  }

  protected void applyRedirect (final JCodeModel model,
                                final SimpleField af,
                                final JDefinedClass fieldOwner,
                                final JDefinedClass fieldType)
  {
    for (final JMethod m : fieldType.methods ())
    {
      if (m.mods ().isPublic () && !m.mods ().isStatic ())
      {
        final int mods = redirectMethodMods (m.mods ().getValue ());
        final JMethod newMeth = fieldOwner.method (mods, m.type (), m.name ());
        final JInvocation call = JExpr.invoke (JExpr.ref (af.fieldName ()), m.name ());
        for (final JVar p : m.params ())
        {
          call.arg (newMeth.param (p.type (), p.name ()));
        }
        if (newMeth.type ().equals (model.VOID))
        {
          newMeth.body ().add (call);
        }
        else
        {
          newMeth.body ()._return (call);
        }
      }
    }
  }

  private static final Set <String> OBJECT_NOARGMETH = new HashSet <> (Stream.of (Object.class.getMethods ())
                                                                             .filter (m -> m.getParameterCount () == 0)
                                                                             .map (Method::getName)
                                                                             .toList ());

  protected void applyRedirect (final JCodeModel model,
                                final SimpleField af,
                                final JDefinedClass fieldOwner,
                                final Class <?> fieldClass)
  {
    if (fieldClass.isPrimitive ())
    {
      return;
    }
    Method[] sortedMethods = fieldClass.getMethods();
      Arrays.sort(sortedMethods, Comparator
              .comparing(Method::getName)
              .thenComparing(Method::getParameterCount)
              // Method::toString actually short signature
              .thenComparing(Comparator.comparing(Method::toString)));
    for (final Method m : sortedMethods)
    {
      if (
      // synthetic methods are added by the compiler, not in the actual code
      m.isSynthetic ()
      // static methods should not be redirected
          || (m.getModifiers () & Modifier.STATIC) != 0
          // don't redirect methods that are either those of Object,
          || m.getDeclaringClass () == Object.class
          // or with no argument and present in Object without argument (hashcode,
          // tostring)
          || m.getParameterCount () == 0 && OBJECT_NOARGMETH.contains (m.getName ()))
      {
        continue;
      }
      final int mods = redirectMethodMods (JMod.PUBLIC);
      final JMethod newMeth = fieldOwner.method (mods, m.getReturnType (), m.getName ());
      final JInvocation call = JExpr.invoke (JExpr.ref (af.fieldName ()), m.getName ());
      for (final Parameter p : m.getParameters ())
      {
        call.arg (newMeth.param (p.getType (), p.getName ()));
      }
      if (newMeth.type ().equals (model.VOID))
      {
        newMeth.body ().add (call);
      }
      else
      {
        newMeth.body ()._return (call);
      }
    }
  }

  static int redirectMethodMods (final int jmods)
  {
    // remove the synchronized and strict modifiers from the calling method.
    return jmods & ~JMod.SYNCHRONIZED & ~JMod.STRICTFP;
  }

  protected void applyToFieldOptions (String optStr, final FieldOptions options)
  {
    if (optStr == null || optStr.isBlank ())
    {
      return;
    }
    else
    {
      optStr = optStr.trim ();
    }
    final EFieldVisibility fv = EFieldVisibility.of (optStr);
    if (fv != null)
    {
      fv.apply (options);
    }
    else
    {
      final EFieldOption fa = EFieldOption.of (optStr);
      if (fa == null)
      {
        throw new UnsupportedOperationException ("can't deduce option from " + optStr);
      }
      else
      {
        fa.apply (options);
      }
    }
  }

}
