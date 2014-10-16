/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.Objects;

/**
 * Meta-information about a formal parameter with a value that should be forwarded to another method call.
 *
 * @author mmc
 */
public class DelegateParameter extends Parameter
{
	private final Method delegateMethod;
	private final Parameter delegateParameter;

	public DelegateParameter(BasicClazz clazz, Type paramType, String paramName, EnumSet<Modifier> declaredModifiers, Method delegateMethod, Parameter delegateParameter)
	{
		super(clazz, paramType, paramName, declaredModifiers);
		this.delegateMethod=Objects.requireNonNull(delegateMethod);
		this.delegateParameter=Objects.requireNonNull(delegateParameter);
	}

	public DelegateParameter(BasicClazz clazz, Type paramType, Type erasedParamType, String paramName, EnumSet<Modifier> declaredModifiers, Method delegateMethod, Parameter delegateParameter)
	{
		super(clazz, paramType, erasedParamType, paramName, declaredModifiers);
		this.delegateMethod=Objects.requireNonNull(delegateMethod);
		this.delegateParameter=Objects.requireNonNull(delegateParameter);
	}

	@Override
	public DelegateParameter setName(String newParamName)
	{
		return new DelegateParameter(clazz, type, erasedParamType, newParamName, declaredModifiers, delegateMethod, delegateParameter);
	}

	public Method getDelegateMethod()
	{
		return delegateMethod;
	}

	public Parameter getDelegateParameter()
	{
		return delegateParameter;
	}

	@Override
	public boolean isDelegating()
	{
		return true;
	}

	@Override
	public String toString(int level) {
		return "DelegateParameter [name=" + name + ", type=" + type.getPrototypicalQualifiedName() + ", erasedType=" + erasedParamType.getPrototypicalQualifiedName() +", delegateMethod="+delegateMethod.getQualifiedName()+", delegateParameter="+delegateParameter.getName()+" declaredModifiers="+declaredModifiers+", modifiers="+getModifiers()+"]";
	}
}
