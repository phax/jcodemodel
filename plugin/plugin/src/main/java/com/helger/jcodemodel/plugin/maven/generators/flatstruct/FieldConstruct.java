package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

/**
 * Additional features to add when constructing fields
 */
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
  },
  LASTUPDATED {
    @Override
    public void apply(FieldOptions opt) {
      opt.setLastUpdated(true);

    }
  },
  NOLASTUPDATED {
  @Override
    public void apply(FieldOptions opt) {
      opt.setLastUpdated(false);
    }
  },
  REDIRECT {
    @Override
    public void apply(FieldOptions opt) {
      opt.setRedirect(true);

    }
  },
  NOREDIRECT {
    @Override
    public void apply(FieldOptions opt) {
      opt.setRedirect(false);
    }
  },
  FINAL {
    @Override
    public void apply(FieldOptions opt) {
      opt.setFinal(true);

    }
  },
  NOFINAL {
    @Override
    public void apply(FieldOptions opt) {
      opt.setFinal(false);
    }
  },
  LIST {
    @Override
    public void apply(FieldOptions opt) {
      opt.setList(true);
    }
  },
  NOLIST {
    @Override
    public void apply(FieldOptions opt) {
      opt.setList(false);
    }
  },;

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
    case "lastupdated", "updated" -> LASTUPDATED;
    case "nolastupdated", "noupdated" -> NOLASTUPDATED;
    case "redirect" -> REDIRECT;
    case "noredirect" -> NOREDIRECT;
    case "final", "const", "immutable" -> FINAL;
    case "nofinal", "noconst", "mutable" -> NOFINAL;
    case "list" -> LIST;
    case "nolist" -> NOLIST;
    default -> null;
    };
  }

}
