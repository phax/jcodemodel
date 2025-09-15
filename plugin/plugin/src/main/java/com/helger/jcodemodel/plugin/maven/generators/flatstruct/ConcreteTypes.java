package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class ConcreteTypes {

  public static final String CONCRETE_LIST_PARAM = "concrete.list";
  public static final String CONCRETE_MAP_PARAM = "concrete.map";
  public static final String CONCRETE_SET_PARAM = "concrete.set";

  public Class<?> list, map, set;

  public static ConcreteTypes from(Map<String, String> params) {
    ConcreteTypes ret = new ConcreteTypes();
    ret.list = findClass(params.get(CONCRETE_LIST_PARAM), ArrayList.class);
    ret.map = findClass(params.get(CONCRETE_MAP_PARAM), HashMap.class);
    ret.set = findClass(params.get(CONCRETE_SET_PARAM), HashSet.class);
    return ret;
  }

  public static Class<?> findClass(String name, Class<?> defaultClass) {
    if (name == null) {
      return defaultClass;
    }
    Class<?> ret = null;
    try {
      ret = Class.forName(name);
    } catch (ClassNotFoundException e) {
    }
    if (ret != null) {
      return ret;
    }
    for (String prefix : new String[] { "java.util", "java.lang" }) {
      try {
        ret = Class.forName(prefix + "." + name);
        if (ret != null) {
          return ret;
        }
      } catch (ClassNotFoundException e) {
      }
    }
    throw new RuntimeException("can't find class " + name);
  }
}