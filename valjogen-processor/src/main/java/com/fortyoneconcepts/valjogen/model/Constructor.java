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
	public Constructor(BasicClazz clazz, Type declaringType, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, ImplementationInfo implementationInfo)
	{
	    super(clazz, declaringType, clazz.getSimpleName(), returnType, parameters, thrownTypes, javaDoc, declaredModifiers, modifiers, implementationInfo, TemplateKind.CONSTRUCTOR);
	}

	public Constructor(BasicClazz clazz, Type declaringType, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, ImplementationInfo implementationInfo)
	{
	    super(clazz, declaringType, clazz.getSimpleName(), returnType, parameters, thrownTypes, javaDoc, declaredModifiers, implementationInfo, TemplateKind.CONSTRUCTOR);
	}

	@Override
	public boolean isConstructor()
	{
		return true;
	}

	@Override
	public String getOverloadName()
	{
		return getOverloadName(false);
	}

	public List<MemberParameter> getNonDelegateMemberParameters()
	{
		return parameters.stream().filter(p -> !p.isDelegating() && p.isMemberAssociated()).map(p -> (MemberParameter)p).collect(Collectors.toList());
	}
}
