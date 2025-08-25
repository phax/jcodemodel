package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

public enum FieldConstruct {

  GETTER {
    @Override
    public void apply(FieldOptions opt) {
      opt.setGetter(true);
    }
  },
  NOGETTER {
    @Override
    public void apply(FieldOptions opt) {
      opt.setGetter(false);
    }
  },
  SETTER {
    @Override
    public void apply(FieldOptions opt) {
      opt.setSetter(true);
    }
  },
  NOSETTER {
    @Override
    public void apply(FieldOptions opt) {
      opt.setSetter(false);
    }
  };

  public abstract void apply(FieldOptions opt);

  public static FieldConstruct of(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }
    return switch (value.toLowerCase()) {
    case "getter", "get" -> GETTER;
    case "nogetter", "noget" -> NOGETTER;
    case "setter", "set" -> SETTER;
    case "nosetter", "noset" -> NOSETTER;
    default -> null;
    };
  }

}
