/*
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
