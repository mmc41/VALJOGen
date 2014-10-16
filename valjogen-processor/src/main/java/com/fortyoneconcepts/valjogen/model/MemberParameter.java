/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.Objects;

/**
 * Meta-information about a formal parameter with a value that is associated with a member
 *
 * @author mmc
 */
public class MemberParameter extends Parameter
{
	private final Member associatedMember;

	public MemberParameter(BasicClazz clazz, Type paramType, String paramName, EnumSet<Modifier> declaredModifiers, Member associatedMember)
	{
		super(clazz, paramType, paramName, declaredModifiers);
		this.associatedMember=Objects.requireNonNull(associatedMember);
	}

	public MemberParameter(BasicClazz clazz, Type paramType, Type erasedParamType, String paramName, EnumSet<Modifier> declaredModifiers, Member associatedMember)
	{
		super(clazz, paramType, erasedParamType, paramName, declaredModifiers);
		this.associatedMember=Objects.requireNonNull(associatedMember);
	}

	@Override
	public MemberParameter setName(String newParamName)
	{
		return new MemberParameter(clazz, type, erasedParamType, newParamName, declaredModifiers, associatedMember);
	}

	public Member getMember()
	{
		return associatedMember;
	}

	@Override
	public boolean isMemberAssociated()
	{
		return true;
	}

	@Override
	public String toString(int level) {
		return "MemberParameter [name=" + name + ", type=" + type.getPrototypicalQualifiedName() + ", erasedType=" + erasedParamType.getPrototypicalQualifiedName() +", assoicated member="+associatedMember.getName()+", declaredModifiers="+declaredModifiers+", modifiers="+getModifiers()+"]";
	}
}
