/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
@VALJOConfigure
public interface ExternalizableMutableInterface extends java.io.Externalizable
{
	public byte getByte();
	public void setByte(byte value);

	public int getInt();
	public void setInt(int value);

	public long getLong();
	public void setLong(long value);

	public char getChar();
	public void setChar(char value);

	public boolean isBoolean();
	public void setBoolean(boolean value);

	public float getFloat();
	public void setFloat(float value);

	public double getDouble();
	public void setDouble(double value);

	public String getString();
	public void setString(String value);

	public Object getObject();
	public void setObject(Object value);

	public byte[] getByteArray();
	public void setByteArray(byte[] value);

	public Object[] getObjectArray();
	public void setObjectArray(Object[] value);
}
