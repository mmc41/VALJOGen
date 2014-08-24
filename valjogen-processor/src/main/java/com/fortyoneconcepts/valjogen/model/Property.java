/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;

/***
 * Meta-information about a property setter/getter method that should be generated (implemented).
 *
 * @author mmc
 *
 */
public final class Property extends Method
{
	private final Member member;
	private final PropertyKind kind;

	public Property(Clazz clazz, Type declaringType, String propertyName, Type returnType, Member member, PropertyKind kind,String javaDoc, List<Type> typeParameters)
	{
		super(clazz, declaringType, propertyName, returnType, Collections.emptyList(), typeParameters, javaDoc);
		this.member=Objects.requireNonNull(member);
		this.kind=kind;
	}

	public Property(Clazz clazz, Type declaringType, String propertyName, Type returnType, Member member, PropertyKind kind, String javaDoc, Parameter parameter, List<Type> typeParameters)
	{
		super(clazz, declaringType, propertyName, returnType, Arrays.asList(parameter), typeParameters, javaDoc);
		this.member=Objects.requireNonNull(member);
		this.kind=kind;
	}

	@Override
	public boolean isFinal()
	{
		return getConfiguration().isFinalPropertiesEnabled();
	}

	public boolean isSynchronized()
	{
		return getConfiguration().isSynchronizedAccessEnabled() && !member.isFinal();
	}

	public Parameter getParameter()
	{
		return parameters.isEmpty() ? null : parameters.get(0);
	}

	public Member getMember()
	{
		return member;
	}

	public boolean isGetter()
	{
		return kind==PropertyKind.GETTER;
	}

	public boolean isSetter()
	{
		return kind==PropertyKind.SETTER;
	}

	public boolean isEnsureNotNullEnabled()
	{
		return member.isEnsureNotNullEnabled();
	}

	@Override
	public String toString()
	{
		return "Property [this=@"+ Integer.toHexString(System.identityHashCode(this))+", member=" + member.getName() + ", propertyKind="+kind+", typeParameters="+typeParameters+"]";
	}
}

