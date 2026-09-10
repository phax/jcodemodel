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
package com.helger.jcodemodel.example.settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

import org.junit.Test;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator.Feature;
import com.helger.jcodemodel.writer.FormatterSettings;

/// Actually not a test, but export the default formatter settings so people
/// have an idea of the settings available.
/// The yaml and json files are exported at the project root.
public class ExportSettingsTest {

  @Test
  public void writeYaml() {
    FormatterSettings export = new FormatterSettings();

    YAMLFactory f =
        new YAMLFactory()
            .disable(Feature.WRITE_DOC_START_MARKER);
    ObjectMapper om = new ObjectMapper(f);

    File out = new File("example-settings.yaml");
    try (FileWriter writer = new FileWriter(out)) {
      om.writer().writeValue(writer, export);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  @Test
  public void writeJson() {
    FormatterSettings export = new FormatterSettings();

    DefaultPrettyPrinter prettyPrinter =
        new DefaultPrettyPrinter()
        .withObjectIndenter(new DefaultIndenter().withLinefeed("\n"));
    ObjectMapper om =
        new ObjectMapper()
            .setDefaultPrettyPrinter(prettyPrinter)
            .enable(SerializationFeature.INDENT_OUTPUT);

    File out = new File("example-settings.json");
    try (FileWriter writer = new FileWriter(out)) {
      om.writer().writeValue(writer, export);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

}
