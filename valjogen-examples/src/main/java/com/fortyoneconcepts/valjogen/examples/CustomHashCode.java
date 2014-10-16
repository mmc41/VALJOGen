/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to apply a custom template to change the generated code for known methods. In this case to support caching of hashCode.
*/
@VALJOGenerate(comment="Example 12")
@VALJOConfigure(customJavaTemplateFileName="custom_hashCode.stg")
public interface CustomHashCode
{
	public String getFirstName();
	public String getLastName();
}
