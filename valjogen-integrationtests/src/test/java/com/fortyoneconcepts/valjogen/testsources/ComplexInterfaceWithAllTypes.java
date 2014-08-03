/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.ComplexClass)
public interface ComplexInterfaceWithAllTypes
{
	public ComplexInterfaceWithAllTypes getOther();
	public Object getObject();
	public String getString();
	public java.util.Date getDate();

	public Object[] getObjectArray();
	// public Object[][] getObjectMultiArray();

	public byte getByte();
	public int getInt();
	public long getLong();
	public char getChar();
	public boolean isBoolean();
	public float getFloat();
	public double getDouble();

	public byte[] getByteArray();
	public int[] getIntArray();
	public long[] getLongArray();
	public char[] getCharArray();
	public boolean[] getBooleanArray();
	public float[] getFloatArray();
	public double[] getDoubleArray();
/*
	public byte[][] getByteMultiArray();
	public int[][] getIntMultiArray();
	public long[][] getLongMultiArray();
	public char[][] getCharMultiArray();
	public boolean[][] getBooleanMultiArray();
	public float[][] getFloatMultiArray();
	public double[][] getDoubleMultiArray();
	*/
}