/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.annotations.types.DataConversion;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

@VALJOGenerate(name=TestClassConstants.SerializableWithBaseClass)
@VALJOConfigure(serialVersionUID=43, staticFactoryMethodEnabled=true, baseClazzName="com.fortyoneconcepts.valjogen.testsources.BaseClass", dataConversion=DataConversion.JACKSON_DATABIND_ANNOTATIONS)
public interface ImmutableSerializableInterfaceWithBaseClass // Serializable through base class.
{
	public String getValue();
}