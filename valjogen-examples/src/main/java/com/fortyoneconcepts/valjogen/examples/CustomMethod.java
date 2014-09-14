/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
@VALJOConfigure(customTemplateFileName="custom_method.stg", implementedMethodNames={"nanoLastUpdated"}, synchronizedAccessEnabled=true)
public interface CustomMethod
{
	public String getName();
	public void setName(String name);

	public long nanoLastUpdated();
}
