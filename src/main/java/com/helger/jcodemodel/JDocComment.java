/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2016 Philip Helger + contributors
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
 * (@param), the return part (@return), and the throws parts (@throws). TODO: it
 * would be nice if we have JComment class and we can derive this class from
 * there.
 */
public class JDocComment extends JCommentPart implements IJGenerable, IJOwned
{
  private static final String INDENT = " *     ";

  private static final long serialVersionUID = 1L;

  private final JCodeModel m_aOwner;

  /** list of @param tags */
  private final Map <String, JCommentPart> m_aAtParams = new LinkedHashMap <String, JCommentPart> ();

  /** list of xdoclets */
  private final Map <String, Map <String, String>> m_aAtXdoclets = new LinkedHashMap <String, Map <String, String>> ();

  /** list of @throws tags */
  private final Map <AbstractJClass, JCommentPart> m_aAtThrows = new LinkedHashMap <AbstractJClass, JCommentPart> ();

  /**
   * The @return tag part.
   */
  private JCommentPart m_aAtReturn;

  /**
   * The @author tag part.
   */
  private JCommentPart m_aAtAuthor;

  /** The @deprecated tag */
  private JCommentPart m_aAtDeprecated;

  protected JDocComment (@Nonnull final JCodeModel owner)
  {
    m_aOwner = JCValueEnforcer.notNull (owner, "Owner");
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return m_aOwner;
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
   * @param param
   *        Parameter to be added
   * @return The created {@link JCommentPart}
   */
  @Nonnull
  public JCommentPart addParam (final String param)
  {
    JCommentPart p = m_aAtParams.get (param);
    if (p == null)
    {
      p = new JCommentPart ();
      m_aAtParams.put (param, p);
    }
    return p;
  }

  /**
   * Append a text to an @param tag.
   *
   * @param param
   *        Parameter to be added
   * @return The created {@link JCommentPart}
   */
  public JCommentPart addParam (@Nonnull final JVar param)
  {
    return addParam (param.name ());
  }

  @Nullable
  public JCommentPart removeParam (final String param)
  {
    return m_aAtParams.remove (param);
  }

  @Nullable
  public JCommentPart removeParam (@Nonnull final JVar param)
  {
    return removeParam (param.name ());
  }

  public void removeAllParams ()
  {
    m_aAtParams.clear ();
  }

  @Nullable
  public JCommentPart getParam (@Nullable final String param)
  {
    return m_aAtParams.get (param);
  }

  @Nullable
  public JCommentPart getParam (@Nonnull final JVar param)
  {
    return getParam (param.name ());
  }

  /**
   * add an @throws tag to the javadoc
   *
   * @param exception
   *        Exception to be added
   * @return New {@link JCommentPart}
   */
  public JCommentPart addThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return addThrows (m_aOwner.ref (exception));
  }

  /**
   * add an @throws tag to the javadoc
   *
   * @param exception
   *        Exception to be added
   * @return New {@link JCommentPart}
   */
  public JCommentPart addThrows (final AbstractJClass exception)
  {
    JCommentPart p = m_aAtThrows.get (exception);
    if (p == null)
    {
      p = new JCommentPart ();
      m_aAtThrows.put (exception, p);
    }
    return p;
  }

