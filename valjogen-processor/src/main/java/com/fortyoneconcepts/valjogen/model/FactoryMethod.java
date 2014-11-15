/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.List;

import com.fortyoneconcepts.valjogen.processor.TemplateKind;

/***
 * Meta-information about a factory method.
 *
 * @author mmc
 */
public class FactoryMethod extends Method
{
	/**
	 * Tells if this factory method is the most complete one and this the one to use for data conversions etc.
	 */
	private final boolean primary;

	public FactoryMethod(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, boolean primary, EnumSet<Modifier> declaredModifiers, List<Annotation> annotations, ImplementationInfo implementationInfo, TemplateKind templateKind)
	{
		super(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, annotations, implementationInfo, templateKind);
	    this.primary=primary;
	}

	public FactoryMethod(BasicClazz clazz, Type declaringType, String methodName, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, boolean primary, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, List<Annotation> annotations, ImplementationInfo implementationInfo, TemplateKind templateKind)
	{
		super(clazz, declaringType, methodName, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, modifiers, annotations, implementationInfo, templateKind);
	    this.primary=primary;
	}

	@Override
	public boolean isFactoryMethod()
	{
		return true;
	}

	@Override
	public boolean isPrimary()
	{
		return primary;
	}
}
