package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

public sealed interface FlatStructRecord {

  String fullyQualifiedClassName();

  /**
   * create a class.
   */
  public record ClassCreation(String fullyQualifiedClassName) implements FlatStructRecord {
  }

  /**
   * create a field.
   */
  public sealed interface FieldCreation extends FlatStructRecord {

    String fieldName();

    String fieldClassName();

  }


  /**
   * we already know the field class before building the model, and it's a flat
   * field
   */
  public record KnownClassFlatField(String fullyQualifiedClassName, String fieldName, Class<?> fieldClass)
      implements FieldCreation {
    @Override
    public String fieldClassName() {
      return fieldClass.getName();
    }
  }

  /** we know the field class before building the model ; it's an array */
  public record KnownClassArrayField(String fullyQualifiedClassName, String fieldName, Class<?> fieldInternalClass)
      implements FieldCreation {
    @Override
    public String fieldClassName() {
      return fieldInternalClass.arrayType().getName();
    }
  }


}
