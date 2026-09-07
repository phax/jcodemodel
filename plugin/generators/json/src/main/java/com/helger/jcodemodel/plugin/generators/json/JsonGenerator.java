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
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonField;
import com.helger.jcodemodel.plugin.generators.json.parser.JsonPackage;
import com.helger.jcodemodel.plugin.maven.ISourcedInputStream;
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
  protected Stream <IFlatStructRecord> loadSource (@NonNull final ISourcedInputStream source)
  {
    final InputStream is = source.inputStream ();
    if (is == null)
      return Stream.empty ();

    try
    {
      final List <IFlatStructRecord> ret = new ArrayList <> ();
      visitPackageRecursive (load (source), null, ret);
      return ret.stream ();
    }
    catch (final IOException e)
    {
      throw new UncheckedIOException (e);
    }
  }

  protected JsonPackage load (final ISourcedInputStream source) throws IOException
  {
    final ObjectMapper mapper = new ObjectMapper ();
    return mapper.readerFor (JsonPackage.class).readValue (source.inputStream ());
  }

  protected void visitPackageRecursive (final JsonPackage pck, final String path, final List <IFlatStructRecord> target)
  {
    if (pck.isClassInfo ())
    {
      if (pck.clazz != null || pck.parentClassName != null)
      {
        final FieldOptions options = new FieldOptions ();
        if (pck.clazz != null)
        {
          for (final String optStr : pck.clazz)
          {
            applyToFieldOptions (optStr, options);
          }
        }
        target.add (new ClassCreation (path, Encapsulated.parse (pck.parentClassName), options));
      }
      if (pck.fields != null)
      {
        for (final Entry <String, JsonField> e : pck.fields.entrySet ())
        {
          visitField (e.getValue (), path, e.getKey (), target);
        }
      }
    }
    else
    {
      if (pck.isPackageInfo ())
      {
        final FieldOptions options = new FieldOptions ();
        if (pck.pck != null)
        {
          for (final String optStr : pck.pck)
          {
            applyToFieldOptions (optStr, options);
          }
        }
        target.add (new PackageCreation (path, options));
      }
      for (final Entry <String, JsonPackage> e : pck.subPackages ().entrySet ())
      {
        final String subPath = (path == null ? "" : path + ".") + e.getKey ();
        visitPackageRecursive (e.getValue (), subPath, target);
      }
    }
  }

  protected void visitField (final JsonField field,
                             final String path,
                             final String fieldName,
                             final List <IFlatStructRecord> target)
  {
    final FieldOptions options = new FieldOptions ();
    if (field.options != null)
    {
      for (final String optStr : field.options)
      {
        applyToFieldOptions (optStr, options);
      }
    }
    final Encapsulated enc = Encapsulated.parse (field.type);
    target.add (new SimpleField (path, fieldName, enc, options));
  }
}
