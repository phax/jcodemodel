package com.helger.jcodemodel.util;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

/**
 * @author Ben Fagin
 * @version 2013-04-01
 */
public final class NameUtilities
{
  private NameUtilities ()
  {}

  @Nonnull
  public static String getFullName (@Nonnull final Class <?> c)
  {
    if (c == null)
      throw new IllegalArgumentException ("class cannot be null");

    final StringBuilder name = new StringBuilder ();
    name.append (c.getPackage ().getName ()).append (".");

    Class <?> klaus = c;
    final List <Class <?>> enclosingClasses = new ArrayList <Class <?>> ();
    while ((klaus = klaus.getEnclosingClass ()) != null)
      enclosingClasses.add (klaus);

    // Back to front
    for (int i = enclosingClasses.size () - 1; i >= 0; i--)
      name.append (enclosingClasses.get (i).getSimpleName ()).append (".");

    name.append (c.getSimpleName ());
    return name.toString ();
  }
}
