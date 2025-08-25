package com.helger.jcodemodel.plugin.maven.generators;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
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
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.KnownClassArrayField;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.KnownClassFlatField;

public abstract class FlatStructureGenerator implements CodeModelBuilder {

  protected abstract Stream<FlatStructRecord> loadSource(InputStream source);

  @Override
  public void build(JCodeModel model, InputStream source) throws JCodeModelException {
    loadSource(source).forEach(rec -> applyRecord(rec, model));
  }

  Map<String, AbstractJType> knownTypes = new HashMap<>();

  Map<String, JDefinedClass> definedClasses = new HashMap<>();

  /**
   * ensure a jdefinedclass exists
   */
  protected JDefinedClass addClass(JCodeModel model, String fullyQualifiedName) {
    JDefinedClass clazz = definedClasses.computeIfAbsent(fullyQualifiedName, n -> {
      try {
        return model._class(n);
      } catch (JCodeModelException e) {
        throw new RuntimeException(e);
      }
    });
    knownTypes.computeIfAbsent(fullyQualifiedName, n -> clazz);
    return clazz;
  }

  public void applyRecord(FlatStructRecord rec, JCodeModel model) {
    if (rec instanceof ClassCreation cc) {
      addClass(model, cc.fullyQualifiedClassName());
    } else if (rec instanceof KnownClassFlatField kcff) {
      JDefinedClass jdc = addClass(model, kcff.fullyQualifiedClassName());
      addField(jdc, model._ref(kcff.fieldClass()), kcff.fieldName(), kcff.options(), model);
    } else if (rec instanceof KnownClassArrayField kcaf) {
      JDefinedClass jdc = addClass(model, kcaf.fullyQualifiedClassName());
      Class<?> fieldType = kcaf.fieldInternalClass();
      for (int i = 0; i < kcaf.arrayDepth(); i++) {
        fieldType = fieldType.arrayType();
      }
      addField(jdc, model._ref(fieldType), kcaf.fieldName(), kcaf.options(), model);
    } else {
      throw new RuntimeException("can't apply reccord " + rec);
    }
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
  }

  protected void addSetter(JFieldVar fv, JDefinedClass jdc, JCodeModel model) {
    AbstractJType paramType = fv.type();
    String methName = "set" + Character.toUpperCase(fv.name().charAt(0))
        + (fv.name().length() < 2 ? "" : fv.name().substring(1));
    JMethod meth = jdc.method(JMod.PUBLIC, model.VOID, methName);
    JVar param = meth.param(paramType, fv.name());
    meth.body().assign(JExpr.refthis(fv), param);
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

}
