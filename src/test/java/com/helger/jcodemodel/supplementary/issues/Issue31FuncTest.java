package com.helger.jcodemodel.supplementary.issues;

import org.junit.Test;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.supplementary.issues.issue31.AbstractFieldInstanceImpl;
import com.helger.jcodemodel.supplementary.issues.issue31.AbstractFieldInstanceImpl.ValueHolderInstanceImpl;
import com.helger.jcodemodel.util.CodeModelTestsHelper;

/**
 * Test for https://github.com/phax/jcodemodel/issues/31
 *
 * @author Philip Helger
 */
public final class Issue31FuncTest
{
  @Test
  public void test () throws Exception
  {
    final JCodeModel generator = new JCodeModel ();

    final AbstractJClass jtype = generator.ref (String.class);
    final AbstractJClass aspect = generator.ref (ValueHolderInstanceImpl.class);
    final AbstractJClass abstractFieldClass = generator.ref (AbstractFieldInstanceImpl.class).narrow (jtype);
    final JDefinedClass basefield = generator.anonymousClass (abstractFieldClass);
    final JFieldVar apectfield = basefield.field (JMod.PRIVATE, aspect, "valueHolder");
    final JMethod initfield = basefield.method (JMod.PROTECTED, generator.VOID, "initialize");
    initfield.body ().assign (apectfield, JExpr._new (aspect).arg (jtype.dotclass ()).arg (JExpr._null ()));

    final JDefinedClass cls = generator._class (JMod.PUBLIC, "TestClass1Impl");
    final JMethod m = cls.method (JMod.PUBLIC, generator.VOID, "foo");
    m.body ().decl (abstractFieldClass, "_testField", JExpr._new (basefield));

    CodeModelTestsHelper.parseCodeModel (generator);
  }
}
