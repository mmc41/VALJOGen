/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.CustomTemplateClass)
@VALJOConfigure(customJavaTemplateFileName="custom_template.stg", staticFactoryMethodEnabled=true)
public interface CustomTemplateInterface
{
	public Object getObject();
}