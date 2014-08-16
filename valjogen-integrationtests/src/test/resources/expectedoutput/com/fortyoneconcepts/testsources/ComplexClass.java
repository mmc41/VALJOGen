package com.fortyoneconcepts.valjogen.testsources;

import java.util.Arrays;
import java.util.Objects;

/**
 * {@inheritDoc}
 */

public final class ComplexClass implements ComplexInterfaceWithAllTypes
{
  private final ComplexInterfaceWithAllTypes other;
  private final Object _object;
  private final String _string;
  private final java.util.Date date;
  private final Object[] objectArray;
  private final byte _byte;
  private final int _int;
  private final long _long;
  private final char _char;
  private final boolean _boolean;
  private final float _float;
  private final double _double;
  private final byte[] byteArray;
  private final int[] intArray;
  private final long[] longArray;
  private final char[] charArray;
  private final boolean[] booleanArray;
  private final float[] floatArray;
  private final double[] doubleArray;

  public static ComplexClass valueOf(final ComplexInterfaceWithAllTypes other, final Object _object, final String _string, final java.util.Date date, final Object[] objectArray, final byte _byte, final int _int, final long _long, final char _char, final boolean _boolean, final float _float, final double _double, final byte[] byteArray, final int[] intArray, final long[] longArray, final char[] charArray, final boolean[] booleanArray, final float[] floatArray, final double[] doubleArray)
  {
    return new ComplexClass(other, _object, _string, date, objectArray, _byte, _int, _long, _char, _boolean, _float, _double, byteArray, intArray, longArray, charArray, booleanArray, floatArray, doubleArray);
  }

