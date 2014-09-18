package com.fortyoneconcepts.valjogen.testsources;

import java.util.Arrays;
import java.util.Objects;
import javax.annotation.Generated;

@Generated(value = "com.fortyoneconcepts.valjogen", date="2014-09-17T08:19Z", comments="Generated by ValjoGen code generator (ValjoGen.41concepts.com) from SerializableInterface")
public final class SerializableClass implements SerializableInterface
{
  private static final long serialVersionUID = 42;

  private String value;

  public static SerializableClass valueOf(final String value)
  {
    SerializableClass _instance = new SerializableClass(value);
    return _instance;
  }

  private SerializableClass(final String value)
  {
    this.value=Objects.requireNonNull(value);
  }

  /**
  * {@inheritDoc}
  */
  @Override
  public final String getValue()
  {
   return value;
  }

  /**
  * {@inheritDoc}
  */
  @Override
  public final void setValue(final String value)
  {
   this.value=Objects.requireNonNull(value);
  }

  /**
  * {@inheritDoc}
  */
  @Override
  public int hashCode()
  {
    int _result = Objects.hash(value);
    return _result;
  }

  /**
  * {@inheritDoc}
  */
  @Override
  public boolean equals(final Object arg0)
  {
    if (this == arg0)
      return true;

    if (arg0 == null)
      return false;

    if (getClass() != arg0.getClass())
      return false;

    @SuppressWarnings("unchecked")
    SerializableClass _other = (SerializableClass) arg0;

    return (Objects.equals(value, _other.value));
  }

  /**
  * {@inheritDoc}
  */
  @Override
  public String toString()
  {
    final StringBuilder _sb = new StringBuilder();
    _sb.append("SerializableClass [");
    _sb.append("value=");
    _sb.append(value); 
    _sb.append(']');
    return _sb.toString();
  }
}