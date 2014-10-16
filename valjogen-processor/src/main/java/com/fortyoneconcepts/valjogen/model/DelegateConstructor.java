package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/***
 * Meta-information about a constructor method that calls another constructor
 *
 * @author mmc
 */
public final class DelegateConstructor extends Constructor
{
	private final Constructor delegateConstructor;

	public DelegateConstructor(BasicClazz clazz, Type declaringType, Type returnType, List<Parameter> parameters, List<Type> thrownTypes, String javaDoc, EnumSet<Modifier> declaredModifiers, EnumSet<Modifier> modifiers, ImplementationInfo implementationInfo, Constructor delegateMethod)
	{
	    super(clazz, declaringType, returnType, parameters, thrownTypes, javaDoc, declaredModifiers, modifiers, implementationInfo);
	    this.delegateConstructor=Objects.requireNonNull(delegateMethod);
	}

	public Constructor getDelegateConstructor()
	{
		return delegateConstructor;
	}

	public List<DelegateParameter> getDelegateParameters()
	{
		return parameters.stream().filter(p -> p.isDelegating()).map(p -> (DelegateParameter)p).collect(Collectors.toList());
	}

	@Override
	public boolean isConstructor()
	{
		return true;
	}

	@Override
	public boolean isDelegating()
	{
		return true;
	}
}
