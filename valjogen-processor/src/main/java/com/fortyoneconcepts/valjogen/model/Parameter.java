/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Objects;

/**
 * Meta-information about a formal parameter for a method/property.
 *
 * @author mmc
 */
public class Parameter implements Model
{
	private final Clazz clazz;
	private final String paramName;
	private final Type type;

	public Parameter(Clazz clazz, Type paramType, String paramName)
	{
		this.clazz=Objects.requireNonNull(clazz);
		this.paramName=Objects.requireNonNull(paramName);
		this.type=Objects.requireNonNull(paramType);
	}

	@Override
	public Configuration getConfiguration()
	{
		return clazz.getConfiguration();
	}

	@Override
	public HelperTypes getHelperTypes()
	{
		return clazz.getHelperTypes();
	}

	@Override
	public Clazz getClazz()
	{
		return clazz;
	}

	@Override
	public String getPackageName()
	{
		return clazz.getPackageName();
	}

	public String getName() {
		return paramName;
	}

	public Type getType()
	{
	    return type;
	}

	@Override
	public String toString() {
		return "Parameter [clazz = "+clazz.getName()+", name=" + getName() + ", type=" + getType().getName() + "]";
	}
}
