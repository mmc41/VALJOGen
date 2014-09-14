/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
public interface ImmutablePair<TLeft, TRight>
{
	public TLeft getLeft();
	public ImmutablePair<TLeft, TRight> setLeft(TLeft left);

	public TRight getRight();
	public ImmutablePair<TLeft, TRight> setRight(TRight right);
}
