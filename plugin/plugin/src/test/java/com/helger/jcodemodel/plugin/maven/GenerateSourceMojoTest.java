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
package com.helger.jcodemodel.plugin.maven;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeNoException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class GenerateSourceMojoTest
{
  @Rule
  public final TemporaryFolder m_aFolder = new TemporaryFolder ();

  private GenerateSourceMojo m_aMojo;
  private File m_aRoot;

  @Before
  public void before () throws IOException
  {
    m_aMojo = new GenerateSourceMojo ();
    m_aRoot = m_aFolder.newFolder ("sources");
    m_aFolder.newFile ("sources/a.yaml");
    m_aFolder.newFile ("sources/b.txt");
    m_aFolder.newFolder ("sources", "sub");
    m_aFolder.newFile ("sources/sub/c.yaml");
  }

  private List <String> _findNames (final File aRootFile)
  {
    return m_aMojo.findSourceFiles (aRootFile).stream ().map (File::getName).toList ();
  }

  @Test
  public void testSingleFile ()
  {
    assertEquals (List.of ("a.yaml"), _findNames (new File (m_aRoot, "a.yaml")));
  }

  @Test
  public void testDirectoryIsBrowsedRecursively ()
  {
    assertEquals (List.of ("a.yaml", "b.txt", "c.yaml"), _findNames (m_aRoot));
  }

  @Test
  public void testFilterAppliesToFilesOnly ()
  {
    // the sub directory does not match the filter, but the files inside it do
    m_aMojo.setSourcesFilter (".yaml");
    assertEquals (List.of ("a.yaml", "c.yaml"), _findNames (m_aRoot));
  }

  @Test
  public void testMissingFile ()
  {
    assertEquals (List.of (), _findNames (new File (m_aRoot, "doesNotExist.yaml")));
  }

  @Test
  public void testSymbolicLinkLoop ()
  {
    try
    {
      // sources/sub/loop -> sources
      Files.createSymbolicLink (new File (m_aRoot, "sub/loop").toPath (), m_aRoot.toPath ());
    }
    catch (final IOException | UnsupportedOperationException e)
    {
      // creating symbolic links is not allowed on every platform
      assumeNoException (e);
    }

    // must not recurse endlessly ; the linked directory is visited only once
    assertEquals (List.of ("a.yaml", "b.txt", "c.yaml"), _findNames (m_aRoot));
  }
}
