/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.List;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
@VALJOConfigure(baseClazzName="com.fortyoneconcepts.valjogen.test.input.ComparableBaseClassWithHashAndEquals")
public interface InterfaceWithComparableHashEqualsBaseClass
{
	public int getMyValue();
	public InterfaceWithComparableHashEqualsBaseClass setMyValue(int myValue);

	public List<Double> getMyDoubleList();
}
