/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
@VALJOConfigure(getterPrefixes={ "should" }, setterPrefixes= {"with"})
public interface CustomNamedPropertiesInterface
{
	public boolean shouldRequire();
	public CustomNamedPropertiesInterface withRequire(boolean require);
}
