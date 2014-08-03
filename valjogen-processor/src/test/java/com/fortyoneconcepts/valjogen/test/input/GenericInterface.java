package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface GenericInterface<T> extends Comparable<T>
{
	public T getWrapped();
}
