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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;

import jakarta.annotation.Nullable;

public sealed interface IFlatStructRecord
{
  String localName ();

  /**
   * specify package-wide options
   * 
   * @param localName
   *        local name
   * @param options
   *        options
   */
  public record PackageCreation (String localName, FieldOptions options) implements IFlatStructRecord
  {}

  /**
   * create a class, with options
   * 
   * @param localName
   *        local name
   * @param parentType
   *        parent type
   * @param options
   *        options
   */
  public record ClassCreation (String localName, Encapsulated parentType, FieldOptions options) implements
                              IFlatStructRecord
  {}

  /**
   * create a field.
   */
  public sealed interface IFieldCreation extends IFlatStructRecord
  {
    String fieldName ();

    /**
     * @return fully readable requested type
     */
    String fieldClassName ();

    FieldOptions options ();
  }

  /**
   * A field definition in a class, with a simple type that can be an array
   * 
   * @param localName
   *        local name
   * @param fieldName
   *        field name
   * @param fieldType
   *        file type
   * @param options
   *        options
   */
  public record SimpleField (String localName, String fieldName, Encapsulated fieldType, FieldOptions options)
                            implements
                            IFieldCreation
  {
    @Override
    public String fieldClassName ()
    {
      return fieldType ().toString ();
    }
  }

  enum EEncapsulation
  {
    ARRAY ()
    {
      @Override
      public String apply (final String encapsulatedClassName)
      {
        return encapsulatedClassName + " []";
      }

      @Override
      public AbstractJType apply (final AbstractJType t, final JCodeModel cm)
      {
        return t.array ();
      }

      @Override
      public AbstractJType applyConcrete (final AbstractJType t, final JCodeModel cm, final ConcreteTypes concrete)
      {
        return t.array ();
      }
    },
    LIST ()
    {
      @Override
      public String apply (final String encapsulatedClassName)
      {
        return "List<" + encapsulatedClassName + ">";
      }

      @Override
      public AbstractJType apply (final AbstractJType e, final JCodeModel cm)
      {
        return cm.ref (List.class).narrow (e);
      }

      @Override
      public AbstractJType applyConcrete (final AbstractJType e, final JCodeModel cm, final ConcreteTypes concrete)
      {
        return cm.ref (concrete.list).narrow (e);
      }
    },
    MAP ()
    {
      @Override
      public String apply (final String encapsulatedClassName)
      {
        return "Map<Object, " + encapsulatedClassName + ">";
      }

      @Override
      public AbstractJType apply (final AbstractJType e, final JCodeModel cm)
      {
        return cm.ref (Map.class).narrow (cm.ref (Object.class)).narrow (e);
      }

      @Override
      public AbstractJType applyConcrete (final AbstractJType e, final JCodeModel cm, final ConcreteTypes concrete)
      {
        return cm.ref (concrete.map).narrow (cm.ref (Object.class)).narrow (e);
      }
    },
    SET ()
    {
      @Override
      public String apply (final String encapsulatedClassName)
      {
        return "Set<" + encapsulatedClassName + ">";
      }

      @Override
      public AbstractJType apply (final AbstractJType e, final JCodeModel cm)
      {
        return cm.ref (Set.class).narrow (e);
      }

      @Override
      public AbstractJType applyConcrete (final AbstractJType e, final JCodeModel cm, final ConcreteTypes concrete)
      {
        return cm.ref (concrete.set).narrow (e);
      }
    };

    public abstract String apply (String encapsulatedClassName);

    public abstract AbstractJType apply (AbstractJType e, JCodeModel cm);

    public abstract AbstractJType applyConcrete (AbstractJType e, JCodeModel cm, ConcreteTypes concrete);

    @Nullable
    public static EEncapsulation parse (@Nullable final String s)
    {
      if (s == null)
        return null;

      // remove internal whitespaces for eg arrays
      final String sReal = s.toLowerCase (Locale.ROOT).replaceAll ("\\s", "");
      return switch (sReal)
      {
        case "[]" -> ARRAY;
        case "list" -> LIST;
        case "map" -> MAP;
        case "set" -> SET;
        default ->
        {
          throw new UnsupportedOperationException ();
        }
      };
    }
  }

  record Encapsulated (String baseClassName, List <EEncapsulation> encapsulations)
  {
    private static final Pattern BASECLASS_PAT = Pattern.compile ("\\s*([\\w\\.]+)\\s*(.*)");
    private static final Pattern ENCAPS_PAT = Pattern.compile ("\\s*(\\[\\s*\\]|[\\w]+)\\s*(.*)");

    public static Encapsulated parse (final String s)
    {
      String baseClass = null;
      final ArrayList <EEncapsulation> encapsulations = new ArrayList <> ();
      if (s == null)
      {
        return new Encapsulated (baseClass, encapsulations);
      }
      final Matcher m = BASECLASS_PAT.matcher (s);
      if (m.matches ())
      {
        baseClass = m.group (1);
        String restType = m.group (2);
        while (restType != null && !restType.isBlank ())
        {
          final Matcher m2 = ENCAPS_PAT.matcher (restType);
          if (!m2.matches ())
          {
            break;
          }
          encapsulations.add (EEncapsulation.parse (m2.group (1)));
          restType = m2.group (2);
        }
      }
      return new Encapsulated (baseClass, encapsulations);
    }

    @Override
    public String toString ()
    {
      String ret = baseClassName ();
      for (final EEncapsulation enc : encapsulations ())
      {
        ret = enc.apply (ret);
      }
      return ret;
    }
  }
}
