/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
@VALJOConfigure(customTemplateFileName="custom_template.stg")
public interface CustomTemplateInterface
{
	public Object getMutableObject();
	public void setMutableObject(Object o);

	public Object getImmutableObject();
	public CustomTemplateInterface setImmutableObject(Object o);
}