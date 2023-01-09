/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2020 Philip Helger + contributors
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
package com.helger.jcodemodel.supplementary.issues;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.fmt.JTextFile;
import com.helger.jcodemodel.writer.JCMWriter;
import com.helger.jcodemodel.writer.SingleStreamCodeWriter;

/**
 * Test for https://github.com/phax/jcodemodel/issues/61
 *
 * @author Flavio Baronti
 */
public class Issue61FuncTest
{
  @Test
  public void testIssue () throws Exception
  {
    final JCodeModel cm = new JCodeModel ();
    final Charset ascii = StandardCharsets.US_ASCII;
    final JTextFile res = cm.rootResourceDir ().addResourceFile (new JTextFile ("example.txt", ascii));

    res.setContents ("Testing");

    final ByteArrayOutputStream resOut = new ByteArrayOutputStream ();
    new JCMWriter (cm).build (new SingleStreamCodeWriter (new ByteArrayOutputStream ()),
                              new SingleStreamCodeWriter (resOut));
    final String txtRes = ascii.decode (ByteBuffer.wrap (resOut.toByteArray ())).toString ();
    assertTrue (txtRes.contains ("Testing"));
  }
}
