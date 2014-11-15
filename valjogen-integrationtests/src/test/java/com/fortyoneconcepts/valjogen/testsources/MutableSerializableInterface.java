/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.SerializableClass)
//Note that we inherit @VALJOConfigure configuration from package here!
public interface MutableSerializableInterface extends java.io.Serializable
{
	public String getValue();
	public void setValue(String value);
}