/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.List;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
@VALJOConfigure(baseClazzName="com.fortyoneconcepts.valjogen.test.input.AbstractComparableBaseClass", comparableMembers= {"intField", "_float"})
public interface InterfaceWithAbstractComparableBaseClass
{
	public int getMyValue();
	public InterfaceWithAbstractComparableBaseClass setMyValue(int myValue);

	public List<Double> getMyDoubleList();
}
