package com.helger.jcodemodel.preprocessors;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;
import com.helger.jcodemodel.preprocessors.exceptions.BeanProcessorException;

public class BeanProcessor extends AbstractJCodePreprocessor
{

  private Set <JFieldVar> fields = new HashSet <> ();

  /**
   * add fields to be beanified.
   *
   * @param field
   *        required field to add.
   * @return
   */
  public BeanProcessor add (JFieldVar... field)
  {
    fields.addAll (Arrays.asList (field));
    return this;
  }

  @Override
  public boolean apply (JCodeModel jcm, boolean firstPass) throws BeanProcessorException
  {

    boolean bModification = false;
    for (Entry <JDefinedClass, Set <JFieldVar>> entry : listGetterFields ().entrySet ())
    {
      JDefinedClass cl = entry.getKey ();
      for (JFieldVar jfv : entry.getValue ())
        if (generateGetter (jfv, cl, firstPass))
          bModification = true;
    }
    for (Entry <JDefinedClass, Set <JFieldVar>> entry : listSetterFields ().entrySet ())
    {
      JDefinedClass cl = entry.getKey ();
      for (JFieldVar jfv : entry.getValue ())
        if (generateSetter (jfv, cl, firstPass))
          bModification = true;
    }
    return bModification;
  }

  protected Map <JDefinedClass, Set <JFieldVar>> listGetterFields ()
  {
    Map <JDefinedClass, Set <JFieldVar>> ret = new HashMap <> ();
    for (JFieldVar field : fields)
    {
      Set <JFieldVar> classFields = ret.get (field.owner ());
      if (classFields == null)
      {
        classFields = new HashSet <> ();
        ret.put (field.owner (), classFields);
      }
      classFields.add (field);
    }
    return ret;
  }

  protected boolean generateGetter (JFieldVar jfv, JDefinedClass cl, boolean firstPass) throws BeanProcessorException
  {
    AbstractJType type = jfv.type ();
    String sNameSet = "get" + methodBaseName (jfv);
    for (JMethod met : cl.methods ())
      if (met.params ().isEmpty () && met.name ().equals (sNameSet))
      {
        onCreationError (sNameSet, jfv, cl, firstPass);
        return false;
      }
    JMethod meth = cl.method (JMod.PUBLIC, type, sNameSet);
    meth.body ()._return (jfv);
    meth.javadoc ().addReturn ().add ("the {@link #" + jfv.name () + "}");
    return true;
  }

  protected Map <JDefinedClass, Set <JFieldVar>> listSetterFields ()
  {
    Map <JDefinedClass, Set <JFieldVar>> ret = new HashMap <> ();
    for (JFieldVar field : fields)
    {
      Set <JFieldVar> classFields = ret.get (field.owner ());
      if (classFields == null)
      {
        classFields = new HashSet <> ();
        ret.put (field.owner (), classFields);
      }
      classFields.add (field);
    }
    return ret;
  }

  protected boolean generateSetter (JFieldVar jfv, JDefinedClass cl, boolean firstPass) throws BeanProcessorException
  {
    if ((jfv.mods ().getValue () & JMod.FINAL) != 0)// when field is not final
      return false;
    AbstractJType type = jfv.type ();
    String sNameSet = "set" + methodBaseName (jfv);
    for (JMethod met : cl.methods ())
      if (met.params ().size () == 1 && met.params ().get (0).type ().equals (type) && met.name ().equals (sNameSet))
      {
        onCreationError (sNameSet, jfv, cl, firstPass);
        return false;
      }
    JMethod meth = cl.method (JMod.PUBLIC, type, sNameSet);
    JVar p = meth.param (type, "value");
    meth.body ().assign (jfv, p);
    meth.javadoc ().addParam (p).add ("the value to assign to {@link #" + jfv.name () + "}");
    return true;
  }

  protected String methodBaseName (JFieldVar jfv)
  {
    return jfv.name ().substring (0, 1).toUpperCase () + jfv.name ().substring (1);
  }

  protected void onCreationError (String sNameSet, JFieldVar jfv, JDefinedClass cl, boolean firstPass)
      throws BeanProcessorException
  {
    if (firstPass)
      throw new BeanProcessorException (
          "can't create method name " + sNameSet + " for field " + jfv.name () + " in class " + cl.fullName ());
  }

}
