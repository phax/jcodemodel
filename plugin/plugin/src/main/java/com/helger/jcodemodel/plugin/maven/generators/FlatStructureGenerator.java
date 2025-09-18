package com.helger.jcodemodel.plugin.maven.generators;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.helger.jcodemodel.*;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.ConcreteTypes;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldCanner;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOption;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldVisibility;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.Encapsulated;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.Encapsulation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.FieldCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.PackageCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.SimpleField;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.canners.NoCanner;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.canners.reference.WeakReferenceCanner;

public abstract class FlatStructureGenerator implements CodeModelBuilder {

  protected abstract Stream<FlatStructRecord> loadSource(InputStream source);

  private String rootPackage = "";

  @Override
  public void setRootPackage(String rootPackage) {
    this.rootPackage = rootPackage;
  }

  @Override
  public String getRootPackage() {
    return rootPackage;
  }

  protected ConcreteTypes concrete;

  @Override
  public void configure(Map<String, String> params) {
    CodeModelBuilder.super.configure(params);
    concrete = ConcreteTypes.from(params);
  }

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
   * all the classes we created, by local name
   */
  private Map<String, JDefinedClass> definedClasses = new HashMap<>();

  /**
   * all the classes we created, by simple name . eg "my.own.LittleClass" would be
   * stored as "LittleClass" .
   */
  private Map<String, Set<JDefinedClass>> simpleDefinedClasses = new HashMap<>();

  /**
   * classes and package options stored by local name, added as we create them.
   */
  private Map<String, FieldOptions> pathOptions = new HashMap<>();

  /**
   * fully qualified class name to their "lastUpdated" field
   */
  private Map<String, JFieldVar> classLastUpdated = new HashMap<>();

