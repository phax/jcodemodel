/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 1997-2010 Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright 2013-2014 Philip Helger
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

  private final JCodeModel _owner;

  /** list of @param tags */
  private final Map <String, JCommentPart> _atParams = new LinkedHashMap <String, JCommentPart> ();

  /** list of xdoclets */
  private final Map <String, Map <String, String>> _atXdoclets = new LinkedHashMap <String, Map <String, String>> ();

  /** list of @throws tags */
  private final Map <AbstractJClass, JCommentPart> _atThrows = new LinkedHashMap <AbstractJClass, JCommentPart> ();

  /**
   * The @return tag part.
   */
  private JCommentPart _atReturn = null;

  /**
   * The @author tag part.
   */
  private JCommentPart _atAuthor = null;

  /** The @deprecated tag */
  private JCommentPart _atDeprecated = null;

  protected JDocComment (@Nonnull final JCodeModel owner)
  {
    this._owner = owner;
  }

  @Nonnull
  public JCodeModel owner ()
  {
    return _owner;
  }

  @Override
  public JDocComment append (@Nullable final Object o)
  {
    add (o);
    return this;
  }

  /**
   * Append a text to a @param tag to the javadoc
   */
  @Nonnull
  public JCommentPart addParam (final String param)
  {
    JCommentPart p = _atParams.get (param);
    if (p == null)
    {
      p = new JCommentPart ();
      _atParams.put (param, p);
    }
    return p;
  }

  /**
   * Append a text to an @param tag.
   */
  public JCommentPart addParam (@Nonnull final JVar param)
  {
    return addParam (param.name ());
  }

  @Nullable
  public JCommentPart removeParam (final String param)
  {
    return _atParams.remove (param);
  }

  @Nullable
  public JCommentPart removeParam (@Nonnull final JVar param)
  {
    return removeParam (param.name ());
  }

  public void removeAllParams ()
  {
    _atParams.clear ();
  }

  @Nullable
  public JCommentPart getParam (@Nullable final String param)
  {
    return _atParams.get (param);
  }

  @Nullable
  public JCommentPart getParam (@Nonnull final JVar param)
  {
    return getParam (param.name ());
  }

  /**
   * add an @throws tag to the javadoc
   */
  public JCommentPart addThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return addThrows (_owner.ref (exception));
  }

  /**
   * add an @throws tag to the javadoc
   */
  public JCommentPart addThrows (final AbstractJClass exception)
  {
    JCommentPart p = _atThrows.get (exception);
    if (p == null)
    {
      p = new JCommentPart ();
      _atThrows.put (exception, p);
    }
    return p;
  }

  @Nullable
  public JCommentPart removeThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return removeThrows (_owner.ref (exception));
  }

  @Nullable
  public JCommentPart removeThrows (final AbstractJClass exception)
  {
    return _atThrows.remove (exception);
  }

  public void removeAllThrows ()
  {
    _atThrows.clear ();
  }

  @Nullable
  public JCommentPart getThrows (@Nonnull final Class <? extends Throwable> exception)
  {
    return getThrows (_owner.ref (exception));
  }

  @Nullable
  public JCommentPart getThrows (final AbstractJClass exception)
  {
    return _atThrows.get (exception);
  }

  /**
   * Appends a text to @return tag.
   */
  @Nonnull
  public JCommentPart addReturn ()
  {
    if (_atReturn == null)
      _atReturn = new JCommentPart ();
    return _atReturn;
  }

  public void removeReturn ()
  {
    _atReturn = null;
  }

  /**
   * Appends a text to @author tag.
   */
  @Nonnull
  public JCommentPart addAuthor ()
  {
    if (_atAuthor == null)
      _atAuthor = new JCommentPart ();
    return _atAuthor;
  }

  public void removeAuthor ()
  {
    _atAuthor = null;
  }

  /**
   * add an @deprecated tag to the javadoc, with the associated message.
   */
  @Nonnull
  public JCommentPart addDeprecated ()
  {
    if (_atDeprecated == null)
      _atDeprecated = new JCommentPart ();
    return _atDeprecated;
  }

  public void removeDeprecated ()
  {
    _atDeprecated = null;
  }

  /**
   * add an xdoclet.
   */
  @Nonnull
  public Map <String, String> addXdoclet (final String name)
  {
    Map <String, String> p = _atXdoclets.get (name);
    if (p == null)
    {
      p = new LinkedHashMap <String, String> ();
      _atXdoclets.put (name, p);
    }
    return p;
  }

  /**
   * add an xdoclet.
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
    return _atXdoclets.remove (name);
  }

  public void removeAllXdoclets ()
  {
    _atXdoclets.clear ();
  }

  public void generate (@Nonnull final JFormatter f)
  {
    // I realized that we can't use StringTokenizer because
    // this will recognize multiple \n as one token.

    f.print ("/**").newline ();

    format (f, " * ");

    f.print (" * ").newline ();
    for (final Map.Entry <String, JCommentPart> e : _atParams.entrySet ())
    {
      f.print (" * @param ").print (e.getKey ()).newline ();
      e.getValue ().format (f, INDENT);
    }
    if (_atReturn != null)
    {
      f.print (" * @return").newline ();
      _atReturn.format (f, INDENT);
    }
    if (_atAuthor != null)
    {
      f.print (" * @author").newline ();
      _atAuthor.format (f, INDENT);
    }
    for (final Map.Entry <AbstractJClass, JCommentPart> e : _atThrows.entrySet ())
    {
      f.print (" * @throws ").type (e.getKey ()).newline ();
      e.getValue ().format (f, INDENT);
    }
    if (_atDeprecated != null)
    {
      f.print (" * @deprecated").newline ();
      _atDeprecated.format (f, INDENT);
    }
    for (final Map.Entry <String, Map <String, String>> e : _atXdoclets.entrySet ())
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
