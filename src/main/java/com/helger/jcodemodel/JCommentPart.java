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
package com.helger.jcodemodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * A part is a part of a javadoc comment, and it is a list of values.
 * <p>
 * A part can contain a free-form text. This text is modeled as a collection of
 * 'values' in this class. A value can be a {@link AbstractJType} (which will be
 * prinited with a @link tag), anything that can be turned into a {@link String}
 * via the {@link Object#toString()} method, or a {@link Collection}/array of
 * those objects.
 * <p>
 * Values can be added through the various append methods one by one or in a
 * bulk.
 *
 * @author Kohsuke Kawaguchi
 */
public class JCommentPart extends ArrayList <Object>
{
  public JCommentPart ()
  {}

  /**
   * Appends a new value.
   *
   * @param aValue
   *        If the value is {@link AbstractJType} it will be printed as a @link
   *        tag. Otherwise it will be converted to String via
   *        {@link Object#toString()}.
   * @return this for chaining
   */
  @Nonnull
  public JCommentPart append (@Nullable final Object aValue)
  {
    add (aValue);
    return this;
  }

  @Override
  public boolean add (@Nullable final Object aValue)
  {
    return _flattenAppend (aValue);
  }

  @Nullable
  public static String getHTMLEscaped (@Nullable final String sValue)
  {
    if (sValue == null)
      return null;
    if (sValue.length () == 0)
      return "";

    final StringBuilder aSB = new StringBuilder (sValue.length () * 2);
    for (final char c : sValue.toCharArray ())
      if (c == '&')
        aSB.append ("&amp;");
      else
        if (c == '<')
          aSB.append ("&lt;");
        else
          if (c == '>')
            aSB.append ("&gt;");
          else
            if (c == '"')
              aSB.append ("&quot;");
            else
              aSB.append (c);
    return aSB.toString ();
  }

  /**
   * Add the provided value, but before correct HTML character masking ("&lt;"
   * becomes "&amp;lt;" etc.).
   *
   * @param sValue
   *        Source value. May be <code>null</code>.
   * @return <code>true</code> if added, <code>false</code> if the source is
   *         <code>null</code>.
   */
  public boolean addMasked (@Nullable final String sValue)
  {
    final String sMasked = getHTMLEscaped (sValue);
    if (sMasked == null)
      return false;
    return super.add (sMasked);
  }

  private boolean _flattenAppend (@Nullable final Object aValue)
  {
    if (aValue == null)
      return false;

    boolean bAny = false;
    if (aValue instanceof Object [])
    {
      for (final Object o : (Object []) aValue)
        if (_flattenAppend (o))
          bAny = true;
    }
    else
      if (aValue instanceof Collection <?>)
      {
        for (final Object o : (Collection <?>) aValue)
          if (_flattenAppend (o))
            bAny = true;
      }
      else
      {
        // Only String and AbstractJType are allowed
        if (aValue instanceof String || aValue instanceof AbstractJType)
          bAny = super.add (aValue);
        else
          throw new IllegalArgumentException ("Value is of an unsupported type: " + aValue.getClass ().toString ());
      }
    return bAny;
  }

  /**
   * Writes this part into the formatter by using the specified indentation.
   *
   * @param f
   *        Formatter to use
   * @param sIndent
   *        Indentation to use
   */
  protected void format (@Nonnull final IJFormatter f, final String sIndent)
  {
    if (!f.isPrinting ())
    {
      // quickly pass the types to IJFormatter, as that's all we care.
      // we don't need to worry about the exact formatting of text.
      for (final Object o : this)
        if (o instanceof AbstractJClass)
          f.generable ((AbstractJClass) o);
      return;
    }

    if (!isEmpty ())
      f.print (sIndent);

    final Iterator <Object> itr = iterator ();
    while (itr.hasNext ())
    {
      final Object o = itr.next ();

      if (o instanceof String)
      {
        int idx;
        String sStr = (String) o;
        while ((idx = sStr.indexOf ('\n')) != -1)
        {
          final String line = sStr.substring (0, idx);
          if (line.length () > 0)
            f.print (_escape (line));
          sStr = sStr.substring (idx + 1);
          f.newline ().print (sIndent);
        }
        if (sStr.length () != 0)
          f.print (_escape (sStr));
      }
      else
        if (o instanceof AbstractJClass)
        {
          // TODO: this doesn't print the parameterized type properly
          ((AbstractJClass) o).printLink (f);
        }
        else
          if (o instanceof AbstractJType)
          {
            f.generable ((AbstractJType) o);
          }
          else
            throw new IllegalStateException ("Invalid type present: " + o);
    }

    if (!isEmpty ())
      f.newline ();
  }

  /**
   * Escapes the appearance of the comment terminator "*" followed by "/". Note:
   * quotes etc are not masked!
   *
   * @param sText
   *        Source text. May not be <code>null</code>.
   * @return Escaped text
   */
  @Nonnull
  private static String _escape (@Nonnull final String sText)
  {
    String s = sText;
    while (true)
    {
      final int nIdx = s.indexOf ("*/");
      if (nIdx < 0)
        break;
      // Put an empty comment between "*" and "/"
      s = s.substring (0, nIdx + 1) + "<!---->" + s.substring (nIdx + 1);
    }
    return s;
  }
}
