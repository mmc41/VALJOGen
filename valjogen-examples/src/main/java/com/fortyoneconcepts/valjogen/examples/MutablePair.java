/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
public interface MutablePair
{
	public Object getLeft();
	public void setLeft(Object left);

	public Object getRight();
	public void setRight(Object right);
}
