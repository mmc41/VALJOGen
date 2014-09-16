package com.fortyoneconcepts.valjogen.model;

public abstract class ModelBase implements Model
{
	protected final static int MAX_RECURSIVE_LEVEL = 5;
	protected final static int NO_DETAILS_LEVEL = MAX_RECURSIVE_LEVEL+1;

	public final String toString()
	{
		return toString(0);
	}

	public abstract String toString(int level);
}
