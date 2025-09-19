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
package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * list of options to apply to fields. Those can be hold by classes or packages, represented by
 * their parent.
 */
public class FieldOptions
{
  public static final boolean DEFAULT_FINAL = false;
  public static final boolean DEFAULT_GETTER = false;
  public static final boolean DEFAULT_LAST_UPDATED = false;
  public static final boolean DEFAULT_REDIRECT = false;
  public static final boolean DEFAULT_SETTER = false;
  public static final EFieldVisibility DEFAULT_VISIBILITY = EFieldVisibility.PUBLIC;

  private FieldOptions m_aParent;
  private Boolean m_aFinal;
  private Boolean m_aGetter;
  private Boolean m_aLastUpdated;
  private Boolean m_aRedirect;
  private Boolean m_aSetter;
  private EFieldVisibility m_aVisibility;

  @Nullable
  public FieldOptions getParent ()
  {
    return m_aParent;
  }

  @Nonnull
  public FieldOptions setParent (@Nullable final FieldOptions parent)
  {
    m_aParent = parent;
    return this;
  }

  // is field final

  public boolean isFinal ()
  {
    if (m_aFinal != null)
      return m_aFinal.booleanValue ();

    if (m_aParent != null)
      return m_aParent.isFinal ();
    return DEFAULT_FINAL;
  }

  @Nonnull
  public FieldOptions setFinal (@Nullable final Boolean _final)
  {
    m_aFinal = _final;
    return this;
  }

  // create getter

  public boolean isGetter ()
  {
    if (m_aGetter != null)
      return m_aGetter.booleanValue ();

    if (m_aParent != null)
      return m_aParent.isGetter ();
    return DEFAULT_GETTER;
  }

  @Nonnull
  public FieldOptions setGetter (@Nullable final Boolean getter)
  {
    m_aGetter = getter;
    return this;
  }

  // create Instant lastUpdated

  public boolean isLastUpdated ()
  {
    if (m_aLastUpdated != null)
      return m_aLastUpdated.booleanValue ();

    if (m_aParent != null)
      return m_aParent.isLastUpdated ();
    return DEFAULT_LAST_UPDATED;
  }

  @Nonnull
  public FieldOptions setLastUpdated (@Nullable final Boolean lastUpdated)
  {
    m_aLastUpdated = lastUpdated;
    return this;
  }

  // redirect field methods on the owner class

  public boolean isRedirect ()
  {
    if (m_aRedirect != null)
      return m_aRedirect.booleanValue ();

    if (m_aParent != null)
      return m_aParent.isRedirect ();
    return DEFAULT_REDIRECT;
  }

  @Nonnull
  public FieldOptions setRedirect (@Nullable final Boolean redirect)
  {
    m_aRedirect = redirect;
    return this;
  }

  // create setter
  public boolean isSetter ()
  {
    if (m_aSetter != null)
      return m_aSetter.booleanValue ();
    if (m_aParent != null)
      return m_aParent.isSetter ();
    return DEFAULT_SETTER;
  }

  @Nonnull
  public FieldOptions setSetter (@Nullable final Boolean setter)
  {
    m_aSetter = setter;
    return this;
  }

  // visibility of the field

  @Nullable
  public EFieldVisibility getVisibility ()
  {
    if (m_aVisibility != null)
      return m_aVisibility;

    if (m_aParent != null)
      return m_aParent.getVisibility ();
    return DEFAULT_VISIBILITY;
  }

  @Nonnull
  public FieldOptions setVisibility (@Nullable final EFieldVisibility visibility)
  {
    m_aVisibility = visibility;
    return this;
  }
}
