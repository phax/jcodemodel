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

package com.helger.jcodemodel.plugin.generators.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Stream;

import com.helger.jcodemodel.plugin.maven.generators.AbstractFlatStructureGenerator;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.Encapsulated;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.PackageCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.SimpleField;

@JCMGen
public class CSVGenerator extends AbstractFlatStructureGenerator
{

  private String fldSep = ",";

  @Override
  public void configure (final Map <String, String> params)
  {
    fldSep = params.getOrDefault ("field_sep", fldSep);
  }

  @Override
  protected Stream <IFlatStructRecord> loadSource (final InputStream source)
  {
    return new BufferedReader (new InputStreamReader (source)).lines ().map (this::convertLine).filter (r -> r != null);
  }

  protected IFlatStructRecord convertLine (final String line)
  {
    if (line == null || line.isBlank ())
    {
      return null;
    }
    final String [] spl = line.trim ().split (fldSep);
    final String className = spl[0].trim ();

    // field name for fields. Absent for non-fields

    String fieldName = null;
    if (spl.length > 1)
    {
      fieldName = spl[1].trim ();
    }

    // find the type specified, if any, and array depth

    Encapsulated ec = null;
    if (spl.length > 2)
    {
      ec = Encapsulated.parse (spl[2]);
    }

    final FieldOptions options = new FieldOptions ();
    if (spl.length >= 4)
    {
      for (int i = 3; i < spl.length; i++)
      {
        applyToFieldOptions (spl[i], options);
      }
    }

    // no field name specified : class or package definition
    if (fieldName == null || fieldName.isBlank ())
    {
      if (className.contains (" "))
      {
        return new PackageCreation (className.replaceAll (".* ", ""), options);
      }
      return new ClassCreation (className, ec, options);
    }
    return new SimpleField (className, fieldName, ec, options);
  }

}
