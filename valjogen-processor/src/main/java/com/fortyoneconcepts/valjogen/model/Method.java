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
public class Method implements Model
{
	protected final Clazz clazz;
	protected final Type declaringType;
	protected final String methodName;
	protected final List<Parameter> parameters;
	protected final List<Type> typeParameters;
	protected final String javaDoc;
	protected final Type returnType;

	public Method(Clazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> typeParameters, String javaDoc)
	{
	    this.clazz = Objects.requireNonNull(clazz);
	    this.declaringType = Objects.requireNonNull(declaringType);
		this.methodName = Objects.requireNonNull(methodName);
		this.parameters = Objects.requireNonNull(parameters);
		this.typeParameters = Objects.requireNonNull(typeParameters);
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.returnType = Objects.requireNonNull(returnType);
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

	public boolean isFinal()
	{
		return false;
	}

	public boolean isSelfReturnType()
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
	public String toString() {
		return "Method [this=@"+ Integer.toHexString(System.identityHashCode(this))+", declaringType="+declaringType.getName()+", methodName=" + getName() + ", parameters="+parameters+", returnType="+returnType.getName() + ", typeParameters="+typeParameters+"]";
	}
}
