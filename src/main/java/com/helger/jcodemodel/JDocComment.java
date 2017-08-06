/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2017 Philip Helger + contributors
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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCValueEnforcer;

/**
 * JavaDoc comment.
 * <p>
 * A javadoc comment consists of multiple parts. There's the main part (that
 * comes the first in in the comment section), then the parameter parts
 * (@param), the return part (@return), and the throws parts (@throws).<br>
 * TODO: it would be nice if we have JComment class and we can derive this class
 * from there.
 */
public class JDocComment extends JCommentPart implements IJGenerable, IJOwned
{
  public static final String TAG_AUTHOR = "author";
  public static final String TAG_DEPRECATED = "deprecated";
  public static final String TAG_SEE = "see";
  public static final String TAG_SINCE = "since";
  public static final String TAG_VERSION = "version";

  private static final long serialVersionUID = 1L;

  private final JCodeModel m_aOwner;

  private boolean m_bIsSingleLineMode = false;

  /**
   * list of @param tags
   */
  private final Map <String, JCommentPart> m_aAtParams = new LinkedHashMap <> ();

  /**
   * The @return tag part.
   */
  private JCommentPart m_aAtReturn;

  /**
   * list of @throws tags
   */
  private final Map <AbstractJClass, JCommentPart> m_aAtThrows = new LinkedHashMap <> ();

  /**
   * Other comment tags (like @author, @deprecated, @since, @version etc.)
   */
  private final Map <String, JCommentPart> m_aAtTags = new LinkedHashMap <> ();

  /** list of generic xdoclets */
  private final Map <String, Map <String, String>> m_aAtXdoclets = new LinkedHashMap <> ();

  protected JDocComment (@Nonnull final JCodeModel owner)
  {
    m_aOwner = JCValueEnforcer.notNull (owner, "Owner");
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return m_aOwner;
  }

  /**
   * Change whether multi line comments or single line comments should be
   * emitted.
   *
   * @param bSingleLineMode
   *        <code>true</code> to enable single line mode, <code>false</code> for
   *        multi line mode (which is the default).
   * @return this for chaining
   */
  @Nonnull
  public JDocComment setSingleLineMode (final boolean bSingleLineMode)
  {
    m_bIsSingleLineMode = bSingleLineMode;
    return this;
  }

  /**
   * @return <code>true</code> if single line mode is enabled,
   *         <code>false</code> if multi line mode is enabled. Multi line mode
   *         is the default.
   */
  public boolean isSingleLineMode ()
  {
    return m_bIsSingleLineMode;
  }

  @Override
  public JDocComment append (@Nullable final Object o)
  {
    add (o);
    return this;
  }

  /**
   * Append a text to a @param tag to the javadoc
   *
   * @param sParam
   *        Parameter to be added
   * @return The created {@link JCommentPart}
   */
  @Nonnull
  public JCommentPart addParam (@Nonnull final String sParam)
  {
    return m_aAtParams.computeIfAbsent (sParam, k -> new JCommentPart ());
  }

  /**
   * Append a text to an @param tag.
   *
   * @param sParam
   *        Parameter to be added
   * @return The created {@link JCommentPart}
   */
  public JCommentPart addParam (@Nonnull final JVar sParam)
  {
    return addParam (sParam.name ());
  }

  @Nullable
  public JCommentPart removeParam (@Nullable final String sParam)
  {
    return m_aAtParams.remove (sParam);
  }

  @Nullable
  public JCommentPart removeParam (@Nonnull final JVar aParam)
  {
    return removeParam (aParam.name ());
  }

  public void removeAllParams ()
  {
    m_aAtParams.clear ();
  }

  @Nullable
  public JCommentPart getParam (@Nullable final String sParam)
  {
    return m_aAtParams.get (sParam);
  }

  @Nullable
  public JCommentPart getParam (@Nonnull final JVar aParam)
  {
    return getParam (aParam.name ());
  }

  /**
   * Appends a text to @return tag.
   *
   * @return Always the same {@link JCommentPart}
   */
  @Nonnull
  public JCommentPart addReturn ()
  {
    if (m_aAtReturn == null)
      m_aAtReturn = new JCommentPart ();
    return m_aAtReturn;
  }

  @Nullable
  public JCommentPart getReturn ()
  {
    return m_aAtReturn;
  }

  public void removeReturn ()
  {
    m_aAtReturn = null;
  }

  /**
   * add a @throws tag to the javadoc
   *
   * @param aException
   *        Exception to be added. May not be <code>null</code>.
   * @return New {@link JCommentPart}
   */
  public JCommentPart addThrows (@Nonnull final Class <? extends Throwable> aException)
  {
    return addThrows (m_aOwner.ref (aException));
  }

  /**
   * add a @throws tag to the javadoc
   *
   * @param aException
   *        Exception to be added. May not be <code>null</code>.
   * @return New {@link JCommentPart}
   */
  @Nonnull
  public JCommentPart addThrows (@Nonnull final AbstractJClass aException)
  {
    return m_aAtThrows.computeIfAbsent (aException, k -> new JCommentPart ());
  }

  @Nullable
  public JCommentPart removeThrows (@Nonnull final Class <? extends Throwable> aException)
  {
    return removeThrows (m_aOwner.ref (aException));
  }

  @Nullable
  public JCommentPart removeThrows (@Nullable final AbstractJClass aException)
  {
    return m_aAtThrows.remove (aException);
  }

  public void removeAllThrows ()
  {
    m_aAtThrows.clear ();
  }

  @Nullable
  public JCommentPart getThrows (@Nonnull final Class <? extends Throwable> aException)
  {
    return getThrows (m_aOwner.ref (aException));
  }

  @Nullable
  public JCommentPart getThrows (@Nullable final AbstractJClass aException)
  {
    return m_aAtThrows.get (aException);
  }

