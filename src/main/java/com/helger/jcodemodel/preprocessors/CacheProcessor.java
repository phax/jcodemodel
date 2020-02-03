package com.helger.jcodemodel.preprocessors;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.AbstractJType;
import com.helger.jcodemodel.JBlock;
import com.helger.jcodemodel.JCodeModel;
import com.helger.jcodemodel.JDefinedClass;
import com.helger.jcodemodel.JExpr;
import com.helger.jcodemodel.JFieldVar;
import com.helger.jcodemodel.JMethod;
import com.helger.jcodemodel.JMod;
import com.helger.jcodemodel.JVar;

/**
 * generate cache from generator, associated to a name. Two use cases :
 *
 * <p>
 * First use case is when a method generates an item from nothing. eg String makeString(). if this method is registered
 * to the name "myobject", then this will produce
 * <ul>
 * <li>the field<br />
 * private String myobject_cache=null;</li>
 * <li>the method<br />
 * public String getMyobject(){
 * if(myobject_cache==null){
 * myobject_cache=makeString();
 * }
 * return myobject_cache;
 * }
 * </li>
 * </ul>
 * </p>
 * <p>
 * Second use case is when the method generates an item from something. This relation something=> item is then hold as a
 * cache attribute.
 * </p>
 *
 * @author glelouet
 *
 */
public class CacheProcessor extends AbstractJCodePreprocessor
{

  private LinkedHashMap <JDefinedClass, Map <String, JMethod>> generators = new LinkedHashMap <> ();

  public CacheProcessor register (JMethod meth, String name)
  {
    JDefinedClass cl = meth.owningClass ();
    Map <String, JMethod> map = generators.get (cl);
    if (map == null)
    {
      map = new LinkedHashMap <> ();
      generators.put (cl, map);
    }
    map.put (name, meth);
    return this;
  }

  @Override
  public boolean apply (JCodeModel jcm, boolean firstPass) throws PreprocessException
  {
    boolean modif = false;
    for (Entry <JDefinedClass, Map <String, JMethod>> e : generators.entrySet ())
    {
      JDefinedClass cl = e.getKey ();
      for (Entry <String, JMethod> e2 : e.getValue ().entrySet ())
      {
        String name = e2.getKey ();
        JMethod generator = e2.getValue ();
        if (generator.params ().isEmpty ())
        {
          if (generateNoParam (cl, generator, name, firstPass))
            modif = true;
        }
        else if (generator.params ().size () == 1)
        {
          if (generateParam (cl, generator, name, generator.params ().get (0), firstPass))
            modif = true;
        }
        else
          throw new PreprocessException ("can't use method "+generator.name ()+" as generator as it accepts"+generator.params ().size ()+" parameters");
      }

    }
    return modif;
  }

  /**
   * create a field and access to that field, with instantiation if this field is null.
   *
   * @param cl
   * @param generator
   * @param name
   * @param firstPass
   * @return
   * @throws PreprocessException
   */
  protected boolean generateNoParam (JDefinedClass cl, JMethod generator, String name, boolean firstPass)
      throws PreprocessException
  {
    if (cl.fields ().get (name) != null)
      if (firstPass)
        throw new PreprocessException ("can't create field " + name + " in class " + cl.binaryName ());
      else
        return false;
    JFieldVar field = cl.field (JMod.PRIVATE, generator.type (), name);
    field.init (JExpr._null ());
    JMethod access = cl.method (JMod.PUBLIC, generator.type (), makeMethodName (makeMethodPart (name)));
    JBlock assign = access.body ()._if (JExpr.ref (field).eqNull ())._then ();
    assign.assign (field, JExpr.invoke (generator));
    access.body ()._return (field);
    return true;
  }

  /**
   * create a field of type hashmap &lt;K, V&gt; as a cache over the generator method
   *
   * @param cl
   * @param generator
   *        a method that takes a K and retruns a V.
   * @param name
   * @param jVar
   * @param firstPass
   * @return
   * @throws PreprocessException
   */
  protected boolean generateParam (JDefinedClass cl, JMethod generator, String name, JVar jVar, boolean firstPass)
      throws PreprocessException
  {
    if (cl.fields ().get (name) != null)
      if (firstPass)
        throw new PreprocessException ("can't create field " + name + " in class " + cl.binaryName ());
      else
        return false;
    AbstractJType retType = generator.type ();
    AbstractJType paramType = generator.params ().get (0).type ();

    // make the map
    AbstractJClass fieldType = cl.owner ().ref (HashMap.class).narrow (paramType.boxify (), retType.boxify ());
    JFieldVar field = cl.field (JMod.PRIVATE,
        fieldType, name);
    field.init (fieldType._new ());

    JMethod access = cl.method (JMod.PUBLIC, retType, makeMethodName (makeMethodPart (name)));
    JVar accessParam = access.param (paramType, generator.params ().get (0).name ());
    JVar retVar = access.body ().decl (retType, "ret");
    retVar.init (field.invoke ("get").arg (accessParam));
    JBlock assignBlock = access.body ()._if (retVar.eqNull ())._then ();
    assignBlock.assign (retVar, JExpr.invoke (generator).arg (accessParam));
    assignBlock.add (JExpr.invoke (field, "put").arg (accessParam).arg (retVar));
    access.body ()._return (retVar);
    return true;
  }

  protected String makeMethodPart (String baseName)
  {
    if (baseName == null)
      return null;
    if (baseName.length () == 0)
      return baseName;
    return baseName.substring (0, 1).toUpperCase () + baseName.substring (1, baseName.length ());
  }

  protected String makeMethodName (String methodPart)
  {
    return "get" + methodPart;
  }

}
