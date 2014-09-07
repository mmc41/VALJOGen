/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
@VALJOConfigure(getterPrefixes={ "should" }, setterPrefixes= {"with"})
public interface CustomPropertiesInterface
{
	public boolean shouldRequire();
	public CustomPropertiesInterface withRequire(boolean require);
}
