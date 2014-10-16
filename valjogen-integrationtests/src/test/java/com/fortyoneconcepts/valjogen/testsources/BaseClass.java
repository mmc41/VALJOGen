package com.fortyoneconcepts.valjogen.testsources;

public class BaseClass implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;

	protected final int baseValue;

	protected BaseClass(int baseValue)
	{
		this.baseValue=baseValue;
	}
}
