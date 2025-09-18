package com.helger.jcodemodel.plugin.maven.generators.flatstruct.canners.reference;

import java.lang.ref.WeakReference;

import com.helger.jcodemodel.plugin.maven.generators.flatstruct.canners.ARefCanner;

public class WeakReferenceCanner extends ARefCanner {

  public WeakReferenceCanner() {
    super(WeakReference.class);
  }

}
