/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to apply a basic custom template implementing a simple custom method. A custom method that can tell when a property was last changed.
* Example also shows how to make the generated mutable class thread safe.
*/
@VALJOGenerate(comment="Example 21")
@VALJOConfigure(customJavaTemplateFileName="simple_custom_method.stg", synchronizedAccessEnabled=true)
public interface SimpleCustomMethod
{
	public String getName();
	public void setName(String name);

	// Custom method here:
	public long nanoLastUpdated();
}
