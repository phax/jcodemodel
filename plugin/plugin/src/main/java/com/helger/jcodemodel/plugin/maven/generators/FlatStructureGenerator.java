package com.helger.jcodemodel.plugin.maven.generators;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;
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
      jdc.field(JMod.PUBLIC, model._ref(kcff.fieldClass()), kcff.fieldName());
    } else if (rec instanceof KnownClassArrayField kcaf) {
      JDefinedClass jdc = addClass(model, kcaf.fullyQualifiedClassName());
      Class<?> fieldType = kcaf.fieldInternalClass();
      for (int i = 0; i < kcaf.arrayDepth(); i++) {
        fieldType = fieldType.arrayType();
      }
      jdc.field(JMod.PUBLIC, model._ref(fieldType), kcaf.fieldName());
    } else {
      throw new RuntimeException("can't apply reccord " + rec);
    }
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
