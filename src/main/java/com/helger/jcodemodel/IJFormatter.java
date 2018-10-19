package com.helger.jcodemodel;

import java.io.Closeable;
import java.util.Collection;

import javax.annotation.Nonnull;

/**
 * Base interface for JFormatter.
 *
 * @author Philip Helger
 */
public interface IJFormatter extends Closeable
{
  /**
   * Special character token we use to differentiate '&gt;' as an operator and
   * '&gt;' as the end of the type arguments. The former uses '&gt;' and it
   * requires a preceding whitespace. The latter uses this, and it does not have
   * a preceding whitespace.
   */
  char CLOSE_TYPE_ARGS = '\uFFFF';

  /**
   * @return <code>true</code> if we are in the printing mode, where we actually
   *         produce text. The other (internal) mode is the "collecting mode".
   */
  boolean isPrinting ();

  /**
   * Increment the indentation level.
   *
   * @return this for chaining
   */
  @Nonnull
  IJFormatter indent ();

  /**
   * Decrement the indentation level.
   *
   * @return this for chaining
   */
  @Nonnull
  IJFormatter outdent ();

  /**
   * Print a new line into the stream
   *
   * @return this for chaining
   */
  @Nonnull
  IJFormatter newline ();

  /**
   * Print a char into the stream
   *
   * @param c
   *        the char
   * @return this for chaining
   */
  @Nonnull
  IJFormatter print (char c);

  @Nonnull
  default IJFormatter printCloseTypeArgs ()
  {
    return print (CLOSE_TYPE_ARGS);
  }

  /**
   * Print a String into the stream. Indentation happens automatically.
   *
   * @param sStr
   *        the String
   * @return this
   */
  @Nonnull
  IJFormatter print (@Nonnull String sStr);

  /**
   * Print a type name.
   * <p>
   * In the collecting mode we use this information to decide what types to
   * import and what not to.
   *
   * @param aType
   *        Type to be emitted
   * @return this for chaining
   */
  @Nonnull
  IJFormatter type (@Nonnull AbstractJClass aType);

  @Nonnull
  default IJFormatter type (@Nonnull final AbstractJType aType)
  {
    if (aType.isReference ())
      return type ((AbstractJClass) aType);
    return generable (aType);
  }

  /**
   * Cause the {@link JVar} to generate source for itself. With annotations,
   * type, name and init expression.
   *
   * @param aVar
   *        the {@link JVar} object
   * @return this for chaining
   */
  @Nonnull
  IJFormatter var (@Nonnull JVar aVar);

  /**
   * Print an identifier
   *
   * @param sID
   *        identifier
   * @return this for chaining
   */
  @Nonnull
  IJFormatter id (@Nonnull String sID);

  /**
   * Cause the {@link IJGenerable} object to generate source for itself
   *
   * @param aObj
   *        the object
   * @return this for chaining
   */
  @Nonnull
  IJFormatter generable (@Nonnull IJGenerable aObj);

  /**
   * Produces {@link IJGenerable}s separated by ','
   *
   * @param aList
   *        List of {@link IJGenerable} objects that will be separated by a
   *        comma
   * @return this for chaining
   */
  @Nonnull
  IJFormatter generable (@Nonnull final Collection <? extends IJGenerable> aList);

  /**
   * Cause the {@link IJStatement} to generate source for itself
   *
   * @param aObj
   *        the object
   * @return this for chaining
   */
  @Nonnull
  IJFormatter statement (@Nonnull IJStatement aObj);

  /**
   * Cause the {@link IJDeclaration} to generate source for itself
   *
   * @param aObj
   *        the object
   * @return this for chaining
   */
  @Nonnull
  IJFormatter declaration (@Nonnull IJDeclaration aObj);
}
