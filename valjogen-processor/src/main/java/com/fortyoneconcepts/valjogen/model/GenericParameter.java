package com.fortyoneconcepts.valjogen.model;

import java.util.Objects;

public class GenericParameter implements Model
{
	private final Clazz clazzUsingParam;
	private final String genericParameterName;

	public GenericParameter(Clazz clazzUsingParam, String genericParameterName)
	{
		this.clazzUsingParam=Objects.requireNonNull(clazzUsingParam);
		this.genericParameterName=Objects.requireNonNull(genericParameterName);
	}

	@Override
	public Configuration getConfiguration()
	{
		return clazzUsingParam.getConfiguration();
	}

	@Override
	public HelperTypes getHelperTypes()
	{
		return clazzUsingParam.getHelperTypes();
	}

	@Override
	public String getPackageName()
	{
		return clazzUsingParam.getPackageName();
	}

	@Override
	public Clazz getClazz()
	{
		return clazzUsingParam;
	}

	@Override
	public String toString() {
		return "GenericParameter [this=@"+ Integer.toHexString(System.identityHashCode(this))+", genericParameterName = "+genericParameterName+ "]";
	}
}
