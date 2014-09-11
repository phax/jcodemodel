/*
 * Copyright 2014 Philip Helger.
 */
package com.helger.jcodemodel.tests;

import com.helger.jcodemodel.AbstractJClass;
import com.helger.jcodemodel.JCodeModel;
import java.util.ArrayList;
import java.util.List;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class AbstractJTypeTest {

  List<AbstractJClass> freshTypes = new ArrayList<AbstractJClass> ();
  List<AssignmentTypes> freshAssignableTypes = new ArrayList<AssignmentTypes> ();

  private void registerType (AbstractJClass type)
  {
    freshTypes.add(type);
  }

  private List<AbstractJClass> refreshTypes ()
  {
    List<AbstractJClass> result = freshTypes;
    freshTypes = new ArrayList<AbstractJClass> ();
    return result;
  }

  private List<AssignmentTypes> refreshAssignableTypes ()
  {
    List<AssignmentTypes> result = freshAssignableTypes;
    freshAssignableTypes = new ArrayList<AssignmentTypes> ();
    return result;
  }

  private void assertIsAssignableInTopLevelPositionOnly (AbstractJClass variable, AbstractJClass value)
  {
    boolean result = variable.isAssignableFrom (value);
    // System.out.println(variable + ".isAssignableFrom(" + value + ") == " + result);
    try
    {
      assert result;
    } catch (AssertionError ex) {
      throw new AssertionError ("Expecting " + variable + " to be assignable from " + value);
    }
  }

  private void assertIsAssignable (AbstractJClass variable, AbstractJClass value)
  {
    freshAssignableTypes.add (new AssignmentTypes (variable, value));
    assertIsAssignableInTopLevelPositionOnly (variable, value);
  }

  private void assertIsNotAssignable (AbstractJClass variable, AbstractJClass value)
  {
    boolean result = variable.isAssignableFrom (value);
    // System.out.println(variable + ".isAssignableFrom (" + value + ") == " + result);
    try
    {
      assert !result;
    } catch (AssertionError ex) {
      throw new AssertionError ("Expecting " + variable + " not to be assignable from " + value);
    }
  }

  @After
  public void cleanup ()
  {
    refreshTypes ();
    refreshAssignableTypes ();
  }

  @Test
  public void testIsAssignableFromSmoke()
  {
    JCodeModel codeModel = new JCodeModel ();
    AbstractJClass _Object = codeModel.ref (Object.class);
    AbstractJClass _Integer = codeModel.ref (Integer.class);
    AbstractJClass _List = codeModel.ref (List.class);

    assertIsAssignable (_Object, _Integer);
    assertIsAssignable (_List.narrow (_Integer), _List.narrow (_Integer));
    assertIsNotAssignable (_List.narrow (_Object), _List.narrow (_Integer));
    assertIsAssignable (_List.narrow (_Object.wildcard ()), _List.narrow (_Integer));
    assertIsAssignable (_List.narrow (_Object.wildcard ()), _List.narrow (_Integer.wildcard()));
    assertIsAssignable (_List.narrow (_Integer.wildcardSuper ()), _List.narrow (_Object));
    assertIsAssignable (_List.narrow (_Integer.wildcardSuper ()), _List.narrow (_Object.wildcardSuper ()));
    assertIsNotAssignable (_List.narrow (_Integer.wildcardSuper ()), _List.narrow (_Integer.wildcard ()));
    assertIsNotAssignable (_List.narrow (_Integer.wildcard ()), _List.narrow (_Integer.wildcardSuper ()));

    assertIsNotAssignable (_List.narrow (_List), _List.narrow (_List.narrow (_Integer)));
    assertIsAssignable (_List.narrow (_List.wildcard ()), _List.narrow (_List.narrow (_Integer)));

    // List<? extends List<Object>> list1 = (List<List>)list2
    assertIsNotAssignable (_List.narrow (_List.narrow (_Object).wildcard ()), _List.narrow (_List));

    // List<? super List<List<List>>> list1 = (List<List<? super List>>)list2
    assertIsNotAssignable (_List.narrow (_List.narrow (_List.narrow (_List)).wildcardSuper ()),
                           _List.narrow (_List.narrow (_List.wildcardSuper ())));
  }

  @Test
  public void testIsAssignableFromRandomized ()
  {
    JCodeModel codeModel = new JCodeModel ();
    AbstractJClass _Object = codeModel.ref (Object.class);
    AbstractJClass _Integer = codeModel.ref (Integer.class);
    AbstractJClass _List = codeModel.ref (List.class);

    registerType (_Object);
    registerType (_Integer);
    registerType (_List);

    for (int i = 0; i < 2; i++)
    {
      for (AbstractJClass type: refreshTypes())
      {
        assertIsAssignable (_Object, type);
        assertIsAssignable (type, type);

        registerType (_List.narrow (type));
        registerType (_List.narrow (type.wildcard ()));
        registerType (_List.narrow (type.wildcardSuper ()));

        assertIsAssignableInTopLevelPositionOnly (_List.narrow (type), _List);
        assertIsAssignable (_List, _List.narrow (type));

        assertIsAssignable (_List.narrow (type), _List.narrow (type));
        assertIsAssignable (_List.narrow (type.wildcard ()), _List.narrow (type));
        assertIsNotAssignable (_List.narrow(type.wildcard ()), _List.narrow(type.wildcardSuper ()));
        assertIsNotAssignable (_List.narrow(type.wildcardSuper ()), _List.narrow(type.wildcard ()));
        assertIsAssignable (_List.narrow (type.wildcardSuper ()), _List.narrow (type));
      }
      for (AssignmentTypes assignment: refreshAssignableTypes())
      {
        if (!assignment.value.equals (assignment.variable))
        {
          assertIsNotAssignable (_List.narrow (assignment.variable), _List.narrow (assignment.value));
        }
        assertIsAssignable (_List.narrow (assignment.variable.wildcard ()), _List.narrow (assignment.value));
        assertIsAssignable (_List.narrow (assignment.variable.wildcard ()), _List.narrow (assignment.value.wildcard ()));
        assertIsAssignable (_List.narrow (assignment.value.wildcardSuper ()), _List.narrow (assignment.variable));
        assertIsAssignable (_List.narrow (assignment.value.wildcardSuper ()), _List.narrow (assignment.variable.wildcardSuper ()));
      }
    }
  }

  private static class AssignmentTypes
  {
    private final AbstractJClass variable;
    private final AbstractJClass value;

    public AssignmentTypes (AbstractJClass variable, AbstractJClass value)
    {
      this.variable = variable;
      this.value = value;
    }
  }
}
