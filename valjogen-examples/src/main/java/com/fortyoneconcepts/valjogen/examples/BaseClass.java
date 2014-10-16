/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

/**
* Example of a preexisting base class that generated code can extend from.
* Used by the "ExtendingByInheritance.java" example.
*
* Note that constructors of the generated class will delegate to available
* constructors in the base class.
*/
public class BaseClass
{
	protected final int baseValue;

	public BaseClass(int baseValue)
	{
		this.baseValue=baseValue;
	}
}
