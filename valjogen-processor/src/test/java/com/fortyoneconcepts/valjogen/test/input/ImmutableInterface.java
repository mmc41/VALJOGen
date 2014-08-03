package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
public interface ImmutableInterface
{
	public int getValue();
	public ImmutableInterface setValue(int v);
}
