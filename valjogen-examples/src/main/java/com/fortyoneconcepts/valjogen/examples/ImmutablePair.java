/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.annotations.types.Mutability;

/**
* Example that shows how to generate a simple immutable pair class.
* Note the tiny difference compared to the mutable pair class example.
*
* @param <TLeft> First object type in pair.
* @param <TRight> Second object type in pair.
*/
@VALJOGenerate(comment="Example 4")
@VALJOConfigure(mutability=Mutability.Immutable)
public interface ImmutablePair<TLeft, TRight>
{
	public TLeft getLeft();
	public ImmutablePair<TLeft, TRight> setLeft(TLeft left);

	public TRight getRight();
	public ImmutablePair<TLeft, TRight> setRight(TRight right);
}
