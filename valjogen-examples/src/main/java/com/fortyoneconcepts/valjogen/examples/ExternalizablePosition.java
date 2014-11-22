/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to get an auto-implementation of externalizable.
*/
@VALJOGenerate(comment="Example 6")
@VALJOConfigure(serialVersionUID=42)
public interface ExternalizablePosition extends java.io.Externalizable
{
	public int getX();
	public int getY();
}
