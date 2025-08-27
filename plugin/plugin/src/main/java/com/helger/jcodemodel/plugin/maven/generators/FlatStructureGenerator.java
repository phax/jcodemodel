package com.helger.jcodemodel.plugin.maven.generators;

import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.FieldCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.SimpleField;

public abstract class FlatStructureGenerator implements CodeModelBuilder {

  protected abstract Stream<FlatStructRecord> loadSource(InputStream source);

  @Override
  public void build(JCodeModel model, InputStream source) throws JCodeModelException {
    List<FlatStructRecord> records = loadSource(source).toList();
    createClasses(model, records);
    updateParentOptions();
    applyInheritance();
    createFields(model, records);
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
   * create the classes files, so that we can link them dynamically ; and the
   * packages
   */
  protected void createClasses(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof ClassCreation cc) {
        ensureClass(model, cc.fullyQualifiedClassName());
      } else if (rec instanceof FieldCreation fc) {
        ensureClass(model, fc.fullyQualifiedClassName());
      }
    }
  }

  /**
   * ensure a jdefinedclass exists for given name
   */
  protected JDefinedClass ensureClass(JCodeModel model, String fullyQualifiedName) {
    JDefinedClass clazz = definedClasses.computeIfAbsent(fullyQualifiedName, n -> {
      try {
        return model._class(n);
      } catch (JCodeModelException e) {
        throw new RuntimeException(e);
      }
    });
    String simpleName = fullyQualifiedName.replaceAll(".*\\.", "");
    simpleDefinedClasses.computeIfAbsent(simpleName, n -> new HashSet<>()).add(clazz);
    pathOptions.computeIfAbsent(fullyQualifiedName, cn -> new FieldOptions());
    return clazz;
  }

  /**
   * link each class options to its parent package option
   */
  protected void updateParentOptions() {

  }

  /**
   * make the classes extends or implement their parent classes, if any
   */
  protected void applyInheritance() {

  }

  public void createFields(JCodeModel model, List<FlatStructRecord> records) {
    for (FlatStructRecord rec : records) {
      if (rec instanceof SimpleField af) {
        JDefinedClass jdc = definedClasses.get(af.fullyQualifiedClassName());
        AbstractJType fieldType = resolveType(model, af.fieldInternalClassName(), af.arrayDepth());
        if (fieldType == null) {
          throw new RuntimeException("can't resolve tytpe " + af.fieldClassName() + " for field "
              + af.fullyQualifiedClassName() + "::" + af.fieldName());
        }
        addField(jdc, fieldType, af.fieldName(), af.options(), model);
      }
    }
  }

  protected AbstractJType resolveType(JCodeModel model, String typeName, int arrayLevel) {
    AbstractJType defined = definedClasses.get(typeName);
    if (defined == null) {
      System.err.println("resolving simple class name for " + typeName);
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
      for (int i = 0; i < arrayLevel; i++) {
        defined = defined.array();
      }
      return defined;
    }
    try {
      Class<?> staticResolved = convertType(typeName);
      if (staticResolved != null) {
        for (int i = 0; i < arrayLevel; i++) {
          staticResolved = staticResolved.arrayType();
        }
        return model._ref(staticResolved);
      }
    } catch (ClassNotFoundException e) {
    }
    return null;
  }

  protected Class<?> convertType(String typeName) throws ClassNotFoundException {
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
      addSetter(fv, jdc, model);
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

  protected void addSetter(JFieldVar fv, JDefinedClass jdc, JCodeModel model) {
    AbstractJType paramType = fv.type();
    String methName = "set" + Character.toUpperCase(fv.name().charAt(0))
        + (fv.name().length() < 2 ? "" : fv.name().substring(1));
    JMethod meth = jdc.method(JMod.PUBLIC, model.VOID, methName);
    JVar param = meth.param(paramType, fv.name());
    meth.body().assign(JExpr.refthis(fv), param);
    meth.javadoc().add("set the {@link #" + fv.name() + "}");
  }

}
