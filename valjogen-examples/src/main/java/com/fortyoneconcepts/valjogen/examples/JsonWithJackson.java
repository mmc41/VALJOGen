/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.annotations.types.DataConversion;

/**
* Example that shows how to generate a mutable or immutable class with Jackson annotations, so it can be easily converted to/from JSON.
* If using JDK8, consider using {@link DataConversion#JACKSON_DATABIND_ANNOTATIONS_WITH_JDK8_PARAMETER_NAMES} instead.
*
* @see <a href="https://github.com/FasterXML/jackson-annotations">jackson-annotations</a>
*/
@VALJOGenerate(comment="Example 10")
@VALJOConfigure(dataConversion=DataConversion.JACKSON_DATABIND_ANNOTATIONS)
public interface JsonWithJackson
{
	public String getName();
	public int getAge();
}
