package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import com.helger.jcodemodel.JMod;

public enum FieldVisibility {

  PUBLIC(JMod.PUBLIC), PRIVATE(JMod.PRIVATE), PROTECTED(JMod.PROTECTED), PACKAGE(JMod.PROTECTED);

  public final int jmod;

  FieldVisibility(int jmod) {
    this.jmod = jmod;
  }

  public void apply(FieldOptions opt) {
    opt.setVisibility(this);
  }

  public static FieldVisibility of(String value) {
    if(value==null || value.isBlank()) {
      return null;
    }
    return switch(value.toLowerCase()) {
    case "public", "all" -> PUBLIC;
    case "private", "prv" -> PRIVATE;
    case "protected", "prt" -> PROTECTED;
    case "package", "packaged", "pck" -> PACKAGE;
    default-> null;
    };
  }

}
