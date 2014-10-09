/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.*;

/**
* Simplest possible example that generates a default value object containing a hello message.
*
* NOTE: Comments are entirely optional but used for the websites documentation backend.
*/
@VALJOGenerate(comment="Example 1")
public interface Hello
{
	public String getHelloMessage();
}
