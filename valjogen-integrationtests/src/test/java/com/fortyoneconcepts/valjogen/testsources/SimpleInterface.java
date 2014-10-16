/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.SimpleClass)
//Note that we inherit @VALJOConfigure configuration from package here.
public interface SimpleInterface
{
	public Object getObject();
	public String getString();
}