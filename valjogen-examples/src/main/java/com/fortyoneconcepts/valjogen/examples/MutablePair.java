/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
public interface MutablePair<TLeft, TRight>
{
	public TLeft getLeft();
	public void setLeft(TLeft left);

	public TRight getRight();
	public void setRight(TRight right);
}
