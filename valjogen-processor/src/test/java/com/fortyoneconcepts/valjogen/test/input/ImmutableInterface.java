/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.annotations.types.*;

@VALJOGenerate
@VALJOConfigure(mutability=Mutability.Immutable)
public interface ImmutableInterface
{
	public int getIntValue();
	public ImmutableInterface setIntValue(int intValue);

	public Object getObjectValue();
	public ImmutableInterface setObjectValue(Object objectValue);
}
