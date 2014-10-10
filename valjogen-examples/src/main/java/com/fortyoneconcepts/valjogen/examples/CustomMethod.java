/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to apply a custom template implementing a custom method.
* Example also shows how to make the generated mutable class thread safe.
*/
@VALJOGenerate(comment="Example 9")
@VALJOConfigure(customTemplateFileName="custom_method.stg", synchronizedAccessEnabled=true)
public interface CustomMethod
{
	public String getName();
	public void setName(String name);

	public long nanoLastUpdated();
}
