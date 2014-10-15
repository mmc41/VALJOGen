package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.List;

/***
 * Meta-information about a constructor method.
 *
 * @author mmc
 */
public final class Constructor extends Method
{
	public Constructor(BasicClazz clazz, Type declaringType, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, ImplementationInfo implementationInfo)
	{
	    super(clazz, declaringType, "this", returnType, parameters, thrownTypes, javaDoc, declaredModifiers, modifiers, implementationInfo);
	}

	public Constructor(BasicClazz clazz, Type declaringType, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, ImplementationInfo implementationInfo)
	{
	    super(clazz, declaringType, "this", returnType, parameters, thrownTypes, javaDoc, declaredModifiers, implementationInfo);
	}

	@Override
	public boolean isConstructor()
	{
		return true;
	}
}
