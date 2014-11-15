/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.processor.TemplateKind;

/***
 * Meta-information about a constructor method.
 *
 * @author mmc
 */
public class Constructor extends Method
{
	/**
	 * Tells if this constructor is the most complete one and this the one to use for data conversions etc.
	 */
	private final boolean primary;

	public Constructor(BasicClazz clazz, Type declaringType, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, boolean primary, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, List<Annotation> annotations, ImplementationInfo implementationInfo)
	{
	    super(clazz, declaringType, clazz.getSimpleName(), returnType, parameters, thrownTypes, javaDoc, declaredModifiers, modifiers, annotations, implementationInfo, TemplateKind.CONSTRUCTOR);
	    this.primary=primary;
	}

	public Constructor(BasicClazz clazz, Type declaringType, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, boolean primary, EnumSet<Modifier> declaredModifiers, List<Annotation> annotations, ImplementationInfo implementationInfo)
	{
	    super(clazz, declaringType, clazz.getSimpleName(), returnType, parameters, thrownTypes, javaDoc, declaredModifiers, annotations, implementationInfo, TemplateKind.CONSTRUCTOR);
	    this.primary=primary;
	}

	@Override
	public boolean isConstructor()
	{
		return true;
	}

	@Override
	public boolean isPrimary()
	{
		return primary;
	}

	@Override
	public String getOverloadName()
	{
		return getOverloadName("", parameters);
	}

	public List<MemberParameter> getNonDelegateMemberParameters()
	{
		return parameters.stream().filter(p -> !p.isDelegating() && p.isMemberAssociated()).map(p -> (MemberParameter)p).collect(Collectors.toList());
	}
}
