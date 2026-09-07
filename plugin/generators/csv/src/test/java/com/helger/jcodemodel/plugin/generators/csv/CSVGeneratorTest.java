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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;
import com.helger.jcodemodel.plugin.maven.ISourcedInputStream;
import com.helger.jcodemodel.plugin.maven.ISourcedInputStream.DirectSourced;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.IFlatStructRecord.SimpleField;

public class CSVGeneratorTest
{
  /// a line with a non ASCII field name. Written with an escape, so that this file stays ASCII only
  private static final String UMLAUT_FIELD = "\u00f6lfeld";
  private static final String UMLAUT_LINE = "MyClass," + UMLAUT_FIELD + ",string";

  private static CSVGenerator _generator (final Map <String, String> params)
  {
    final CSVGenerator ret = new CSVGenerator ();
    ret.configure (params);
    return ret;
  }

  private static List <IFlatStructRecord> _load (final CSVGenerator gen, final String content, final Charset charset)
  {
    final ISourcedInputStream source = new DirectSourced (new NonBlockingByteArrayInputStream (content.getBytes (charset)));
    return gen.loadSource (source).toList ();
  }

  @Test
  public void testLineWithOnlySeparators ()
  {
    final CSVGenerator gen = _generator (Map.of ());
    assertNull (gen.convertLine (","));
    assertNull (gen.convertLine (",,,"));
    assertNull (gen.convertLine ("  ,  "));
    assertNull (gen.convertLine (""));
    assertNull (gen.convertLine (null));
  }

  @Test
  public void testFieldSepIsALiteral ()
  {
    final CSVGenerator gen = _generator (Map.of ("field_sep", "|"));
    final IFlatStructRecord rec = gen.convertLine ("MyClass|myField|int");
    assertTrue (rec instanceof SimpleField);

    final SimpleField sf = (SimpleField) rec;
    assertEquals ("MyClass", sf.localName ());
    assertEquals ("myField", sf.fieldName ());
    assertEquals ("int", sf.fieldType ().baseClassName ());
  }

  @Test
  public void testDefaultCharsetIsUTF8 ()
  {
    final List <IFlatStructRecord> records = _load (_generator (Map.of ()), UMLAUT_LINE, StandardCharsets.UTF_8);
    assertEquals (1, records.size ());
    assertEquals (UMLAUT_FIELD, ((SimpleField) records.get (0)).fieldName ());

    // the same content in another charset is not decoded as UTF-8
    final List <IFlatStructRecord> other = _load (_generator (Map.of ()), UMLAUT_LINE, StandardCharsets.ISO_8859_1);
    assertEquals (1, other.size ());
    assertNotEquals (UMLAUT_FIELD, ((SimpleField) other.get (0)).fieldName ());
  }

  @Test
  public void testConfiguredCharset ()
  {
    final CSVGenerator gen = _generator (Map.of ("charset", StandardCharsets.ISO_8859_1.name ()));
    final List <IFlatStructRecord> records = _load (gen, UMLAUT_LINE, StandardCharsets.ISO_8859_1);
    assertEquals (1, records.size ());
    assertEquals (UMLAUT_FIELD, ((SimpleField) records.get (0)).fieldName ());
  }

  @Test
  public void testNoSource ()
  {
    assertEquals (0, _generator (Map.of ()).loadSource (ISourcedInputStream.NULL).count ());
  }
}
