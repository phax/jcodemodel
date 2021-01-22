package com.helger.jcodemodel;

import java.util.HashMap;
import java.util.Map.Entry;

import com.helger.jcodemodel.exceptions.JCodeModelException;

public class ModelCopy extends JCodeModel
{

  private final JCodeModel from;

  public ModelCopy (JCodeModel from)
  {
    this.from = from;
    // also translates classes
    translatePackages ();
    // TODO translate resources
  }

  private void translatePackages ()
  {
    for (JPackage pack : from.getAllPackages ())
    {
      translate (pack);
      for (JDefinedClass clas : pack.classes ())
        translate (clas);
    }
  }

  private HashMap <JPackage, JPackage> translatedPackages = new HashMap <> ();

  public JPackage translate (JPackage pack)
  {
    return translatedPackages.computeIfAbsent (pack, o -> _package (pack.name ()));
  }

  private HashMap <JDefinedClass, JDefinedClass> translatedJDefinedClass = new HashMap <> ();

  public JDefinedClass translate (JDefinedClass cl)
  {
    JDefinedClass ret = translatedJDefinedClass.get (cl);
    if (ret == null)
    {
      try
      {
        ret = _class (cl.mods ().getValue (), cl.fullName (), cl.getClassType ());
      }
      catch (JCodeModelException e1)
      {
        throw new UnsupportedOperationException ("catch this", e1);
      }
      // put it in the map before building it, for recursive calls.
      translatedJDefinedClass.put (cl, ret);
      for (Entry <String, JFieldVar> e : cl.fields ().entrySet ())
      {
        JFieldVar f = e.getValue ();
        ret.field (f.mods ().getValue (), f.type (), e.getKey (), translate (f.init ()));
      }
      // TODO what else ?
    }
    return cl;
  }

  protected IJExpression translate (IJExpression expr)
  {
    Class <? extends IJExpression> cl = expr.getClass ();
    if (cl == JArray.class)
    {
      JArray arr = (JArray) expr;
      return JExpr.newArray (arr.type (), arr.size ());
    }
    if (cl == JAtom.class)
    {
      JAtom atom = (JAtom) expr;
      return new JAtom (atom.what ());
    }
    // TODO
    return null;
  }

}
