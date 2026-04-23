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
