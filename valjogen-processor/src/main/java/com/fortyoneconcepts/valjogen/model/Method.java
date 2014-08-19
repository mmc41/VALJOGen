/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.List;
import java.util.Objects;

import javax.lang.model.element.ExecutableElement;

/***
 * Meta-information about a method that should be generated (implemented).
 *
 * @author mmc
 */
public class Method implements Model
{
	protected final Clazz clazz;
	protected final ExecutableElement element;
	protected final List<Parameter> parameters;
	protected final String javaDoc;
	protected final Type returnType;

	public Method(Clazz clazz, ExecutableElement element, List<Parameter> parameters, String javaDoc)
	{
	    this.clazz = Objects.requireNonNull(clazz);
		this.element = Objects.requireNonNull(element);
		this.parameters = Objects.requireNonNull(parameters);
		this.javaDoc = Objects.requireNonNull(javaDoc);
		this.returnType = new Type(clazz, element.getReturnType());
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
		return element.getSimpleName().toString();
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
		return "Method [clazz = "+clazz.getName()+", methodName=" + getName() + ", returnType="+getReturnType() + "]";
	}
}
