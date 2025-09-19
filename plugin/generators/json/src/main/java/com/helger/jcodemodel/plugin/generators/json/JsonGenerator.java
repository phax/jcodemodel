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
package com.helger.jcodemodel.plugin.generators.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonField;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonPackage;
import com.helger.jcodemodel.plugin.maven.generators.AbstractFlatStructureGenerator;
import com.helger.jcodemodel.plugin.maven.generators.JCMGen;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FieldOptions;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.ClassCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.Encapsulated;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.PackageCreation;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.SimpleField;

@JCMGen
public class JsonGenerator extends AbstractFlatStructureGenerator
{

  @Override
  protected Stream <IFlatStructRecord> loadSource (InputStream source)
  {
    try
    {
      return visitPackage (load (source), null);
    }
    catch (IOException e)
    {
      throw new RuntimeException (e);
    }
  }

  protected JsonPackage load (InputStream source) throws IOException
  {
    ObjectMapper mapper = new ObjectMapper ();
    return mapper.readerFor (JsonPackage.class).readValue (source);
  }

  protected Stream <IFlatStructRecord> visitPackage (JsonPackage pck, String path)
  {
    System.err.println ("visit package " + path);
    Stream <IFlatStructRecord> ret = Stream.empty ();
    if (pck.isClassInfo ())
    {
      if (pck.clazz != null || pck.parentClassName != null)
      {
        FieldOptions options = new FieldOptions ();
        if (pck.clazz != null)
        {
          for (String optStr : pck.clazz)
          {
            applyToFieldOptions (optStr, options);
          }
        }
        ret = Stream.concat (ret,
                             Stream.of (new ClassCreation (path, Encapsulated.parse (pck.parentClassName), options)));
      }
      if (pck.fields != null)
      {
        for (Entry <String, JsonField> e : pck.fields.entrySet ())
        {
          ret = Stream.concat (ret, visitField (e.getValue (), path, e.getKey ()));
        }
      }
    }
    else
    {
      if (pck.isPackageInfo ())
      {
        FieldOptions options = new FieldOptions ();
        if (pck.pck != null)
        {
          for (String optStr : pck.pck)
          {
            applyToFieldOptions (optStr, options);
          }
        }
        ret = Stream.concat (ret, Stream.of (new PackageCreation (path, options)));
      }
      for (Entry <String, JsonPackage> e : pck.subPackages ().entrySet ())
      {
        String subPath = (path == null ? "" : path + ".") + e.getKey ();
        ret = Stream.concat (ret, visitPackage (e.getValue (), subPath));
      }

    }
    return ret;
  }

  protected Stream <IFlatStructRecord> visitField (JsonField field, String path, String fieldName)
  {
    FieldOptions options = new FieldOptions ();
    if (field.options != null)
    {
      for (String optStr : field.options)
      {
        applyToFieldOptions (optStr, options);
      }
    }
    Encapsulated enc = Encapsulated.parse (field.type);
    return Stream.of (new SimpleField (path, fieldName, enc, options));
  }

}
