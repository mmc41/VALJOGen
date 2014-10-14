/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Objects;

/**
 * Meta-information about a formal parameter for a method/property.
 *
 * @author mmc
 */
public class Parameter extends DefinitionBase implements TypedModel
{
	private final Type type;
	private final Type erasedParamType;
	private EnumSet<Modifier> modifiers;

	public Parameter(BasicClazz clazz, Type paramType, Type erasedParamType, String paramName, EnumSet<Modifier> declaredModifiers)
	{
		super(clazz, paramName, declaredModifiers);
		this.type=Objects.requireNonNull(paramType);
		this.erasedParamType=Objects.requireNonNull(erasedParamType);

		HashSet<Modifier> _modifiers = new HashSet<>(declaredModifiers);
		//if (clazz.getConfiguration().is())
		_modifiers.add(Modifier.FINAL);
		modifiers=_modifiers.size()>0 ? EnumSet.copyOf(_modifiers) : EnumSet.noneOf(Modifier.class);
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
		return modifiers;
	}

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
		return "Parameter [name=" + name + ", type=" + type.getPrototypicalQualifiedName() + ", erasedType=" + erasedParamType.getPrototypicalQualifiedName() +", declaredModifiers="+declaredModifiers+", modifiers="+modifiers+"]";
	}
}
