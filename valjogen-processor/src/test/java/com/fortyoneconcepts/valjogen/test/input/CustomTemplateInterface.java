/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
@VALJOConfigure(customTemplateFileName="custom_template.stg")
public interface CustomTemplateInterface
{
	public String getMutableObject();
	public void setMutableObject(String o);

	public String getImmutableObject();
	public CustomTemplateInterface setImmutableObject(String o);
}