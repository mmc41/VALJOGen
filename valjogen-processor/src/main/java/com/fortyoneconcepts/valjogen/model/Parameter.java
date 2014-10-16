/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.Objects;

/**
 * Meta-information about a formal parameter for a method/property.
 *
 * @author mmc
 */
public class Parameter extends DefinitionBase implements TypedModel
{
	protected final Type type;
	protected final Type erasedParamType;

	public Parameter(BasicClazz clazz, Type paramType, Type erasedParamType, String paramName, EnumSet<Modifier> declaredModifiers)
	{
		super(clazz, paramName, declaredModifiers);
		this.type=Objects.requireNonNull(paramType);
		this.erasedParamType=Objects.requireNonNull(erasedParamType);
	}

	public Parameter(BasicClazz clazz, Type paramType, String paramName, EnumSet<Modifier> declaredModifiers)
	{
		super(clazz, paramName, declaredModifiers);
		this.type=Objects.requireNonNull(paramType);
		this.erasedParamType=Objects.requireNonNull(paramType);
	}

	public boolean isDelegating()
	{
		return false;
	}

	public boolean isMemberAssociated()
	{
		return false;
	}

	@Override
	public String getPackageName()
	{
		return clazz.getPackageName();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public EnumSet<Modifier> getModifiers()
	{
		return (clazz.getConfiguration().isFinalMembersAndParametersEnabled()) ? EnumSet.of(Modifier.FINAL) : EnumSet.noneOf(Modifier.class);
	}

	// TODO: Remove this setter.
	public Parameter setName(String newParamName)
	{
		return new Parameter(clazz, type, erasedParamType, newParamName, declaredModifiers);
	}

	@Override
	public Type getType()
	{
	    return type;
	}

	public Type getErasedType()
	{
	    return erasedParamType;
	}

	@Override
	public String toString(int level) {
		return "Parameter [name=" + name + ", type=" + type.getPrototypicalQualifiedName() + ", erasedType=" + erasedParamType.getPrototypicalQualifiedName() +", declaredModifiers="+declaredModifiers+", modifiers="+getModifiers()+"]";
	}
}
