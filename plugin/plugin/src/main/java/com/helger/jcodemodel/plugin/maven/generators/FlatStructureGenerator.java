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

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.FieldCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.PackageCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.SimpleField;

public abstract class FlatStructureGenerator implements CodeModelBuilder {

  protected abstract Stream<FlatStructRecord> loadSource(InputStream source);

  @Override
  public void build(JCodeModel model, InputStream source) throws JCodeModelException {
    List<FlatStructRecord> records = loadSource(source).toList();
    createClasses(model, records);
    updateParentOptions(records);
    applyInheritance(model, records);
    createFields(model, records);
    createConstructors(model, records);
    applyRedirects(model, records);
  }

  /**
   * all the classes we created, by fully qualified name
   */
  private Map<String, JDefinedClass> definedClasses = new HashMap<>();

  /**
   * all the classes we created, by simple name . eg "my.own.LittleClass" would be
   * stored as "LittleClass" .
   */
  private Map<String, Set<JDefinedClass>> simpleDefinedClasses = new HashMap<>();

  /**
   * classes and package options, added as we create them.
   */
  private Map<String, FieldOptions> pathOptions = new HashMap<>();

  /**
   * fully qualified class name to the lastUpdated field
   */
  private Map<String, JFieldVar> classLastUpdated = new HashMap<>();

