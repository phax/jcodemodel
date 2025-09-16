package com.helger.jcodemodel.plugin.maven;

import java.io.InputStream;
import java.util.Map;

import org.apache.maven.project.MavenProject;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.exceptions.JCodeModelException;

/**
 * implementation of a jcodemodel builder (AKA generator)
 */
public interface CodeModelBuilder {

  /**
   * called by the plugin after creating the generator, with the plugin "params"
   * configuration. Override to handle generator-specific parameters
   */
  default void configure(Map<String, String> params) {
  }

  /**
   * asking the generator to build a model.
   *
   * @param model   the model to build into.
   * @param source  inputstream deduced by the plugin
   * @throws JCodeModelException
   */
  void build(JCodeModel model, InputStream source) throws JCodeModelException;

  /**
   * shortcut to {@link #build(JCodeModel, MavenProject, InputStream)} with null
   * values.
   */
  default void build(JCodeModel model) throws JCodeModelException {
    build(model, null);
  }

  void setRootPackage(String rootPackage);

  String getRootPackage();

  /**
   * @param localPath class we want to create, eg "pck.MyClass"
   * @return localpath prefixed by rootpackage and "." if needed.
   */
  default String expandClassName(String localPath) {
    String rootPackage = getRootPackage();
    return rootPackage == null || rootPackage.isBlank() ? localPath : rootPackage + "." + localPath;
  }

}
