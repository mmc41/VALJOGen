/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

/**
 * This is documentation for the interface.
 *
 * @author mmc
 */
@VALJOGenerate
@VALJOConfigure(headerFileName="Header.txt")
public interface AnnotatedInterfaceWithHeader
{
	/**
	 * This is documentation for the method.
	 *
	 * @return something
	 */
	public Object getObjectValue();
}