  @Nonnull
  public JCommentPart addTag (@Nonnull final String sName)
  {
    JCValueEnforcer.notEmpty (sName, "Name");
    return m_aAtTags.computeIfAbsent (sName, k -> new JCommentPart ());
  }

  @Nullable
  public JCommentPart removeTag (@Nullable final String sName)
  {
    return m_aAtTags.remove (sName);
  }

  @Nullable
  public JCommentPart getTag (@Nullable final String sName)
  {
    return m_aAtTags.get (sName);
  }

  /**
   * Create an @author tag.
   *
   * @return Always the same {@link JCommentPart}
   * @see #addTag(String)
   */
  @Nonnull
  public JCommentPart addAuthor ()
  {
    return addTag (TAG_AUTHOR);
  }

  public void removeAuthor ()
  {
    removeTag (TAG_AUTHOR);
  }

  /**
   * add a @deprecated tag to the javadoc, with the associated message.
   *
   * @return Always the same {@link JCommentPart}
   * @see #addTag(String)
   */
  @Nonnull
  public JCommentPart addDeprecated ()
  {
    return addTag (TAG_DEPRECATED);
  }

  public void removeDeprecated ()
  {
    removeTag (TAG_DEPRECATED);
  }

  /**
   * add an xdoclet.
   *
   * @param sName
   *        xdoclet name
   * @return Map with the key/value pairs
   */
  @Nonnull
  public Map <String, String> addXdoclet (@Nonnull final String sName)
  {
    JCValueEnforcer.notNull (sName, "Name");
    return m_aAtXdoclets.computeIfAbsent (sName, k -> new LinkedHashMap <> ());
  }

  /**
   * add an xdoclet.
   *
   * @param sName
   *        xdoclet name
   * @param aAttributes
   *        Attributes to be added
   * @return Map with the key/value pairs
   */
  @Nonnull
  public Map <String, String> addXdoclet (@Nonnull final String sName, @Nonnull final Map <String, String> aAttributes)
  {
    JCValueEnforcer.notNull (sName, "Name");
    JCValueEnforcer.notNull (aAttributes, "Attributes");
    final Map <String, String> p = addXdoclet (sName);
    p.putAll (aAttributes);
    return p;
  }

  /**
   * add an xdoclet with <code>@name attribute = "value"</code>. If value is
   * <code>null</code> than it will be <code>@name attribute</code>.
   *
   * @param sName
   *        xdoclet name
   * @param sAttribute
   *        Attribute name to be added
   * @param sValue
   *        Attribute value to be added
   * @return Map with the key/value pairs
   */
  @Nonnull
  public Map <String, String> addXdoclet (@Nonnull final String sName,
                                          @Nonnull final String sAttribute,
                                          @Nullable final String sValue)
  {
    JCValueEnforcer.notNull (sName, "Name");
    JCValueEnforcer.notNull (sAttribute, "Attribute");
    final Map <String, String> p = addXdoclet (sName);
    p.put (sAttribute, sValue);
    return p;
  }

  @Nullable
  public Map <String, String> removeXdoclet (@Nullable final String name)
  {
    return m_aAtXdoclets.remove (name);
  }

  public void removeAllXdoclets ()
  {
    m_aAtXdoclets.clear ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    // Is any "@" comment present?
    final boolean bHasAt = !m_aAtParams.isEmpty () ||
                           m_aAtReturn != null ||
                           !m_aAtThrows.isEmpty () ||
                           !m_aAtTags.isEmpty () ||
                           !m_aAtXdoclets.isEmpty ();
    if (!isEmpty () || bHasAt)
    {
      final boolean bIsJavaDoc = true;
      final String sIndent = m_bIsSingleLineMode ? "// " : " * ";
      final String sIndentLarge = sIndent + "    ";

      // Start comment
      if (!m_bIsSingleLineMode)
        f.print (bIsJavaDoc ? "/**" : "/*").newline ();

      // Print all simple text elements
      format (f, sIndent);
      if (!isEmpty () && bHasAt)
        f.print (sIndent).newline ();

      for (final Map.Entry <String, JCommentPart> aEntry : m_aAtParams.entrySet ())
      {
        f.print (sIndent + "@param ").print (aEntry.getKey ()).newline ();
        aEntry.getValue ().format (f, sIndentLarge);
      }
      if (m_aAtReturn != null)
      {
        f.print (sIndent + "@return").newline ();
        m_aAtReturn.format (f, sIndentLarge);
      }
      for (final Map.Entry <AbstractJClass, JCommentPart> aEntry : m_aAtThrows.entrySet ())
      {
        f.print (sIndent + "@throws ").type (aEntry.getKey ()).newline ();
        aEntry.getValue ().format (f, sIndentLarge);
      }
      for (final Map.Entry <String, JCommentPart> aEntry : m_aAtTags.entrySet ())
      {
        f.print (sIndent + "@" + aEntry.getKey () + " ");
        aEntry.getValue ().format (f, "");
      }
      for (final Map.Entry <String, Map <String, String>> aEntry : m_aAtXdoclets.entrySet ())
      {
        f.print (sIndent + "@").print (aEntry.getKey ());
        if (aEntry.getValue () != null)
        {
          for (final Map.Entry <String, String> aEntry2 : aEntry.getValue ().entrySet ())
          {
            final String sName = aEntry2.getKey ();
            f.print (" ").print (sName);

            // Print value only if present
            final String sValue = aEntry2.getValue ();
            if (sValue != null && sValue.length () > 0)
              f.print ("= \"").print (sValue).print ("\"");
          }
        }
        f.newline ();
      }

      // End comment
      if (!m_bIsSingleLineMode)
        f.print (" */").newline ();
    }
  }
}
