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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.helger.base.io.nonblocking.NonBlockingByteArrayInputStream;

/// an inputstream and its source when it has one
/// 
/// implementations are :
///  - from a file
///  - from a url
///  - transmitted directly
///  - no source was provided, so null
public sealed interface ISourcedInputStream
{
  public InputStream inputStream ();

  /// when the inputstream is generated from a file
  public static record FileSourced (File sourceFile, InputStream inputStream) implements ISourcedInputStream
  {
    public FileSourced (File sourceFile) throws FileNotFoundException
    {
      this (sourceFile, new FileInputStream (sourceFile));
    }

  }

  /// when the inputstream is generated from a url
  public static record URLSourced (String sourceUrl, InputStream inputStream) implements ISourcedInputStream
  {}

  /// when the data was transmitted directly
  public static record DirectSourced (InputStream inputStream) implements ISourcedInputStream
  {
    public DirectSourced (String data)
    {
      this (new NonBlockingByteArrayInputStream (data.getBytes (StandardCharsets.UTF_8)));
    }
  }

  /// when no data was provided, so no inputstream
  public static record NullSourced () implements ISourcedInputStream
  {
    @Override
    public InputStream inputStream ()
    {
      return null;
    }
  }

  public static NullSourced NULL = new NullSourced ();

}
