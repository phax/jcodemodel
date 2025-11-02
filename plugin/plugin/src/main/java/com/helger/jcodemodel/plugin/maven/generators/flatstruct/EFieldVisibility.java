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

import java.util.Locale;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.jcodemodel.JMod;

/**
 * visibility to set when constructing a field
 */
public enum EFieldVisibility
{
  PUBLIC (JMod.PUBLIC),
  PRIVATE (JMod.PRIVATE),
  PROTECTED (JMod.PROTECTED),
  PACKAGE (JMod.PROTECTED);

  public final int m_nJMod;

  EFieldVisibility (final int jmod)
  {
    m_nJMod = jmod;
  }

  public void apply (@NonNull final FieldOptions opt)
  {
    opt.setVisibility (this);
  }

  @Nullable
  public static EFieldVisibility of (@Nullable final String value)
  {
    if (value == null || value.isBlank ())
      return null;

    return switch (value.toLowerCase (Locale.ROOT))
    {
      case "public", "all" -> PUBLIC;
      case "private", "prv" -> PRIVATE;
      case "protected", "prt" -> PROTECTED;
      case "package", "packaged", "pck" -> PACKAGE;
      default -> null;
    };
  }

}