  /**
   * create the classes files, so that we can link them dynamically ; and the
   * packages
   */
  protected void createClasses(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof ClassCreation cc) {
        ensureClass(model, cc.fullyQualifiedClassName(), cc.options());
      } else if (rec instanceof PackageCreation pc) {
        pathOptions.put(pc.fullyQualifiedClassName(), pc.options());
      } else if (rec instanceof FieldCreation fc) {
        ensureClass(model, fc.fullyQualifiedClassName(), null);
      }
    }
  }

  /**
   * ensure a jdefinedclass exists for given name
   */
  protected JDefinedClass ensureClass(JCodeModel model, String fullyQualifiedName, FieldOptions options) {
    JDefinedClass clazz = definedClasses.computeIfAbsent(fullyQualifiedName, n -> {
      try {
        return model._class(n);
      } catch (JCodeModelException e) {
        throw new RuntimeException(e);
      }
    });
    String simpleName = fullyQualifiedName.replaceAll(".*\\.", "");
    simpleDefinedClasses.computeIfAbsent(simpleName, n -> new HashSet<>()).add(clazz);
    if (options == null) {
      pathOptions.computeIfAbsent(fullyQualifiedName, cn -> new FieldOptions());
    } else {
      pathOptions.put(fullyQualifiedName, options);
    }
    return clazz;
  }

  /**
   * link each class options and package option to its parent package option
   */
  protected void updateParentOptions(List<FlatStructRecord> records) {
    for (Entry<String, FieldOptions> e : pathOptions.entrySet()) {
      e.getValue().setParent(findParentOption(e.getKey()));
    }
  }

  /**
   * find the fieldoptions associated to the longest leading path of the child.
   * eg. if child is my.own.LittleClass, and there is a fieldoptions stored for my
   * and one for my.own, then this would return the fieldoptions associated to
   * my.own.
   */
  protected FieldOptions findParentOption(String fullChildName) {
    String search = fullChildName;
    FieldOptions found = null;
    do {
      // remove last element of the path
      int idx = search.lastIndexOf('.');
      search = idx > -1 ? search.substring(0, idx) : null;
      found = pathOptions.get(search);
    } while (found == null && search != null && !search.isBlank());
    return found;
  }

  /**
   * make the classes extends or implement their parent classes, if any
   */
  protected void applyInheritance(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof ClassCreation cc) {
        if (cc.parentClassName() != null && !cc.parentClassName().isBlank()) {
          AbstractJType parentType = resolveType(model, cc.parentClassName());
          if (parentType == null) {
            throw new RuntimeException("can't resolve type " + cc.parentClassName() + " as parent of "
                + cc.fullyQualifiedClassName());
          }
          if (parentType instanceof JPrimitiveType jpt) {
            throw new RuntimeException(
                "class " + cc.fullyQualifiedClassName() + " cannot extend the primitive class " + jpt);
          }
          AbstractJClass parentJClass = (AbstractJClass) parentType;
          JDefinedClass ownerClass = definedClasses.get(cc.fullyQualifiedClassName());
          if (parentJClass.isInterface()) {
            ownerClass._implements(parentJClass);
          } else {
            ownerClass._extends(parentJClass);
          }
        }
      }
    }

  }

  protected void createFields(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof SimpleField af) {
        JDefinedClass owner = Objects.requireNonNull(definedClasses.get(af.fullyQualifiedClassName()),
            "can't find defined class " + af.fullyQualifiedClassName() + " for field " + af);
        FieldOptions ownerOptions = pathOptions.get(owner.fullName());
        Objects.requireNonNull(ownerOptions, "can't find options for class " + owner.fullName()
            + " known classes are " + pathOptions.keySet());
        af.options().setParent(ownerOptions);
        AbstractJType fieldType = resolveType(model, af.fieldInternalClassName(), af.arrayDepth());
        if (fieldType == null) {
          throw new RuntimeException("can't resolve type " + af.fieldClassName() + " for field "
              + af.fullyQualifiedClassName() + "::" + af.fieldName());
        }
        if (af.options().isList()) {
          if (fieldType instanceof JPrimitiveType) {
            throw new RuntimeException("can't create a list of primitive : " + fieldType + " for field "
                + af.fullyQualifiedClassName() + "::" + af.fieldName());
          }
          fieldType = model.ref(List.class).narrow(fieldType);
        }
        addField(owner, fieldType, af.fieldName(), af.options(), model);
      }
    }
  }

  protected AbstractJType resolveType(JCodeModel model, String typeName) {
    AbstractJType defined = definedClasses.get(typeName);
    if (defined == null) {
      Set<JDefinedClass> set = simpleDefinedClasses.get(typeName);
      if (set != null && !set.isEmpty()) {
        if (set.size() > 1) {
          throw new UnsupportedOperationException(
              "several classes stored for simple name " + typeName + " : " + set);
        } else {
          defined = set.iterator().next();
        }
      }
    }
    if (defined != null) {
      return defined;
    }
    try {
      Class<?> staticResolved = convertStaticType(typeName);
      if (staticResolved != null) {
        return model._ref(staticResolved);
      }
    } catch (ClassNotFoundException e) {
    }
    return null;

  }

  protected AbstractJType resolveType(JCodeModel model, String typeName, int arrayLevel) {
    AbstractJType ret = resolveType(model, typeName);
    if (ret == null) {
      return null;
    }
    for (int i = 0; i < arrayLevel; i++) {
      ret = ret.array();
    }
    return ret;
  }

  protected Class<?> convertStaticType(String typeName) throws ClassNotFoundException {
    return switch (typeName) {
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
    case "string", "String" -> String.class;
    default -> Class.forName(typeName);
    };
  }

  protected void addField(JDefinedClass jdc, AbstractJType type, String fieldName, FieldOptions options,
      JCodeModel model) {
    JFieldVar fv = jdc.field(options.getVisibility().jmod | (options.isFinal() ? JMod.FINAL : 0), type, fieldName);
    if (options.isSetter() && !options.isFinal()) {
      addSetter(fv, jdc, model, options);
    }
    if (options.isGetter()) {
      addGetter(fv, jdc);
    }
  }

  protected void addGetter(JFieldVar fv, JDefinedClass jdc) {
    AbstractJType retType = fv.type();
    String methName = "get" + Character.toUpperCase(fv.name().charAt(0))
        + (fv.name().length() < 2 ? "" : fv.name().substring(1));
    JMethod meth = jdc.method(JMod.PUBLIC, retType, methName);
    meth.body()._return(fv);
    meth.javadoc().add("@return the {@link #" + fv.name() + "}");
  }

  protected void addSetter(JFieldVar fv, JDefinedClass jdc, JCodeModel model, FieldOptions options) {
    AbstractJType paramType = fv.type();
    String methName = "set" + Character.toUpperCase(fv.name().charAt(0))
        + (fv.name().length() < 2 ? "" : fv.name().substring(1));
    JMethod meth = jdc.method(JMod.PUBLIC, model.VOID, methName);
    JVar param = meth.param(paramType, fv.name());
    meth.body().assign(JExpr.refthis(fv), param);
    if (options.isLastUpdated()) {
      JFieldVar lastUpdated = classLastUpdated.computeIfAbsent(jdc.fullName(),
          n -> addLastUpdated(jdc, model));
      meth.body().assign(JExpr.refthis(lastUpdated), model.ref(Instant.class).staticInvoke("now"));
    }
    meth.javadoc().add("set the {@link #" + fv.name() + "}");
  }

  protected JFieldVar addLastUpdated(JDefinedClass jdc, JCodeModel model) {
    FieldOptions ownerOptions = pathOptions.get(jdc.fullName());
    JFieldVar lastUpdated = jdc.field(ownerOptions.getVisibility().jmod, model.ref(Instant.class), "lastUpdated",
        JExpr._null());
    lastUpdated.javadoc().add("last time the class was directly set a field using a setter");
    if (ownerOptions.isGetter()) {
      addGetter(lastUpdated, jdc);
    }
    return lastUpdated;
  }

  //
  // apply constructors
  //

  /**
   * create constructors for classes that have a final field or extends a class
   * without no-arg constructor
   */
  protected void createConstructors(JCodeModel model, List<FlatStructRecord> records) {
    Set<JDefinedClass> done = new HashSet<>();
    for (JDefinedClass createdClass : definedClasses.values()) {
      createConstructors(model, createdClass, done);
    }
  }

  protected void createConstructors(JCodeModel model, JDefinedClass createdClass,
      Set<JDefinedClass> done) {
    if (done.contains(createdClass)) {
      return;
    }
    AbstractJClass parent;
    if ((parent = createdClass._extends()) != null) {
      if (parent instanceof JDefinedClass parentClass) {
        createConstructors(model, createdClass, parentClass, done);
      } else if (parent instanceof JReferencedClass parentClass) {
        createConstructors(model, createdClass, parentClass);
      } else {
        throw new UnsupportedOperationException("can't create constructor for abstractjclass " + parent);
      }
    } else {
      createConstructors(model, createdClass);
    }
    done.add(createdClass);
  }

  protected List<JFieldVar> extractFinalFields(JDefinedClass createdClass) {
    return createdClass.fields().values().stream()
        .filter(jfv -> jfv.mods().isFinal())
        // TODO test is field is assigned
        .toList();
  }

  protected void createConstructors(JCodeModel model, JDefinedClass createdClass, JDefinedClass parentClass,
      Set<JDefinedClass> done) {
    createConstructors(model, parentClass, done);
    int[] lowestArgs = { Integer.MAX_VALUE };
    List<JMethod> selectedConstructors = new ArrayList<>();
    parentClass.constructorsStream().forEach(constructor -> {
      int params = constructor.params().size();
      if (params < lowestArgs[0]) {
        lowestArgs[0] = params;
        selectedConstructors.clear();
        selectedConstructors.add(constructor);
      } else if (params == lowestArgs[0]) {
        selectedConstructors.add(constructor);
      }
    });
    if (selectedConstructors.isEmpty() || lowestArgs[0] == 0) {
      createConstructors(model, createdClass);
      return;
    }
    List<JFieldVar> finalFields = extractFinalFields(createdClass);
    selectedConstructors.stream().forEach(sc -> {
      JMethod newConstructor = createdClass.constructor(JMod.PUBLIC);
      JBlock body = newConstructor.body();
      JInvocation supercall = JExpr.invoke("super");
      for (JVar jv : sc.params()) {
        supercall.arg(newConstructor.param(jv.type(), jv.name()));
      }
      body.add(supercall);
      for (JFieldVar jfv : finalFields) {
        body.add(
            JExpr.assign(
                JExpr.refthis(jfv), newConstructor.param(jfv.type(), jfv.name())));
      }
    });
  }

  protected void createConstructors(JCodeModel model, JDefinedClass createdClass, JReferencedClass parentClass) {
    Class<?> parent = parentClass.getReferencedClass();
    if (parent.equals(Object.class)) {
      createConstructors(model, createdClass);
      return;
    }

    int lowestArgs = Integer.MAX_VALUE;
    List<Constructor<?>> selectedConstructors = new ArrayList<>();
    for (Constructor<?> c : parent.getDeclaredConstructors()) {
      if ((c.getModifiers() & (Modifier.PUBLIC | Modifier.PROTECTED)) != 0) {
        int params = c.getParameterCount();
        if (params < lowestArgs) {
          lowestArgs = params;
          selectedConstructors.clear();
          selectedConstructors.add(c);
        } else if (params == lowestArgs) {
          selectedConstructors.add(c);
        }
      }
    }
    if (selectedConstructors.isEmpty() || lowestArgs == 0) {
      createConstructors(model, createdClass);
      return;
    }
    List<JFieldVar> finalFields = extractFinalFields(createdClass);
    selectedConstructors.stream().forEach(sc -> {
      JMethod newConstructor = createdClass.constructor(JMod.PUBLIC);
      JBlock body = newConstructor.body();
      JInvocation supercall = JExpr.invoke("super");
      for (Parameter p : sc.getParameters()) {
        supercall.arg(newConstructor.param(model.ref(p.getType()), p.getName()));
      }
      body.add(supercall);
      for (JFieldVar jfv : finalFields) {
        body.add(
            JExpr.assign(
                JExpr.refthis(jfv), newConstructor.param(jfv.type(), jfv.name())));
      }
    });

  }

  /**
   * create the constructors for a class that does not have super class, or that
   * super class has an empty constructor.
   */
  protected void createConstructors(JCodeModel model, JDefinedClass createdClass) {
    List<JFieldVar> finalFields = extractFinalFields(createdClass);
    if (finalFields.isEmpty()) {
      return;
    }
    JMethod newConstructor = createdClass.constructor(JMod.PUBLIC);
    JBlock body = newConstructor.body();
    for (JFieldVar jfv : finalFields) {
      body.add(
          JExpr.assign(
              JExpr.refthis(jfv), newConstructor.param(jfv.type(), jfv.name())));
    }
  }

  //
  // apply redirect
  //

  protected void applyRedirects(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof SimpleField af) {
        if (af.options().isRedirect()) {
          if (af.arrayDepth() > 0) {
            continue;
          }
          JDefinedClass fieldOwner = Objects.requireNonNull(definedClasses.get(af.fullyQualifiedClassName()),
              "can't find defined class " + af.fullyQualifiedClassName() + " for field " + af);
          AbstractJType fieldType = resolveType(model, af.fieldInternalClassName());
          if (fieldType instanceof JDefinedClass jdc) {
            applyRedirect(model, af, fieldOwner, jdc);
          } else if (fieldType instanceof JReferencedClass jrc) {
            applyRedirect(model, af, fieldOwner, jrc.getReferencedClass());
          } else {
            throw new UnsupportedOperationException("can't apply redirect to " + fieldType + " "
                + af.fieldClassName() + "::" + af.fieldName());
          }
        }
      }
    }
  }

  protected void applyRedirect(JCodeModel model, SimpleField af, JDefinedClass fieldOwner,
      JDefinedClass fieldType) {
    for (JMethod m : fieldType.methods()) {

      if (m.mods().isPublic() && !m.mods().isStatic()) {
        int mods = redirectMethodMods(m.mods().getValue());
        JMethod newMeth = fieldOwner.method(mods, m.type(), m.name());
        JInvocation call = JExpr.invoke(JExpr.ref(af.fieldName()), m.name());
        for (JVar p : m.params()) {
          call.arg(newMeth.param(p.type(), p.name()));
        }
        if (newMeth.type().equals(model.VOID)) {
          newMeth.body().add(call);
        } else {
          newMeth.body()._return(call);
        }
      }
    }
  }

  private static final Set<String> OBJECT_NOARGMETH = new HashSet<>(
      Stream.of(Object.class.getMethods())
          .filter(m -> m.getParameterCount() == 0)
          .map(Method::getName)
          .toList());

  protected void applyRedirect(JCodeModel model, SimpleField af, JDefinedClass fieldOwner,
      Class<?> fieldClass) {
    if (fieldClass.isPrimitive()) {
      return;
    }
    for (Method m : fieldClass.getMethods()) {
      if (
      // synthetic methods are added by the compiler, not in the actual code
      m.isSynthetic()
          // static methods should not be redirected
          || (m.getModifiers() & Modifier.STATIC) != 0
          // don't redirect methods that are either those of Object,
          || m.getDeclaringClass() == Object.class
          // or with no argument and present in Object without argument (hashcode,
          // tostring)
          || m.getParameterCount() == 0 && OBJECT_NOARGMETH.contains(m.getName())) {
        continue;
      }
      int mods = redirectMethodMods(JMod.PUBLIC);
      JMethod newMeth = fieldOwner.method(mods, m.getReturnType(), m.getName());
      JInvocation call = JExpr.invoke(JExpr.ref(af.fieldName()), m.getName());
      for (Parameter p : m.getParameters()) {
        call.arg(newMeth.param(p.getType(), p.getName()));
      }
      if (newMeth.type().equals(model.VOID)) {
        newMeth.body().add(call);
      } else {
        newMeth.body()._return(call);
      }
    }
  }

  static int redirectMethodMods(int jmods) {
    // remove the synchronized and strict modifiers from the calling method.
    return jmods
        & ~JMod.SYNCHRONIZED
        & ~JMod.STRICTFP;
  }

}
