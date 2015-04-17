/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2015 Philip Helger
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://glassfish.dev.java.net/public/CDDL+GPL_1_1.html
 * or packager/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at packager/legal/LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package com.helger.jcodemodel;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.Test;

import com.helger.jcodemodel.IJAnnotationWriter;
import com.helger.jcodemodel.JAnnotationArrayMember;
import com.helger.jcodemodel.JAnnotationUse;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JEnumConstant;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;

/**
 * A test program for the annotation use features Note: Not all the generated
 * code would make sense but just checking in all the different ways you can use
 * an annotation
 *
 * @author Bhakti Mehta
 */
public final class AnnotationUseTest
{
  @interface XmlElement
  {
    String value();

    String ns();
  }

  interface XmlElementW extends IJAnnotationWriter <XmlElement>
  {
    XmlElementW value (String s);

    XmlElementW ns (String s);
  }

  /**
   * *********************************************************************
   * Generates this
   * **********************************************************************
   *
   * <pre>
   * import java.lang.annotation.Retention;
   * import java.lang.annotation.RetentionPolicy;
   * import java.lang.annotation.Target;
   * import com.helger.jcodemodel.tests.AnnotationUseTest;
   * 
   * &#064;Retention (value = Test.Iamenum.GOOD, value1 = RetentionPolicy.RUNTIME)
   * &#064;AnnotationUseTest.XmlElement (ns = &quot;##default&quot;, value = &quot;foobar&quot;)
   * public class Test
   * {
   *   &#064;Retention (name = &quot;book&quot;,
   *               targetNamespace = 5,
   *               names = { &quot;Bob&quot;, &quot;Rob&quot;, &quot;Ted&quot; },
   *               namesno = { 4, 5, 6 },
   *               values = { @Target (type = java.lang.Integer.class), @Target (type = java.lang.Float.class) },
   *               foo = @Target (junk = 7))
   *   private double y;
   * 
   *   public void foo ()
   *   {}
   * 
   *   public enum Iamenum
   *   {
   *     GOOD,
   *     BAD;
   *   }
   * }
   * 
   *
   *
   * </pre>
   */
  @Test
  public void testMain () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final JDefinedClass cls = cm._class ("Test");
    // JMethod m =
    cls.method (JMod.PUBLIC, cm.VOID, "foo");

    // Annotating a class
    // Using the existing Annotations from java.lang.annotation package
    final JAnnotationUse use = cls.annotate (cm.ref (Retention.class));

    // declaring an enum class and an enumconstant as a membervaluepair
    final JDefinedClass enumcls = cls._enum ("Iamenum");
    final JEnumConstant ec = enumcls.enumConstant ("GOOD");
    final JEnumConstant ec1 = enumcls.enumConstant ("BAD");
    final JEnumConstant ec2 = enumcls.enumConstant ("BAD");
    ec1.equals (ec2);

    use.param ("value", ec);
    // adding another param as an enum
    use.param ("value1", RetentionPolicy.RUNTIME);

    // Adding annotation for fields
    // will generate like
    // @String(name = "book") private double y;
    //
    final JFieldVar field = cls.field (JMod.PRIVATE, cm.DOUBLE, "y");

    // Adding more annotations which are member value pairs
    final JAnnotationUse aUse = field.annotate (Retention.class);
    aUse.param ("name", "book");
    aUse.param ("targetNamespace", 5);

    // Adding arrays as member value pairs
    final JAnnotationArrayMember arrayMember = aUse.paramArray ("names");
    arrayMember.param ("Bob");
    arrayMember.param ("Rob");
    arrayMember.param ("Ted");

    // Shortcut
    aUse.paramArray ("namesno", 4, 5, 6);

    final JAnnotationArrayMember arrayMember2 = aUse.paramArray ("values");
    // adding an annotation as a member value pair
    arrayMember2.annotate (Target.class).param ("type", Integer.class);
    arrayMember2.annotate (Target.class).param ("type", Float.class);

    // test typed annotation writer
    final XmlElementW w = cls.annotate2 (XmlElementW.class);
    w.ns ("##default").value ("foobar");

    // adding an annotation as a member value pair
    final JAnnotationUse myuse = aUse.annotationParam ("foo", Target.class);
    myuse.param ("junk", 7);

    cm.build (new SingleStreamCodeWriter (System.out));
  }
}
