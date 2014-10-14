package com.fortyoneconcepts.valjogen.model;

/**
 * A model that has a single assoicated type like a  member or a parameter.
 *
 * @author mmc
 */
public interface TypedModel extends Model
{
	public Type getType();
}
