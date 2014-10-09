/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to specify serialization ID for serialiable classes.
*/
@VALJOGenerate(comment="Example 5")
@VALJOConfigure(serialVersionUID=42)
public interface SerializablePosition extends java.io.Serializable
{
	public int getX();
	public int getY();
}
