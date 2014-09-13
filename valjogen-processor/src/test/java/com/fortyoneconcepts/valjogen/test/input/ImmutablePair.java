/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

// @VALJOGenerate
public interface ImmutablePair<X,Y>
{
	public X getLeft();
	public ImmutablePair<X,Y> setLeft(X left);

	public Y getRight();
	public ImmutablePair<X,Y> setRight(Y right);
}
