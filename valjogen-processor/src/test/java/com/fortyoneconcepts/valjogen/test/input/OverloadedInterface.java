/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
@VALJOConfigure(customTemplateFileName="custom_overload.stg", implementedMethodNames= { "customMethod(String,int)" })
public interface OverloadedInterface
{
	public void customMethod(int intValue);
	public void customMethod(String stringValue);
	public OverloadedInterface customMethod(String stringValue, int intValue);
}
