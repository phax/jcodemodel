package com.helger.jcodemodel.plugin.maven.generators;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.time.Instant;
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
    applyInheritance(records);
    createFields(model, records);
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
      // remove last dot token
      int idx = search.lastIndexOf('.');
      search = idx > -1 ? search.substring(0, idx) : null;
      found = pathOptions.get(search);
    } while (found == null && search != null && !search.isBlank());
    return found;
  }

  /**
   * make the classes extends or implement their parent classes, if any
   */
  protected void applyInheritance(List<FlatStructRecord> records) {

  }

  public void createFields(JCodeModel model, List<FlatStructRecord> records) {
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
          throw new RuntimeException("can't resolve tytpe " + af.fieldClassName() + " for field "
              + af.fullyQualifiedClassName() + "::" + af.fieldName());
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
    case "long" -> long.class;
    case "Long" -> Long.class;
    case "string", "String" -> String.class;
    default -> Class.forName(typeName);
    };
  }

  protected void addField(JDefinedClass jdc, AbstractJType type, String fieldName, FieldOptions options,
      JCodeModel model) {
    JFieldVar fv = jdc.field(options.visibility().jmod, type, fieldName);
    if (options.setter()) {
      addSetter(fv, jdc, model, options);
    }
    if (options.getter()) {
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
    if (options.lastUpdated()) {
      JFieldVar lastUpdated = classLastUpdated.computeIfAbsent(jdc.fullName(),
          n -> addLastUpdated(jdc, model));
      meth.body().assign(JExpr.refthis(lastUpdated), model.ref(Instant.class).staticInvoke("now"));
    }
    meth.javadoc().add("set the {@link #" + fv.name() + "}");
  }

  protected JFieldVar addLastUpdated(JDefinedClass jdc, JCodeModel model) {
    FieldOptions ownerOptions = pathOptions.get(jdc.fullName());
    JFieldVar lastUpdated = jdc.field(ownerOptions.visibility().jmod, model.ref(Instant.class), "lastUpdated",
        JExpr._null());
    lastUpdated.javadoc().add("last time the class was directly set a field using a setter");
    if (ownerOptions.getter()) {
      addGetter(lastUpdated, jdc);
    }
    return lastUpdated;
  }

  protected void applyRedirects(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof SimpleField af) {
        if (af.options().redirect()) {
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

  protected void applyRedirect(JCodeModel model, SimpleField af, JDefinedClass fieldOwner,
      Class<?> fieldClass) {
    if (fieldClass.isPrimitive()) {
      return;
    }
    for (Method m : fieldClass.getMethods()) {
      if (m.getDeclaringClass() == Object.class) {
        continue;
      }
      System.err.println("redirecting method " + m);
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
