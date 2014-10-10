package com.fortyoneconcepts.valjogen.model;

public abstract class ModelBase implements Model
{
	protected final static int MAX_RECURSIVE_LEVEL = 5;
	protected final static int NO_DETAILS_LEVEL = MAX_RECURSIVE_LEVEL+1;

	/**
	 * A special toString implementation that forward class to our recursive-safe toString.
	 * @return Safe string representation of the object (possibly cut-of).
	 */
	public final String toString()
	{
		return toString(0);
	}
}
