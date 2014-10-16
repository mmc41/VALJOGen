/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how use standard subclassing for customization. Generated output will inheriting from the specified base
* class (see "BaseClass.java"). In addition the generated abstract class can be futher customized when subclassing it.
*/
@VALJOGenerate(comment="Example 6")
@VALJOConfigure(clazzModifiers= {"ABSTRACT"}, baseClazzName="com.fortyoneconcepts.valjogen.examples.BaseClass")
public interface ExtendingByInheritance
{
	public String getGeneratedProperty();
}
