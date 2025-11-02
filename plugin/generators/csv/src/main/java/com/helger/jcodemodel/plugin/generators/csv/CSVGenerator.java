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
package com.helger.jcodemodel.plugin.generators.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Stream;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import com.helger.base.string.StringHelper;
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
  public void configure (@NonNull final Map <String, String> params)
  {
    fldSep = params.getOrDefault ("field_sep", fldSep);
  }

  @Override
  protected Stream <IFlatStructRecord> loadSource (final InputStream source)
  {
    // What charset to use?
    return new BufferedReader (new InputStreamReader (source)).lines ().map (this::convertLine).filter (r -> r != null);
  }

  protected IFlatStructRecord convertLine (@Nullable final String line)
  {
    if (StringHelper.isEmpty (line))
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
    if (StringHelper.isEmpty (fieldName))
    {
      if (className.contains (" "))
        return new PackageCreation (className.replaceAll (".* ", ""), options);

      return new ClassCreation (className, ec, options);
    }
    return new SimpleField (className, fieldName, ec, options);
  }

}
