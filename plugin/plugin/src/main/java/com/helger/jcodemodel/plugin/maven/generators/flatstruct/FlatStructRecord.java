package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;

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
  public record ClassCreation(String fullyQualifiedClassName, Encapsulated parentType, FieldOptions options)
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
  public record SimpleField(String fullyQualifiedClassName, String fieldName, Encapsulated fieldType,
      FieldOptions options)
      implements FieldCreation {
    @Override
    public String fieldClassName() {
      return fieldType().toString();
    }
  }

  enum Encapsulation {
    ARRAY() {
      @Override
      public String apply(String encapsulatedClassName) {
        return encapsulatedClassName + " []";
      }

      @Override
      public AbstractJType apply(AbstractJType t, JCodeModel cm) {
        return t.array();
      }

      @Override
      public AbstractJType applyConcrete(AbstractJType t, JCodeModel cm) {
        return t.array();
      }
    },
    LIST() {
      @Override
      public String apply(String encapsulatedClassName) {
        return "List<" + encapsulatedClassName + ">";
      }

      @Override
      public AbstractJType apply(AbstractJType e, JCodeModel cm) {
        return cm.ref(List.class).narrow(e);
      }

      @Override
      public AbstractJType applyConcrete(AbstractJType e, JCodeModel cm) {
        return cm.ref(ArrayList.class).narrow(e);
      }
    },
    MAP() {
      @Override
      public String apply(String encapsulatedClassName) {
        return "Map<Object, " + encapsulatedClassName + ">";
      }

      @Override
      public AbstractJType apply(AbstractJType e, JCodeModel cm) {
        return cm.ref(Map.class).narrow(cm.ref(Object.class)).narrow(e);
      }

      @Override
      public AbstractJType applyConcrete(AbstractJType e, JCodeModel cm) {
        return cm.ref(HashMap.class).narrow(cm.ref(Object.class)).narrow(e);
      }
    },
    SET() {
      @Override
      public String apply(String encapsulatedClassName) {
        return "Set<" + encapsulatedClassName + ">";
      }

      @Override
      public AbstractJType apply(AbstractJType e, JCodeModel cm) {
        return cm.ref(Set.class).narrow(e);
      }

      @Override
      public AbstractJType applyConcrete(AbstractJType e, JCodeModel cm) {
        return cm.ref(HashSet.class).narrow(e);
      }
    };

    public abstract String apply(String encapsulatedClassName);

    public abstract AbstractJType apply(AbstractJType e, JCodeModel cm);

    public abstract AbstractJType applyConcrete(AbstractJType e, JCodeModel cm);

    public static Encapsulation parse(String s) {
      if (s == null) {
        return null;
      }
      // remove internal whitespaces for eg arrays
      s = s.toLowerCase().replaceAll("\\s", "");
      return switch (s) {
      case "[]" -> ARRAY;
      case "list" -> LIST;
      case "map" -> MAP;
      case "set" -> SET;
      default -> {
        throw new UnsupportedOperationException();
      }
      };
    }
  }

  record Encapsulated(String baseClassName, List<Encapsulation> encapsulations) {

    private static final Pattern BASECLASS_PAT = Pattern.compile("\\s*([\\w\\.]+)\\s*(.*)");

    private static final Pattern ENCAPS_PAT = Pattern.compile("\\s*(\\[\\s*\\]|[\\w]+)\\s*(.*)");

    public static Encapsulated parse(String s) {
      String baseClass = null;
      ArrayList<Encapsulation> encapsulations = new ArrayList<>();
      if (s == null) {
        return new Encapsulated(baseClass, encapsulations);
      }
      Matcher m = BASECLASS_PAT.matcher(s);
      if (m.matches()) {
        baseClass = m.group(1);
        String restType = m.group(2);
        while (restType != null && !restType.isBlank()) {
          Matcher m2 = ENCAPS_PAT.matcher(restType);
          if (!m2.matches()) {
            break;
          }
          encapsulations.add(Encapsulation.parse(m2.group(1)));
          restType = m2.group(2);
        }
      }
      return new Encapsulated(baseClass, encapsulations);
    }

    @Override
    public String toString() {
      String ret = baseClassName();
      for (Encapsulation enc : encapsulations()) {
        ret = enc.apply(ret);
      }
      return ret;
    }

  }

}
