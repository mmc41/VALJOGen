package com.fortyoneconcepts.valjogen.testsources;

import java.util.Arrays;
import java.util.Objects;

/**
 * {@inheritDoc}
 */

public final class SimpleClass implements SimpleInterface
{
  private final Object _object;
  private final String _string;

  public static SimpleClass valueOf(final Object _object, final String _string)
  {
    return new SimpleClass(_object, _string);
  }

  /**
   * Constructs an instance of SimpleClass
   */
  private SimpleClass(final Object _object, final String _string)
  {
    this._object=Objects.requireNonNull(_object);
    this._string=Objects.requireNonNull(_string);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Object getObject()
  {
    return _object;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getString()
  {
    return _string;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(_object,_string);
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;

    SimpleClass _other = (SimpleClass) obj;

    return (Objects.equals(_object, _other._object) && Objects.equals(_string, _other._string));
  }

  @Override
  public String toString()
  {
    final StringBuilder _sb = new StringBuilder();

    _sb.append("SimpleClass [");

    _sb.append("_object=");
    _sb.append(_object); 
    _sb.append(", ");
    _sb.append("_string=");
    _sb.append(_string); 

    _sb.append(']');

    return _sb.toString();
  }
}