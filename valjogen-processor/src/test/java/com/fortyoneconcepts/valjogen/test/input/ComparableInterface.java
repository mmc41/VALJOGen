package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface ComparableInterface extends Comparable<ComparableInterface>
{
	public int getIntValue();
	public String getStringValue();
	public String[] getStringValues();
}
