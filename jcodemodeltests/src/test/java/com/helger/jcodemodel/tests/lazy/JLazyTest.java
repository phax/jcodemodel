package com.helger.jcodemodel.tests.lazy;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

public class JLazyTest {

  @Test
  public void testStatic() {
    Set<Integer> values =
        IntStream.rangeClosed(0, 1000).parallel()
            .mapToObj(i -> GeneratedLazyClass.getSyncStatic())
            .collect(Collectors.toSet());
    Assert.assertEquals(1, values.size());
  }

  @Test
  public void testInstance() {
    GeneratedLazyClass test = new GeneratedLazyClass();
    Set<Integer> values =
        IntStream.rangeClosed(0, 1000).parallel()
            .mapToObj(i -> test.getSyncInstance())
            .collect(Collectors.toSet());
    Assert.assertEquals(1, values.size());
  }

}
