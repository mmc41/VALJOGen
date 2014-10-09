/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.*;

/**
* Simplest possible example that generates a default value object containing a hello message.
*/
@VALJOGenerate
public interface Hello
{
	public String getHelloMessage();
}
