/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.annotations.types.*;

@VALJOGenerate
@VALJOConfigure(mutability=Mutability.Mutable)
public interface MutableInterface
{
	public int getIntValue();
	public void setIntValue(int intValue);

	public Object getObjectValue();
	public void setObjectValue(Object objectValue);
}
