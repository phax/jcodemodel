package com.helger.jcodemodel;

import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.helger.jcodemodel.util.JCNameUtilities;

/**
 * References to existing classes.
 * <p>
 * {@link JReferencedClass} is kept in a pool so that they are shared. There is
 * one pool for each {@link JCodeModel} object.
 * <p>
 * It is impossible to cache JReferencedClass globally only because there is the
 * {@link #_package()} method, which obtains the owner {@link JPackage} object,
 * which is scoped to JCodeModel.
 */
class JReferencedClass extends AbstractJClass implements IJDeclaration
{
  private final Class <?> m_aClass;

  JReferencedClass (@Nonnull final JCodeModel aOwner, @Nonnull final Class <?> aClass)
  {
    super (aOwner);
    m_aClass = aClass;
    assert !m_aClass.isArray ();
  }

  @Override
  public String name ()
  {
    return m_aClass.getSimpleName ();
  }

  @Override
  @Nonnull
  public String fullName ()
  {
    return JCNameUtilities.getFullName (m_aClass);
  }

  @Override
  public String binaryName ()
  {
    return m_aClass.getName ();
  }

  @Override
  public AbstractJClass outer ()
  {
    final Class <?> p = m_aClass.getDeclaringClass ();
    if (p == null)
      return null;
    return owner ().ref (p);
  }

  @Override
  @Nonnull
  public JPackage _package ()
  {
    final String name = fullName ();

    // this type is array
    if (name.indexOf ('[') != -1)
      return owner ()._package ("");

    // other normal case
    final int idx = name.lastIndexOf ('.');
    if (idx < 0)
      return owner ()._package ("");
    return owner ()._package (name.substring (0, idx));
  }

  @Override
  public AbstractJClass _extends ()
  {
    final Class <?> sp = m_aClass.getSuperclass ();
    if (sp == null)
    {
      if (isInterface ())
        return owner ().ref (Object.class);
      return null;
    }
    return owner ().ref (sp);
  }

  @Override
  public Iterator <AbstractJClass> _implements ()
  {
    final Class <?> [] aInterfaces = m_aClass.getInterfaces ();
    return new Iterator <AbstractJClass> ()
    {
      private int m_nIdx = 0;

      public boolean hasNext ()
      {
        return m_nIdx < aInterfaces.length;
      }

      @Nonnull
      public AbstractJClass next ()
      {
        return owner ().ref (aInterfaces[m_nIdx++]);
      }

      public void remove ()
      {
        throw new UnsupportedOperationException ();
      }
    };
  }

  @Override
  public boolean isInterface ()
  {
    return m_aClass.isInterface ();
  }

  @Override
  public boolean isAbstract ()
  {
    return Modifier.isAbstract (m_aClass.getModifiers ());
  }

  @Override
  @Nullable
  public JPrimitiveType getPrimitiveType ()
  {
    final Class <?> v = JCodeModel.boxToPrimitive.get (m_aClass);
    if (v != null)
      return AbstractJType.parse (owner (), v.getName ());
    return null;
  }

  public void declare (final JFormatter f)
  {
    // Nothing to do here...
  }

  @Override
  public JTypeVar [] typeParams ()
  {
    // TODO: does JDK 1.5 reflection provides these information?
    return super.typeParams ();
  }

  @Override
  protected AbstractJClass substituteParams (final JTypeVar [] variables,
                                             final List <? extends AbstractJClass> bindings)
  {
    // TODO: does JDK 1.5 reflection provides these information?
    return this;
  }
}
