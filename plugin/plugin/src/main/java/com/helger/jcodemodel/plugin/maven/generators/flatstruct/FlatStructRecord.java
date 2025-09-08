package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

public sealed interface FlatStructRecord {

  String fullyQualifiedClassName();

  /**
   * specify package-wide options
   */
  public record PackageCreation(String fullyQualifiedClassName, FieldOptions options) implements FlatStructRecord {
  }

  /**
   * create a class, with options
   */
  public record ClassCreation(String fullyQualifiedClassName, String parentClassName, FieldOptions options)
      implements FlatStructRecord {
  }

  /**
   * create a field.
   */
  public sealed interface FieldCreation extends FlatStructRecord {

    String fieldName();

    /**
     * @return fully readable requested type
     */
    String fieldClassName();

    FieldOptions options();

  }

  /** A field definition in a class, with a simple type that can be an array */
  public record SimpleField(String fullyQualifiedClassName, String fieldName, String fieldInternalClassName,
      int arrayDepth, FieldOptions options)
      implements FieldCreation {
    @Override
    public String fieldClassName() {
      return fieldInternalClassName + "[]".repeat(arrayDepth);
    }
  }


}
