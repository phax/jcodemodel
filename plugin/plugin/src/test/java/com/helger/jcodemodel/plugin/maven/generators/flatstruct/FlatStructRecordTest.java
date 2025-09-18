package com.helger.jcodemodel.plugin.maven.generators.flatstruct;

import org.junit.Assert;
import org.junit.Test;

import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.Encapsulated;
import com.helger.jcodemodel.plugin.maven.generators.flatstruct.FlatStructRecord.Encapsulation;

public class FlatStructRecordTest {

  @Test
  public void testParse() {
    Encapsulated test1 = Encapsulated.parse("String[]");
    Assert.assertEquals("String", test1.baseClassName());
    Assert.assertEquals(Encapsulation.ARRAY, test1.encapsulations().get(0));

    Encapsulated test2 = Encapsulated.parse("int [ ][ ] map");
    Assert.assertEquals("int", test2.baseClassName());
    Assert.assertEquals(Encapsulation.ARRAY, test2.encapsulations().get(0));
    Assert.assertEquals(Encapsulation.ARRAY, test2.encapsulations().get(1));
    Assert.assertEquals(Encapsulation.MAP, test2.encapsulations().get(2));

    Encapsulated test3 = Encapsulated.parse("double []set[]");
    Assert.assertEquals("double", test3.baseClassName());
    Assert.assertEquals(Encapsulation.ARRAY, test3.encapsulations().get(0));
    Assert.assertEquals(Encapsulation.SET, test3.encapsulations().get(1));
    Assert.assertEquals(Encapsulation.ARRAY, test3.encapsulations().get(2));

    Encapsulated test4 = Encapsulated.parse("java.lang.String MAP   ");
    Assert.assertEquals("java.lang.String", test4.baseClassName());
    Assert.assertEquals(Encapsulation.MAP, test4.encapsulations().get(0));
  }

}
