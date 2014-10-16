/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

/**
* Example of a preexisting base class that generated code can extend from.
* Used by the "ExtendingByInheritance.java" example.
*
* Note that constructors of the generated class will delegate to available
* constructors in the base class. Also keep it mind that if you need
* serialization base classes must be serializable for any generated
* subclasses like ours (without a default constructor) to be serializable.
*/
public class BaseClass
{
	protected final int baseValue;

	public BaseClass(int baseValue)
	{
		this.baseValue=baseValue;
	}
}
