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
	private final Type overriddenReturnType;
	private final PropertyKind kind;

	public Property(BasicClazz clazz, AccessLevel accessLevel, Type declaringType, String propertyName, Type returnType, Type overriddenReturnType, List<Type> thrownTypes, Member member, PropertyKind kind,String javaDoc, ImplementationInfo implementationInfo)
	{
		super(clazz, accessLevel, declaringType, propertyName, returnType, Collections.emptyList(), thrownTypes, javaDoc, implementationInfo);
		this.member=Objects.requireNonNull(member);
		this.overriddenReturnType=overriddenReturnType;
		this.kind=kind;
	}

	public Property(BasicClazz clazz, AccessLevel accessLevel, Type declaringType, String propertyName, Type returnType, Type overriddenReturnType, List<Type> thrownTypes, Member member, PropertyKind kind, String javaDoc, ImplementationInfo implementationInfo, Parameter parameter)
	{
		super(clazz, accessLevel, declaringType, propertyName, returnType, Arrays.asList(parameter), thrownTypes, javaDoc, implementationInfo);
		this.member=Objects.requireNonNull(member);
		this.overriddenReturnType=overriddenReturnType;
		this.kind=kind;
	}

	@Override
	public boolean isFinal()
	{
		return getConfiguration().isFinalPropertiesEnabled();
	}

	public Type getOverriddenReturnType()
	{
		return overriddenReturnType;
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
			sb.append(", accessLevel="+accessLevel+", member=" + member.getName() + ", propertyKind="+kind+", overriddenReturnType="+overriddenReturnType.toString(NO_DETAILS_LEVEL)+", thrownTypes="+thrownTypes+", implementationInfo="+implementationInfo+"]");

		sb.append("]");

		return sb.toString();
	}
}

