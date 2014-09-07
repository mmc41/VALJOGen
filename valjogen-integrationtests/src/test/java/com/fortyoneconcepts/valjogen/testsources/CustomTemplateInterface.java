/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.CustomTemplateClass)
@VALJOConfigure(customTemplateFileName="custom_template.stg")
public interface CustomTemplateInterface
{
	public Object getObject();
}