  /**
   * Constructs an instance of ComplexClass
   */
  private ComplexClass(final ComplexInterfaceWithAllTypes other, final Object _object, final String _string, final java.util.Date date, final Object[] objectArray, final byte _byte, final int _int, final long _long, final char _char, final boolean _boolean, final float _float, final double _double, final byte[] byteArray, final int[] intArray, final long[] longArray, final char[] charArray, final boolean[] booleanArray, final float[] floatArray, final double[] doubleArray)
  {
    this.other=Objects.requireNonNull(other);
    this._object=Objects.requireNonNull(_object);
    this._string=Objects.requireNonNull(_string);
    this.date=Objects.requireNonNull(date);
    this.objectArray=Objects.requireNonNull(objectArray);
    this._byte=_byte;
    this._int=_int;
    this._long=_long;
    this._char=_char;
    this._boolean=_boolean;
    this._float=_float;
    this._double=_double;
    this.byteArray=Objects.requireNonNull(byteArray);
    this.intArray=Objects.requireNonNull(intArray);
    this.longArray=Objects.requireNonNull(longArray);
    this.charArray=Objects.requireNonNull(charArray);
    this.booleanArray=Objects.requireNonNull(booleanArray);
    this.floatArray=Objects.requireNonNull(floatArray);
    this.doubleArray=Objects.requireNonNull(doubleArray);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final ComplexInterfaceWithAllTypes getOther()
  {
    return other;
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

  /**
   * {@inheritDoc}
   */
  @Override
  public final java.util.Date getDate()
  {
    return date;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Object[] getObjectArray()
  {
    return objectArray;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final byte getByte()
  {
    return _byte;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int getInt()
  {
    return _int;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final long getLong()
  {
    return _long;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final char getChar()
  {
    return _char;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean isBoolean()
  {
    return _boolean;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final float getFloat()
  {
    return _float;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final double getDouble()
  {
    return _double;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final byte[] getByteArray()
  {
    return byteArray;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final int[] getIntArray()
  {
    return intArray;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final long[] getLongArray()
  {
    return longArray;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final char[] getCharArray()
  {
    return charArray;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final boolean[] getBooleanArray()
  {
    return booleanArray;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final float[] getFloatArray()
  {
    return floatArray;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final double[] getDoubleArray()
  {
    return doubleArray;
  }

  @Override
  public int hashCode()
  {
    final int _prime = 31;
    int _result = 1;
    _result = _prime * _result + Objects.hashCode(other); 
    _result = _prime * _result + Objects.hashCode(_object); 
    _result = _prime * _result + Objects.hashCode(_string); 
    _result = _prime * _result + Objects.hashCode(date); 
    _result = _prime * _result + Arrays.hashCode(objectArray); 
    _result = _prime * _result + Byte.hashCode(_byte); 
    _result = _prime * _result + Integer.hashCode(_int); 
    _result = _prime * _result + Long.hashCode(_long); 
    _result = _prime * _result + Character.hashCode(_char); 
    _result = _prime * _result + Boolean.hashCode(_boolean); 
    _result = _prime * _result + Float.hashCode(_float); 
    _result = _prime * _result + Double.hashCode(_double); 
    _result = _prime * _result + Arrays.hashCode(byteArray); 
    _result = _prime * _result + Arrays.hashCode(intArray); 
    _result = _prime * _result + Arrays.hashCode(longArray); 
    _result = _prime * _result + Arrays.hashCode(charArray); 
    _result = _prime * _result + Arrays.hashCode(booleanArray); 
    _result = _prime * _result + Arrays.hashCode(floatArray); 
    _result = _prime * _result + Arrays.hashCode(doubleArray); 
    return _result;
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

    ComplexClass _other = (ComplexClass) obj;

    return (Objects.equals(other, _other.other) && Objects.equals(_object, _other._object) && Objects.equals(_string, _other._string) && Objects.equals(date, _other.date) && Arrays.equals(objectArray, _other.objectArray) && (_byte == _other._byte) && (_int == _other._int) && (_long == _other._long) && (_char == _other._char) && (_boolean == _other._boolean) && (Float.floatToIntBits(_float) == Float.floatToIntBits(_other._float)) && (Double.doubleToLongBits(_double) == Double.doubleToLongBits(_other._double)) && Arrays.equals(byteArray, _other.byteArray) && Arrays.equals(intArray, _other.intArray) && Arrays.equals(longArray, _other.longArray) && Arrays.equals(charArray, _other.charArray) && Arrays.equals(booleanArray, _other.booleanArray) && Arrays.equals(floatArray, _other.floatArray) && Arrays.equals(doubleArray, _other.doubleArray));
  }

  @Override
  public String toString()
  {
    final StringBuilder _sb = new StringBuilder();

    _sb.append("ComplexClass [");

    _sb.append("other=");
    _sb.append(other); 
    _sb.append(", ");
    _sb.append("_object=");
    _sb.append(_object); 
    _sb.append(", ");
    _sb.append("_string=");
    _sb.append(_string); 
    _sb.append(", ");
    _sb.append("date=");
    _sb.append(date); 
    _sb.append(", ");
    _sb.append("objectArray=");
    _sb.append(Arrays.toString(objectArray)); 
    _sb.append(", ");
    _sb.append("_byte=");
    _sb.append(_byte); 
    _sb.append(", ");
    _sb.append("_int=");
    _sb.append(_int); 
    _sb.append(", ");
    _sb.append("_long=");
    _sb.append(_long); 
    _sb.append(", ");
    _sb.append("_char=");
    _sb.append(_char); 
    _sb.append(", ");
    _sb.append("_boolean=");
    _sb.append(_boolean); 
    _sb.append(", ");
    _sb.append("_float=");
    _sb.append(_float); 
    _sb.append(", ");
    _sb.append("_double=");
    _sb.append(_double); 
    _sb.append(", ");
    _sb.append("byteArray=");
    _sb.append(Arrays.toString(byteArray)); 
    _sb.append(", ");
    _sb.append("intArray=");
    _sb.append(Arrays.toString(intArray)); 
    _sb.append(", ");
    _sb.append("longArray=");
    _sb.append(Arrays.toString(longArray)); 
    _sb.append(", ");
    _sb.append("charArray=");
    _sb.append(Arrays.toString(charArray)); 
    _sb.append(", ");
    _sb.append("booleanArray=");
    _sb.append(Arrays.toString(booleanArray)); 
    _sb.append(", ");
    _sb.append("floatArray=");
    _sb.append(Arrays.toString(floatArray)); 
    _sb.append(", ");
    _sb.append("doubleArray=");
    _sb.append(Arrays.toString(doubleArray)); 

    _sb.append(']');

    return _sb.toString();
  }
}