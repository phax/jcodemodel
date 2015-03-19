/*
 * Copyright 2015 Philip Helger.
 */
package com.helger.jcodemodel;

/**
 * This exception purely indicates, that the {@link JErrorClass} is used which
 * is never intended.
 *
 * @author Victor Nazarov &lt;asviraspossible@gmail.com&gt;
 */
public class JErrorClassUsedException extends UnsupportedOperationException
{
  private static final long serialVersionUID = 1L;

  JErrorClassUsedException (final String message)
  {
    super (message);
  }
}