  /**
   * create the classes files, so that we can link them dynamically ; and the
   * packages
   */
  protected void createClasses(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof ClassCreation cc) {
        ensureClass(model, cc.localName(), cc.options());
      } else if (rec instanceof PackageCreation pc) {
        String localName = pc.localName() == null ? "" : pc.localName().replaceAll("^\\.", "");
        pathOptions.put(localName, pc.options());
      } else if (rec instanceof FieldCreation fc) {
        ensureClass(model, fc.localName(), null);
      }
    }
  }

  /**
   * ensure a jdefinedclass exists for given name
   */
  protected JDefinedClass ensureClass(JCodeModel model, String localName, FieldOptions options) {
    JDefinedClass clazz = definedClasses.computeIfAbsent(localName, n -> {
      try {
        return model._class(expandClassName(n));
      } catch (JCodeModelException e) {
        throw new RuntimeException(e);
      }
    });
    String simpleName = localName.replaceAll(".*\\.", "");
    simpleDefinedClasses.computeIfAbsent(simpleName, n -> new HashSet<>()).add(clazz);
    if (options == null) {
      pathOptions.computeIfAbsent(localName, cn -> new FieldOptions());
    } else {
      pathOptions.put(localName, options);
    }
    return clazz;
  }

  /**
   * link each class options and package option to its parent package option
   */
  protected void updateParentOptions(List<FlatStructRecord> records) {
    for (Entry<String, FieldOptions> e : pathOptions.entrySet()) {
      if (!e.getKey().isBlank()) {
        e.getValue().setParent(findParentOption(e.getKey()));
      }
    }
  }

  /**
   * find the FieldOptions associated to the longest leading path of the child.
   * eg. if child is my.own.LittleClass, and there is a fieldoptions stored for
   * "my"
   * and one for "my.own", then this would return the fieldoptions associated to
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
    if (found == null) {
      found = pathOptions.get("");
    }
    return found;
  }

  /**
   * make the classes extends or implement their parent classes, if any
   */
  protected void applyInheritance(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof ClassCreation cc) {
        if (cc.parentType() != null
            && cc.parentType().baseClassName() != null
            && !cc.parentType().baseClassName().isBlank()) {
          AbstractJType parentType = resolveConcreteType(model, cc.parentType());
          if (parentType == null) {
            throw new RuntimeException("can't resolve type " + cc.parentType() + " as parent of "
                + cc.localName());
          }
          if (parentType instanceof JPrimitiveType jpt) {
            throw new RuntimeException(
                "class " + cc.localName() + " cannot extend the primitive class " + jpt);
          }
          AbstractJClass parentJClass = (AbstractJClass) parentType;
          JDefinedClass ownerClass = definedClasses.get(cc.localName());
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
        JDefinedClass owner = Objects.requireNonNull(definedClasses.get(af.localName()),
            "can't find defined class " + af.localName() + " for field " + af);
        FieldOptions ownerOptions = pathOptions.get(af.localName());
        Objects.requireNonNull(ownerOptions, "can't find options for class " + af.localName()
            + " known classes are " + pathOptions.keySet());
        af.options().setParent(ownerOptions);

        AbstractJType fieldType = resolveType(model, af.fieldType());
        if (fieldType == null) {
          throw new RuntimeException("can't resolve type " + af.fieldClassName() + " for field "
              + af.localName() + "::" + af.fieldName());
        }
        addField(owner, fieldType, af.fieldName(), af.options(), model);
      }
    }
  }

  protected AbstractJType resolveType(JCodeModel model, Encapsulated enc) {
    AbstractJType ret = resolveType(model, enc.baseClassName());
    for (Encapsulation e : enc.encapsulations()) {
      ret = e.apply(ret, model);
    }
    return ret;
  }

  protected AbstractJType resolveConcreteType(JCodeModel model, Encapsulated enc) {
    AbstractJType ret = resolveType(model, enc.baseClassName());
    for (Encapsulation e : enc.encapsulations()) {
      ret = e.applyConcrete(ret, model, concrete);
    }
    return ret;
  }

  /**
   * resolve a name to a class. The order of searching is :
   * <ol>
   * <li>a created class with that exact local name</li>
   * <li>a created class with that exact simple name. If several classes exist
   * with that simple name, throws an exception</li>
   * <li>a static class with that exact full name</li>
   * <li>a static class with that exact full name in package java.lang</li>
   * <li>a static class with that exact full name in package java.util</li>
   * </ol>
   *
   * @param model
   * @param typeName
   * @return
   */
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
    Class<?> staticResolved = staticAlias(typeName);
    for (String prefix : new String[] { null, "java.lang", "java.util" }) {
      if (staticResolved != null) {
        break;
      }
      try {
        staticResolved = Class.forName((prefix == null || prefix.isBlank() ? "" : prefix + ".") + typeName);
      } catch (ClassNotFoundException e) {
      }
    }
    return staticResolved == null ? null : model._ref(staticResolved);

  }

  protected AbstractJType resolveType(JCodeModel model, String typeName, List<Encapsulation> encapsulations) {
    AbstractJType ret = resolveType(model, typeName);
    if (ret == null) {
      return null;
    }
    for (Encapsulation e : encapsulations) {
      ret = e.apply(ret, model);
    }
    return ret;
  }

  /**
   * convert an alias to a static class
   *
   * @return corresponding static class, or null if alias does not match any
   */
  protected Class<?> staticAlias(String alias) {
    return switch (alias) {
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

  protected void addField(JDefinedClass jdc, AbstractJType type, String fieldName, FieldOptions options,
      JCodeModel model) {
    FieldCanner fc = getCanner(options.getCanner());
    int fieldMods = options.getVisibility().jmod | (options.isFinal() ? JMod.FINAL : 0);
    JFieldVar fv = fc == null
        ? jdc.field(fieldMods, type, fieldName)
        : fc.makeType(jdc, fieldName, type, fieldMods);
    if (options.isSetter() && !options.isFinal()) {
      addSetter(fv, jdc, model, options, type, fc);
    }
    if (options.isGetter()) {
      addGetter(fv, jdc, type, fc);
    }
    if (fc != null) {
      fc.addAdditional(fv, options);
    }
  }

  protected void addGetter(JFieldVar fv, JDefinedClass jdc, AbstractJType retType, FieldCanner fc) {
    IJExpression retExpression = fc == null ? fv : fc.makeGetter(fv);
    if (retExpression == null) {
      return ;
    }
    String methName = "get" + Character.toUpperCase(fv.name().charAt(0))
        + (fv.name().length() < 2 ? "" : fv.name().substring(1));
    JMethod meth = jdc.method(JMod.PUBLIC, retType, methName);
    meth.body()._return(retExpression);
    meth.javadoc().add("@return the {@link #" + fv.name() + "}");
  }

  protected void addSetter(JFieldVar fv, JDefinedClass jdc, JCodeModel model, FieldOptions options,
      AbstractJType paramType, FieldCanner fc) {
    String methName = "set" + Character.toUpperCase(fv.name().charAt(0))
        + (fv.name().length() < 2 ? "" : fv.name().substring(1));
    JMethod meth = jdc.method(JMod.PUBLIC, model.VOID, methName);
    JVar param = meth.param(paramType, fv.name());
    IJStatement assignExpression = fc == null ? JExpr.assign(JExpr.refthis(fv), param) : fc.makeSetter(param, fv);
    if (assignExpression == null) {
      jdc.methods().remove(meth);
      return;
    }
    meth.body().add(assignExpression);
    if (options.isLastUpdated()) {
      JFieldVar lastUpdated = classLastUpdated.computeIfAbsent(jdc.fullName(),
          n -> addLastUpdated(jdc, model, options));
      meth.body().assign(JExpr.refthis(lastUpdated), model.ref(Instant.class).staticInvoke("now"));
    }
    meth.javadoc().add("set the {@link #" + fv.name() + "}");
  }

  protected JFieldVar addLastUpdated(JDefinedClass jdc, JCodeModel model, FieldOptions fieldOptions) {
    FieldOptions ownerOptions = fieldOptions.getParent();
    JFieldVar lastUpdated = jdc.field(ownerOptions.getVisibility().jmod, model.ref(Instant.class), "lastUpdated",
        JExpr._null());
    lastUpdated.javadoc().add("last time the class was directly set a field using a setter");
    if (ownerOptions.isGetter()) {
      addGetter(lastUpdated, jdc, lastUpdated.type(), null);
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
      if (parent instanceof JNarrowedClass narrowed) {
        parent = narrowed.basis();
      }
//			TODO convert to pattern matching post java 21
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
          // can't redirect calls to a field encapsulated, eg String[] or List<Double>

          if (af.fieldType().encapsulations().size() > 0) {
            continue;
          }
          JDefinedClass fieldOwner = Objects.requireNonNull(definedClasses.get(af.localName()),
              "can't find defined class " + af.localName() + " for field " + af);
          AbstractJType fieldType = resolveType(model, af.fieldType().baseClassName());
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
    Method[] sortedMethods = fieldClass.getMethods();
    Arrays.sort(sortedMethods, Comparator
        .comparing(Method::getName)
        .thenComparing(Method::getParameterCount)
        // Method::toString actually short signature
        .thenComparing(Comparator.comparing(Method::toString)));
    for (Method m : sortedMethods) {
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

  protected void applyToFieldOptions(String optStr, FieldOptions options) {
    if (optStr == null || optStr.isBlank()) {
      return;
    } else {
      optStr = optStr.trim();
    }
    FieldVisibility fv = FieldVisibility.of(optStr);
    if (fv != null) {
      fv.apply(options);
      return;
    }
    FieldOption fa = FieldOption.of(optStr);
    if (fa != null) {
      fa.apply(options);
      return;
    }
    if (cannersAliases().contains(optStr)) {
      options.setCanner(optStr);
      return;
    }
    throw new UnsupportedOperationException("can't deduce option from " + optStr);

  }

  // canners handling

  /**
   * stream the known canners fieldgenerator by their name, to buld the internal
   * map.<br />
   * The usual overriding concatenates the super one with its own, so that its own
   * overwrites the super's.
   *
   * @return stream of canner name to canner generator. The names are the one used
   *         to parse and apply the fields' canner system
   */
  Stream<Entry<String, Class<? extends FieldCanner>>> streamCanners() {
    return Stream.of(
        new SimpleEntry<>("weakref", WeakReferenceCanner.class),
        new SimpleEntry<>("", NoCanner.class));
  }

  private Map<String, FieldCanner> canners = null;

  /**
   * @return map of canner alias to canner implementation
   */
  protected Map<String, FieldCanner> canners() {
    if (canners == null) {
      canners = streamCanners()
          .sequential() // to avoid merging inconsistency
          .collect(
              Collectors.toMap(Entry::getKey,
                  e -> {
                    try {
                      return e.getValue().getConstructor().newInstance();
                    } catch (InstantiationException | IllegalAccessException
                        | IllegalArgumentException
                        | InvocationTargetException | NoSuchMethodException
                        | SecurityException e1) {
                      throw new RuntimeException(e1);
                    }
                  },
                  (o1, o2) -> o2)// merging using the last one
          );
    }
    return canners;
  }

  public FieldCanner getCanner(String alias) {
    if (alias == null) {
      alias = "";
    }
    return canners().get(alias);
  }

  public Set<String> cannersAliases() {
    return canners().keySet();
  }

}
