package com.helger.jcodemodel.preprocess;

import com.helger.jcodemodel.JCodeModel;

/**
 * A preprocessor adds data in a JCodeModel before it is built.<br />
 * <p>
 * This is typically usefull for, but not limited to:
 * </p>
 *
 *
 * <p>
 * the typical use case is
 * <ol>
 * <li>During JCM modeling, the user requests the preprocessor with
 * JCM::preprocessor(preprocessorclass). This preprocessor instance is unique in
 * the JCM for that preprocessor class.</li>
 * <li>the preprocessor is used during the JCM modeling, eg by tagging classes,
 * fields, packages, etc with it eg myprocessor.add(myJCMClass)</li>
 * <li>when the user wants to build the JCM, the JCM calls each preprocessor
 * that has been requested, which can then modify the JCM</li>
 * <li>if several processors are requested and at least one modified the JCM
 * when applied, each processor is applied again, until none generates a
 * modification anymore</li>
 * </ol>
 * </p>
 *
 * <p>
 * A preprocessor class must be have an unparametrized constructor. The settings
 * are set after initialization.
 * </p>
 *
 * @author glelouet
 *
 */
public abstract class AbstractJCodePreprocessor
{

  /**
   *
   * @param jcm
   *        the {@link JCodeModel} we want to apply the processor onto.
   * @param firstPass
   *        true when the processor has not bee applied to the jcm already.
   *        Typically that means them odifications the processor wanted to do
   *        have already been applied, by itself.
   * @return true if the application of the processor modified the jcm.
   */
  public abstract boolean apply (JCodeModel jcm, boolean firstPass) throws PreprocessException;

}
