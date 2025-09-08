package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

/**
 * list of options to apply to fields. Those can be hold by classes or packages,
 * represented by their {@link #parent}.
 */
public class FieldOptions {

  private FieldOptions parent = null;

  public FieldOptions setParent(FieldOptions parent) {
    this.parent = parent;
    return this;
  }

  // is field final

  private Boolean _final = null;

  public FieldOptions setFinal(Boolean _final) {
    this._final = _final;
    return this;
  }

  public static final boolean DEFAULT_FINAL = false;

  public boolean isFinal() {
    if (_final != null) {
      return _final;
    }
    if (parent != null) {
      return parent.isFinal();
    }
    return DEFAULT_FINAL;
  }


  // create getter

  private Boolean getter = null;

  public FieldOptions setGetter(Boolean getter) {
    this.getter = getter;
    return this;
  }

  public static final boolean DEFAULT_GETTER = false;

  public boolean isGetter() {
    if (getter != null) {
      return getter;
    }
    if (parent != null) {
      return parent.isGetter();
    }
    return DEFAULT_GETTER;
  }

  // create Instant lastUpdated

  private Boolean lastUpdated = null;

  public FieldOptions setLastUpdated(Boolean lastUpdated) {
    this.lastUpdated = lastUpdated;
    return this;
  }

  public static final boolean DEFAULT_LAST_UPDATED = false;

  public boolean isLastUpdated() {
    if (lastUpdated != null) {
      return lastUpdated;
    }
    if (parent != null) {
      return parent.isLastUpdated();
    }
    return DEFAULT_LAST_UPDATED;
  }

  // is field a list

  private Boolean list = null;

  public FieldOptions setList(Boolean list) {
    this.list = list;
    return this;
  }

  public static final boolean DEFAULT_LIST = false;

  public boolean isList() {
    if (list != null) {
      return list;
    }
    if (parent != null) {
      return parent.isList();
    }
    return DEFAULT_LIST;
  }

  // redirect field methods on the owner class

  private Boolean redirect = null;

  public FieldOptions setRedirect(Boolean redirect) {
    this.redirect = redirect;
    return this;
  }

  public static final boolean DEFAULT_REDIRECT = false;

  public boolean isRedirect() {
    if (redirect != null) {
      return redirect;
    }
    if (parent != null) {
      return parent.isRedirect();
    }
    return DEFAULT_REDIRECT;
  }

  // create setter

  private Boolean setter = null;

  public FieldOptions setSetter(Boolean setter) {
    this.setter = setter;
    return this;
  }

  public static final boolean DEFAULT_SETTER = false;

  public boolean isSetter() {
    if (setter != null) {
      return setter;
    }
    if (parent != null) {
      return parent.isSetter();
    }
    return DEFAULT_SETTER;
  }

  // visibility of the field

  private FieldVisibility visibility = null;

  public FieldOptions setVisibility(FieldVisibility visibility) {
    this.visibility = visibility;
    return this;
  }

  public static final FieldVisibility DEFAULT_VISIBILITY = FieldVisibility.PUBLIC;

  public FieldVisibility getVisibility() {
    if (visibility != null) {
      return visibility;
    }
    if (parent != null) {
      return parent.getVisibility();
    }
    return DEFAULT_VISIBILITY;
  }

}
