package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
public interface SerializableInterface extends java.io.Serializable
{
	public int getIntValue();
	public void setIntValue(int intValue);
}
