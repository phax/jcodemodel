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

/**
 * Additional features to add when constructing fields
 */
public enum EFieldOption
{
  GETTER
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setGetter (Boolean.TRUE);
    }
  },
  NOGETTER
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setGetter (Boolean.FALSE);
    }
  },
  SETTER
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setSetter (Boolean.TRUE);
    }
  },
  NOSETTER
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setSetter (Boolean.FALSE);
    }
  },
  LASTUPDATED
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setLastUpdated (Boolean.TRUE);
    }
  },
  NOLASTUPDATED
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setLastUpdated (Boolean.FALSE);
    }
  },
  REDIRECT
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setRedirect (Boolean.TRUE);
    }
  },
  NOREDIRECT
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setRedirect (Boolean.FALSE);
    }
  },
  FINAL
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setFinal (Boolean.TRUE);
    }
  },
  NOFINAL
  {
    @Override
    public void apply (@NonNull final FieldOptions opt)
    {
      opt.setFinal (Boolean.FALSE);
    }
  },;

  public abstract void apply (@NonNull FieldOptions opt);

  @Nullable
  public static EFieldOption of (@Nullable final String value)
  {
    if (value == null || value.isBlank ())
      return null;

    return switch (value.toLowerCase (Locale.ROOT))
    {
      case "getter", "get" -> GETTER;
      case "nogetter", "noget" -> NOGETTER;
      case "setter", "set" -> SETTER;
      case "nosetter", "noset" -> NOSETTER;
      case "lastupdated", "updated" -> LASTUPDATED;
      case "nolastupdated", "noupdated" -> NOLASTUPDATED;
      case "redirect" -> REDIRECT;
      case "noredirect" -> NOREDIRECT;
      case "final", "const", "immutable" -> FINAL;
      case "nofinal", "noconst", "mutable" -> NOFINAL;
      default -> null;
    };
  }
}
