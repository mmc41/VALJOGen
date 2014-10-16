/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.SerializableWithBaseClass)
@VALJOConfigure(serialVersionUID=43, staticFactoryMethodEnabled=true, baseClazzName="com.fortyoneconcepts.valjogen.testsources.BaseClass")
public interface SerializableInterfaceWithBaseClass // Nb. base class implements java.io.Serializable
{
	public String getValue();
	public void setValue(String value);
}