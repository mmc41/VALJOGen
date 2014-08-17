package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
public interface ImmutableInterface
{
	public int getIntValue();
	public ImmutableInterface setIntValue(int intValue);

	public Object getObjectValue();
	public ImmutableInterface setObjectValue(Object objectValue);
}
