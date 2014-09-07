/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface InterfaceWithDefaultMethod
{
	public default String getDefMethod()
	{
		return "defaultValue";
	}

	public int getValue();
}
