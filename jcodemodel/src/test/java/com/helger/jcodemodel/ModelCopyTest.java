/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2025 Philip Helger + contributors
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.junit.Test;

import com.helger.jcodemodel.exceptions.JCodeModelException;
import com.helger.jcodemodel.writer.StringCodeWriter;

import jakarta.annotation.Nonnull;

/**
 * abstract method for a test of copying. Such a test consist in creating a source codemodel,
 * copying it, check the equality of the representation of the copy and the source ; then apply
 * several modifications on the source, each result in a model varying from the copy ; then apply
 * those modifications to the copy, which in the end should have the same representation as the
 * source.<br />
 * This class defines
 * <ul>
 * <li>{@link #execute()} that should be called in the test,</li>
 * <li>{@link #createCM()} that creates a base code model,</li>
 * <li>{@link #copy(JCodeModel)} that defines how to copy a {@link JCodeModel},</li>
 * <li>{@link #represent(JCodeModel)} to make a representation of a codemodel,</li>
 * <li>{@link #modifications()} that lists a series of modifications to apply.</li>
 * </ul>
 *
 * @author glelouet
 */
public class ModelCopyTest
{
  @Test
  public void execute ()
  {
    final JCodeModel cm = createCM ();
    final JCodeModel copy = copy (cm);
    assertEquals (represent (cm), represent (copy));
    for (final Consumer <JCodeModel> m : modifications ())
    {
      m.accept (cm);
      assertNotEquals (represent (cm), represent (copy));
    }
    for (final Consumer <JCodeModel> m : modifications ())
      m.accept (copy);
    assertEquals (represent (cm), represent (copy));
  }

  protected JCodeModel createCM ()
  {
    try
    {
      final JCodeModel ret = new JCodeModel ();

      // create an interface IMyClass with method default String string(){return "yes";}
      final JDefinedClass itf = ret._class ("my.pckg.IMyClass", EClassType.INTERFACE);
      final JMethod methd = itf.method (JMod.PUBLIC | JMod.DEFAULT, ret.ref (String.class), "string");
      methd.body ()._return (JExpr.lit ("yes"));

      // create an implementation of that interface, for which toString() returns the string();
      final JDefinedClass imp = itf._package ()._class (JMod.PUBLIC, "Impl")._implements (itf);
      imp.method (JMod.PUBLIC, ret.ref (String.class), "toString").body ()._return (JExpr.invoke (methd));
      return ret;
    }
    catch (final JCodeModelException e)
    {
      throw new UnsupportedOperationException (e);
    }
  }

  @Nonnull
  protected JCodeModel copy (@Nonnull final JCodeModel source)
  {
    return source.copy ();
  }

  @Nonnull
  protected String represent (@Nonnull final JCodeModel target)
  {
    return StringCodeWriter.represent (target);
  }

  @Nonnull
  public List <Consumer <JCodeModel>> modifications ()
  {
    return Arrays.asList (cm -> {
      try
      {
        cm._class (JMod.PUBLIC, "MyClass");
      }
      catch (final JCodeModelException e)
      {
        throw new UnsupportedOperationException (e);
      }
    }, cm -> {
      final JDefinedClass cl = cm._getClass ("my.pckg.IMyClass");
      cl.method (JMod.DEFAULT, cm.BOOLEAN, "yes").body ()._return (JExpr.TRUE);
    }, cm -> {
      try
      {
        final JDefinedClass cl = cm._getClass ("my.pckg.IMyClass");
        final JDefinedClass newcl = cl._class (JMod.STATIC | JMod.PUBLIC, "InternalIntClass");
        final JFieldVar fld = newcl.field (JMod.PRIVATE, cm.INT, "intField");
        final JMethod cons = newcl.constructor (JMod.PUBLIC);
        final JVar consParam = cons.param (cm.INT, "number");
        cons.body ().assign (fld, consParam);
        newcl.method (JMod.PUBLIC, cm.INT, "incr").body ()._return (JExpr.preincr (fld));
      }
      catch (final JCodeModelException e)
      {
        throw new UnsupportedOperationException (e);
      }
    });
  }

}
