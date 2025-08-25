package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

public class FieldOptions {

  private FieldOptions parent = null;

  public FieldOptions setParent(FieldOptions parent) {
    this.parent = parent;
    return this;
  }

  // visibility of the field

  private FieldVisibility visibility = null;

  public FieldOptions setVisibility(FieldVisibility visibility) {
    this.visibility = visibility;
    return this;
  }

  public static final FieldVisibility DEFAULT_VISIBILITY = FieldVisibility.PUBLIC;

  public FieldVisibility visibility() {
    if (visibility != null) {
      return visibility;
    }
    if (parent != null) {
      return parent.visibility();
    }
    return DEFAULT_VISIBILITY;
  }

  // create getter

  private Boolean getter = null;

  public FieldOptions setGetter(Boolean getter) {
    this.getter = getter;
    return this;
  }

  public static final boolean DEFAULT_GETTER = false;

  public boolean getter() {
    if (getter != null) {
      return getter;
    }
    if (parent != null) {
      return parent.getter();
    }
    return DEFAULT_GETTER;
  }

  // create setter

  private Boolean setter = null;

  public FieldOptions setSetter(Boolean setter) {
    this.setter = setter;
    return this;
  }

  public static final boolean DEFAULT_SETTER = false;

  public boolean setter() {
    if (setter != null) {
      return setter;
    }
    if (parent != null) {
      return parent.setter();
    }
    return DEFAULT_SETTER;
  }

}