  @Nullable
  public JCommentPart removeThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return removeThrows (m_aOwner.ref (exception));
  }

  @Nullable
  public JCommentPart removeThrows (final AbstractJClass exception)
  {
    return m_aAtThrows.remove (exception);
  }

  public void removeAllThrows ()
  {
    m_aAtThrows.clear ();
  }

  @Nullable
  public JCommentPart getThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return getThrows (m_aOwner.ref (exception));
  }

  @Nullable
  public JCommentPart getThrows (final AbstractJClass exception)
  {
    return m_aAtThrows.get (exception);
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

  public void removeReturn ()
  {
    m_aAtReturn = null;
  }

  /**
   * Appends a text to @author tag.
   *
   * @return Always the same {@link JCommentPart}
   */
  @Nonnull
  public JCommentPart addAuthor ()
  {
    if (m_aAtAuthor == null)
      m_aAtAuthor = new JCommentPart ();
    return m_aAtAuthor;
  }

  public void removeAuthor ()
  {
    m_aAtAuthor = null;
  }

  /**
   * add an @deprecated tag to the javadoc, with the associated message.
   *
   * @return Always the same {@link JCommentPart}
   */
  @Nonnull
  public JCommentPart addDeprecated ()
  {
    if (m_aAtDeprecated == null)
      m_aAtDeprecated = new JCommentPart ();
    return m_aAtDeprecated;
  }

  public void removeDeprecated ()
  {
    m_aAtDeprecated = null;
  }

  /**
   * add an xdoclet.
   *
   * @param name
   *        xdoclet name
   * @return Map with the key/value pairs
   */
  @Nonnull
  public Map <String, String> addXdoclet (final String name)
  {
    Map <String, String> p = m_aAtXdoclets.get (name);
    if (p == null)
    {
      p = new LinkedHashMap <String, String> ();
      m_aAtXdoclets.put (name, p);
    }
    return p;
  }

  /**
   * add an xdoclet.
   *
   * @param name
   *        xdoclet name
   * @param attributes
   *        Attributes to be added
   * @return Map with the key/value pairs
   */
  @Nonnull
  public Map <String, String> addXdoclet (final String name, final Map <String, String> attributes)
  {
    final Map <String, String> p = addXdoclet (name);
    p.putAll (attributes);
    return p;
  }

  /**
   * add an xdoclet.
   *
   * @param name
   *        xdoclet name
   * @param attribute
   *        Attribute name to be added
   * @param value
   *        Attribute value to be added
   * @return Map with the key/value pairs
   */
  @Nonnull
  public Map <String, String> addXdoclet (final String name, final String attribute, final String value)
  {
    final Map <String, String> p = addXdoclet (name);
    p.put (attribute, value);
    return p;
  }

  @Nullable
  public Map <String, String> removeXdoclet (final String name)
  {
    return m_aAtXdoclets.remove (name);
  }

  public void removeAllXdoclets ()
  {
    m_aAtXdoclets.clear ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    // I realized that we can't use StringTokenizer because
    // this will recognize multiple \n as one token.

    f.print ("/**").newline ();

    format (f, " * ");

    f.print (" * ").newline ();
    for (final Map.Entry <String, JCommentPart> e : m_aAtParams.entrySet ())
    {
      f.print (" * @param ").print (e.getKey ()).newline ();
      e.getValue ().format (f, INDENT);
    }
    if (m_aAtReturn != null)
    {
      f.print (" * @return").newline ();
      m_aAtReturn.format (f, INDENT);
    }
    if (m_aAtAuthor != null)
    {
      f.print (" * @author").newline ();
      m_aAtAuthor.format (f, INDENT);
    }
    for (final Map.Entry <AbstractJClass, JCommentPart> e : m_aAtThrows.entrySet ())
    {
      f.print (" * @throws ").type (e.getKey ()).newline ();
      e.getValue ().format (f, INDENT);
    }
    if (m_aAtDeprecated != null)
    {
      f.print (" * @deprecated").newline ();
      m_aAtDeprecated.format (f, INDENT);
    }
    for (final Map.Entry <String, Map <String, String>> e : m_aAtXdoclets.entrySet ())
    {
      f.print (" * @").print (e.getKey ());
      if (e.getValue () != null)
      {
        for (final Map.Entry <String, String> a : e.getValue ().entrySet ())
        {
          f.print (" ").print (a.getKey ()).print ("= \"").print (a.getValue ()).print ("\"");
        }
      }
      f.newline ();
    }
    f.print (" */").newline ();
  }
}
