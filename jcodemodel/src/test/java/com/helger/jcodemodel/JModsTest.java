package com.helger.jcodemodel;

import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

public class JModsTest {

  @Test
  public void testConvertModifiers() {
    Assert.assertEquals(JMod.PRIVATE, JMods.fromModifier(Modifier.PRIVATE));
    Assert.assertEquals(Modifier.PROTECTED, JMods.toModifier(JMod.PROTECTED));
  }

}
