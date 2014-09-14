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
	protected final AccessLevel accessLevel;
	protected final Type declaringType;
	protected final String methodName;
	protected final List<Parameter> parameters;
	protected final List<Type> thrownTypes;
	protected final String javaDoc;
	protected final Type returnType;
	protected ImplementationInfo implementationInfo;

	public Method(Clazz clazz, AccessLevel accessLevel, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, ImplementationInfo implementationInfo)
	{
	    this.clazz = Objects.requireNonNull(clazz);
	    this.accessLevel = Objects.requireNonNull(accessLevel);
	    this.declaringType = Objects.requireNonNull(declaringType);
		this.methodName = Objects.requireNonNull(methodName);
		this.parameters = Objects.requireNonNull(parameters);
		this.thrownTypes = Objects.requireNonNull(thrownTypes);
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.returnType = Objects.requireNonNull(returnType);
		this.implementationInfo = implementationInfo;
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

	public ImplementationInfo getImplementationInfo()
	{
		return implementationInfo;
	}

	public void setImplementationInfo(ImplementationInfo implementationInfo)
	{
		this.implementationInfo=implementationInfo;
	}

	public AccessLevel getAccessLevel()
	{
		return accessLevel;
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

	public List<Type> getThrownTypes()
	{
		return thrownTypes;
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
			sb.append(", accessLevel="+accessLevel+", declaringType="+declaringType.getName()+", methodName=" + getName() + ", parameters="+parameters+", returnType="+returnType.getName() + ", thrownTypes="+thrownTypes+", implementationInfo="+implementationInfo+"]");

		sb.append("]");

		return sb.toString();
	}
}
