/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;
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
		return kind==PropertyKind.IMMUTABLE_SETTER || kind==PropertyKind.MUTABLE_SETTER;
	}

	public boolean isMutableSetter()
	{
		return kind==PropertyKind.MUTABLE_SETTER;
	}

	public boolean isImmutableSetter()
	{
		return kind==PropertyKind.IMMUTABLE_SETTER;
	}

	public boolean isEnsureNotNullEnabled()
	{
		return member.isEnsureNotNullEnabled();
	}

	@Override
	protected void printExtraTop(IndentedPrintWriter writer, int detailLevel)
	{
		writer.print(", member="+member.getName() + ", propertyKind="+kind+", overriddenReturnType="+overriddenReturnType.getPrototypicalName());
	}

	@Override
	protected void printExtraBottom(IndentedPrintWriter writer, int detailLevel)
	{

	}
}

