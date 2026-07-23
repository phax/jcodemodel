package com.helger.jcodemodel.tests.instanceofvar;

import java.util.Collection;
import java.util.Map;

import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JPackage;
import com.helger.jcodemodel.compile.annotation.TestJCM;
import com.helger.jcodemodel.exceptions.JCodeModelException;

@TestJCM
public class InstanceOfVarTestGen {

  public void instanceOfVarGen(final JPackage root) throws JCodeModelException {
    var cl = root._class("ExampleInstanceOfVar");
    var jm = cl.method(JMod.PUBLIC | JMod.STATIC, root.owner().INT, "toInt");
    var param = jm.param(Object.class, "o");

    jm.body()._if(param.eqNull())._then()._return(JExpr.lit(0));

    // string : strip.length
    var instanceOf = JExpr.instanceOf(param, root.owner().ref(String.class), "s");
    jm.body()._if(instanceOf.cand(instanceOf.var().invoke("isBlank").not()))
        ._then()._return(instanceOf.var().invoke("strip").invoke("length"));

    // collection : size
    instanceOf = JExpr.instanceOf(param, root.owner().ref(Collection.class), "c");
    jm.body()._if(instanceOf)
        ._then()._return(instanceOf.var().invoke("size"));

    // Map : size
    instanceOf = JExpr.instanceOf(param, root.owner().ref(Map.class), "m");
    jm.body()._if(instanceOf)
        ._then()._return(instanceOf.var().invoke("size"));

    // Number : intValue
    instanceOf = JExpr.instanceOf(param, root.owner().ref(Number.class), "n");
    jm.body()._if(instanceOf)
        ._then()._return(instanceOf.var().invoke("intValue"));

    jm.body()._return(JExpr.lit(0));
  }

}
