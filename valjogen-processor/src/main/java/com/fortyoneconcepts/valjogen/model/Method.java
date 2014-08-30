/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;

/***
 * Meta-information about a method that should be generated (implemented).
 *
 * @author mmc
 */
public class Method extends ModelBase
{
	protected final Clazz clazz;
	protected final Type declaringType;
	protected final String methodName;
	protected final List<Parameter> parameters;
	protected final String javaDoc;
	protected final Type returnType;
	protected final boolean implementationClaimed;

	public Method(Clazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, String javaDoc, boolean implementationClaimed)
	{
	    this.clazz = Objects.requireNonNull(clazz);
	    this.declaringType = Objects.requireNonNull(declaringType);
		this.methodName = Objects.requireNonNull(methodName);
		this.parameters = Objects.requireNonNull(parameters);
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.returnType = Objects.requireNonNull(returnType);
		this.implementationClaimed = implementationClaimed;
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

	public boolean isImplementationClaimed()
	{
		return implementationClaimed;
	}

	public boolean isFinal()
	{
		return false;
	}

	public boolean isThisReturnType()
	{
		return clazz.getInterfaceTypes().stream().anyMatch(t -> t.equals(returnType));
	}

	public Type getReturnType()
	{
		return returnType;
	}

	public String getName()
	{
		return methodName;
	}

	public List<Parameter> getParameters()
	{
		return parameters;
	}

	public String getJavaDoc()
	{
		return javaDoc;
	}

	@Override
	public String toString(int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Method [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
			sb.append(", declaringType="+declaringType.getName()+", methodName=" + getName() + ", parameters="+parameters+", returnType="+returnType.getName() + ", implementationClaimed="+implementationClaimed+"]");

		sb.append("]");

		return sb.toString();
	}
}
