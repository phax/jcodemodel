/*
 * Copyright 2015 Philip Helger.
 */
package com.helger.jcodemodel;

/**
 *
 * @author Victor Nazarov <asviraspossible@gmail.com>
 */
public class JErrorClassUsedException extends UnsupportedOperationException {
  private static final long serialVersionUID = 1L;

  JErrorClassUsedException (String message)
  {
    super (message);
  }

}
