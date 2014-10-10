/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.io.ObjectInputValidation;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
@VALJOConfigure(customTemplateFileName="custom_serializable.stg")
public interface SerializableInterface extends java.io.Serializable, ObjectInputValidation
{
	public int getIntValue();
	public void setIntValue(int intValue);
}
