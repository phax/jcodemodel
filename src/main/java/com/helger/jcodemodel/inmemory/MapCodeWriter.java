package com.helger.jcodemodel.inmemory;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.helger.jcodemodel.writer.AbstractCodeWriter;

/**
 * received code and store it in maps of binary data.
 * <p>
 * This is used in a {@link JCodeModel.#build(CodeWriter)} to store the code
 * built. THEN use the {@link #getBinaries()} to retrieve a map of the binaries
 * created by the building process.
 * </p>
 * <p>
 * binary for my.pck.MyClass.java is stored to the key "my/pck/MyClass.java".
 * </p>
 *
 */
public class MapCodeWriter extends AbstractCodeWriter {

  protected MapCodeWriter() {
    super(Charset.defaultCharset(), System.lineSeparator());
  }

  private HashMap<String, ByteArrayOutputStream> binaries = new HashMap<>();

  @Override
  public void close() throws IOException {
    // nothing.
  }

  /**
   *
   * @return an unmodifiable map of the internal binaries.
   */
  public Map<String, ByteArrayOutputStream> getBinaries() {
    return Collections.unmodifiableMap(binaries);
  }

  @Override
  public OutputStream openBinary(String sDirName, String sFilename) throws IOException {
    String fullname = sDirName + "/" + sFilename;

    ByteArrayOutputStream ret = binaries.get(fullname);
    if (ret == null) {
      ret = new ByteArrayOutputStream();
      binaries.put(fullname, ret);
    }
    return ret;
  }

}
