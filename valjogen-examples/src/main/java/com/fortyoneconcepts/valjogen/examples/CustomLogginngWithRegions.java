/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to apply a simple custom template to provide constructor logging by overriding existing regions.
*/
@VALJOGenerate(comment="Example 20")
@VALJOConfigure(customJavaTemplateFileName="custom_logging.stg", synchronizedAccessEnabled=true)
public interface CustomLogginngWithRegions
{
	public String getName();
	public void setName(String name);
}
