/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.ExternalizableMutableWithBaseClass)
@VALJOConfigure(baseClazzName="com.fortyoneconcepts.valjogen.testsources.ExternalizableBaseClass")
public interface ExternalizableMutableInterfaceWithBaseClass
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
}
