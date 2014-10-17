/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Describes how detailed the model describes the type. BasicClazz are more detailed then ObjectType.
 *
 * @author mmc
 */
public enum DetailLevel
{
	Low,
	High;

	public final boolean hasLowerDetailThen(DetailLevel other)
	{
		return this.ordinal()<other.ordinal();
	}
}
