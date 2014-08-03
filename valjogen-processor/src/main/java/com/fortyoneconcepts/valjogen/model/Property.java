/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

import javax.lang.model.element.ExecutableElement;

import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/***
 * Meta-information about a property setter/getter method that should be generated (implemented).
 *
 * @author mmc
 *
 */
public final class Property extends Method
{
	private final Member member;

	public Property(Clazz clazz, ExecutableElement element, Member member, String javaDoc)
	{
		super(clazz, element, Collections.emptyList(), javaDoc);
		this.member=Objects.requireNonNull(member);
	}

	public Property(Clazz clazz, ExecutableElement element, Member member, String javaDoc, Parameter parameter)
	{
		super(clazz, element, Arrays.asList(parameter), javaDoc);
		this.member=Objects.requireNonNull(member);
	}

	@Override
	public boolean isFinal()
	{
		return getConfiguration().isFinalPropertiesEnabled();
	}

	public boolean isSynchronized()
	{
		return getConfiguration().isSerializableEnabled() && !member.isFinal();
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
		return NamesUtil.isGetterMethod(element.getSimpleName().toString());
	}

	public boolean isSetter()
	{
		return NamesUtil.isSetterMethod(element.getSimpleName().toString());
	}

	@Override
	public String toString()
	{
		return "Property [clazz = "+clazz.getName()+", member=" + member + "]";
	}
}

