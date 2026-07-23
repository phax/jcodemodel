/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2026 Philip Helger + contributors
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
package com.helger.jcodemodel.writer;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.util.NullWriter;
import com.helger.jcodemodel.writer.JFormatter.FormatterContext;
import com.helger.jcodemodel.writer.JFormatter.IWriteContext;

public class JFormatterTest {

  @Test
  public void testSizeWithTabsExpanded() {
    Assert.assertEquals(2, JFormatter.sizeWithTabsExpanded("\t", 2));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("\t", 4));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("a\t", 4));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("ab\t", 4));
    Assert.assertEquals(4, JFormatter.sizeWithTabsExpanded("abc\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcd\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcde\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcdef\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("abcdefg\t", 4));
    Assert.assertEquals(12, JFormatter.sizeWithTabsExpanded("abcdefgh\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("\t\t", 4));
    Assert.assertEquals(8, JFormatter.sizeWithTabsExpanded("\tab\t", 4));
  }

  @Test
  public void testContexts() {
    FormatterSettings settings = new FormatterSettings();
    settings.indent.useSpaces(1);
    try (JFormatter test =
        new JFormatter(new SourcePrintWriter(NullWriter.getInstance(), JCMWriter.DEFAULT_NEW_LINE), settings);) {
      IWriteContext root = test.asContext;

      Assert.assertEquals(0, test.indentLevel());
      Assert.assertEquals(0, root.getIndentLevel());
      Assert.assertEquals("", root.getCurrentLine());
      Assert.assertEquals(0, root.getLastChar());

      test.print("test");
      Assert.assertEquals(0, test.indentLevel());
      Assert.assertEquals(0, root.getIndentLevel());
      Assert.assertEquals("test", root.getCurrentLine());
      Assert.assertEquals('t', root.getLastChar());

      test.indent(2);
      Assert.assertEquals(2, test.indentLevel());
      Assert.assertEquals(2, root.getIndentLevel());
      Assert.assertEquals("test", root.getCurrentLine());
      Assert.assertEquals('t', root.getLastChar());

      test.newline();
      Assert.assertEquals(2, test.indentLevel());
      Assert.assertEquals(2, root.getIndentLevel());
      Assert.assertEquals("", root.getCurrentLine());
      Assert.assertEquals(0, root.getLastChar());

      test.print('o');
      Assert.assertEquals(2, test.indentLevel());
      Assert.assertEquals(2, root.getIndentLevel());
      Assert.assertEquals("  o", root.getCurrentLine());
      Assert.assertEquals('o', root.getLastChar());

      test.outdent(1);
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals("  o", root.getCurrentLine());
      Assert.assertEquals('o', root.getLastChar());

      test.newline();
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals("", root.getCurrentLine());
      Assert.assertEquals(0, root.getLastChar());

      test.print("sterone");
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());

      FormatterContext layer = (FormatterContext) test.addContextLayer();
      Assert.assertEquals(1, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(1, layer.getIndentLevel());
      Assert.assertEquals(" sterone", layer.getCurrentLine());
      Assert.assertEquals('e', layer.getLastChar());

      test.indent(2);
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(3, layer.getIndentLevel());
      Assert.assertEquals(" sterone", layer.getCurrentLine());
      Assert.assertEquals('e', layer.getLastChar());

      test.newline();
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(3, layer.getIndentLevel());
      Assert.assertEquals("", layer.getCurrentLine());
      Assert.assertEquals(0, layer.getLastChar());

      test.print("oid");
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(1, root.getIndentLevel());
      Assert.assertEquals(" sterone", root.getCurrentLine());
      Assert.assertEquals('e', root.getLastChar());
      Assert.assertEquals(3, layer.getIndentLevel());
      Assert.assertEquals("   oid", layer.getCurrentLine());
      Assert.assertEquals('d', layer.getLastChar());

      layer.commit();
      Assert.assertEquals(3, test.indentLevel());
      Assert.assertEquals(3, root.getIndentLevel());
      Assert.assertEquals("   oid", root.getCurrentLine());
      Assert.assertEquals('d', root.getLastChar());

    }

  }

}
