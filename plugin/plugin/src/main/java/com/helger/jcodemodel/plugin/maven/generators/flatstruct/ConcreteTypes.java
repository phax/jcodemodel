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
package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class ConcreteTypes
{
  public static final String CONCRETE_LIST_PARAM = "concrete.list";
  public static final String CONCRETE_MAP_PARAM = "concrete.map";
  public static final String CONCRETE_SET_PARAM = "concrete.set";

  public Class <?> list, map, set;

  private ConcreteTypes ()
  {}

  @NonNull
  public static ConcreteTypes from (@NonNull final Map <String, String> params)
  {
    final ConcreteTypes ret = new ConcreteTypes ();
    ret.list = findClass (params.get (CONCRETE_LIST_PARAM), ArrayList.class);
    ret.map = findClass (params.get (CONCRETE_MAP_PARAM), HashMap.class);
    ret.set = findClass (params.get (CONCRETE_SET_PARAM), HashSet.class);
    return ret;
  }

  @Nullable
  public static Class <?> findClass (@Nullable final String name, @Nullable final Class <?> defaultClass)
  {
    if (name == null)
      return defaultClass;

    Class <?> ret = null;
    try
    {
      ret = Class.forName (name);
      if (ret != null)
        return ret;
    }
    catch (final ClassNotFoundException e)
    {}

    for (final String prefix : new String [] { "java.util", "java.lang" })
    {
      try
      {
        ret = Class.forName (prefix + "." + name);
        if (ret != null)
          return ret;
      }
      catch (final ClassNotFoundException e)
      {}
    }

    throw new IllegalArgumentException ("can't find class '" + name + "'");
  }
}
