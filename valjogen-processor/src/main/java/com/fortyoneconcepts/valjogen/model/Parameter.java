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
public class Parameter extends ModelBase
{
	private final BasicClazz clazz;
	private final String paramName;
	private final Type type;
	private final Type erasedParamType;

	public Parameter(BasicClazz clazz, Type paramType, Type erasedParamType, String paramName)
	{
		this.clazz=Objects.requireNonNull(clazz);
		this.paramName=Objects.requireNonNull(paramName);
		this.type=Objects.requireNonNull(paramType);
		this.erasedParamType=Objects.requireNonNull(erasedParamType);
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
	public BasicClazz getClazz()
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

	public Parameter setName(String newParamName)
	{
		return new Parameter(clazz, type, erasedParamType, newParamName);
	}

	public Type getType()
	{
	    return type;
	}

	public Type getErasedType()
	{
	    return erasedParamType;
	}

	@Override
	public String toString(int level) {
		return "Parameter [name=" + paramName + ", type=" + type.getPrototypicalQualifiedName() + ", erasedType=" + erasedParamType.getPrototypicalQualifiedName() +"]";
	}
}
