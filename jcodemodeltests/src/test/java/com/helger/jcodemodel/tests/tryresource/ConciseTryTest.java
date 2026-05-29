package com.helger.jcodemodel.tests.tryresource;

import org.junit.Test;

import com.helger.jcodemodel.tests.tryresource.ConciseTryTestGen.NoErrorCloseable;

public class ConciseTryTest {

  public class CloseMemory implements NoErrorCloseable {

    public int closed = 0;

    @Override
    public void close() {
      closed++;
    }

  }

  @Test
  public void testConciseTry() {
    CloseMemory cm = new CloseMemory();
    org.junit.Assert.assertEquals(0, cm.closed);
    new BasicTry().close(cm);
    org.junit.Assert.assertEquals(1, cm.closed);
  }

}
