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

	public Property(Clazz clazz, Type declaringType, String propertyName, Type returnType, Member member, PropertyKind kind,String javaDoc)
	{
		super(clazz, declaringType, propertyName, returnType, Collections.emptyList(), javaDoc, true);
		this.member=Objects.requireNonNull(member);
		this.kind=kind;
	}

	public Property(Clazz clazz, Type declaringType, String propertyName, Type returnType, Member member, PropertyKind kind, String javaDoc, Parameter parameter)
	{
		super(clazz, declaringType, propertyName, returnType, Arrays.asList(parameter), javaDoc, true);
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
	public String toString(int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Property [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
			sb.append(", member=" + member.getName() + ", propertyKind="+kind+", implementationClaimed="+implementationClaimed+"]");

		sb.append("]");

		return sb.toString();
	}
}

