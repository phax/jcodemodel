package com.helger.jcodemodel.plugin.maven.generators;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.helger.jcodemodel.plugin.maven.CodeModelBuilder;

/**
 * marker that a class is a codemodel generator. Only one should be present per
 * maven module, and only applied to {@link CodeModelBuilder} implementing
 * class.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.SOURCE)
public @interface JCMGen {

}
