/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
@VALJOConfigure(headerFileName="Header.txt")
public interface AnnotatedInterfaceWithHeader
{
	public Object getObjectValue();
}
