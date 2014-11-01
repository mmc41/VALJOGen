/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.processor.TemplateKind;

/***
 * Meta-information about a property setter/getter method that should be generated (implemented).
 *
 * @author mmc
 *
 */
public class Property extends Method
{
	private final Member member;
	private final Type overriddenReturnType;
	private final PropertyKind kind;

	public Property(BasicClazz clazz, Type declaringType, String propertyName, Type returnType, Type overriddenReturnType, List<Type> thrownTypes, Member member, PropertyKind kind, String javaDoc, EnumSet<Modifier> declaredModifiers, ImplementationInfo implementationInfo)
	{
		super(clazz, declaringType, propertyName, returnType, Collections.emptyList(), thrownTypes, javaDoc, declaredModifiers, implementationInfo, TemplateKind.PROPERTY);
		this.member=Objects.requireNonNull(member);
		this.overriddenReturnType=overriddenReturnType;
		this.kind=kind;
	}

	public Property(BasicClazz clazz, Type declaringType, String propertyName, Type returnType, Type overriddenReturnType, List<Type> thrownTypes, Member member, PropertyKind kind, String javaDoc, EnumSet<Modifier> declaredModifiers, ImplementationInfo implementationInfo, Parameter parameter)
	{
		super(clazz, declaringType, propertyName, returnType, Arrays.asList(parameter), thrownTypes, javaDoc, declaredModifiers, implementationInfo, TemplateKind.PROPERTY);
		this.member=Objects.requireNonNull(member);
		this.overriddenReturnType=overriddenReturnType;
		this.kind=kind;
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

	public boolean isMutating()
	{
		boolean mutating = isSetter() && !isThisReturnType();
		return mutating;
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
			sb.append(", member=" + member.getName() +
					  ", propertyKind="+kind+
					  ", overriddenReturnType="+overriddenReturnType.toString(NO_DETAILS_LEVEL)+
  					  ", thrownTypes=["+thrownTypes.stream().map(t -> t.getPrototypicalName()).collect(Collectors.joining(","+System.lineSeparator()))+"]"+
					  ", declaredModifiers="+declaredModifiers+
					  ", modifiers="+modifiers+
					  ", implementationInfo="+implementationInfo+"]");

		sb.append("]");

		return sb.toString();
	}
}

