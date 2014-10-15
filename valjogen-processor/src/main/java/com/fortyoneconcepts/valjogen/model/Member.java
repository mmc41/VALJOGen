/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Collectors;

/***
 * Meta-data about a membervariable that backs a property getter and/or setter method.
 *
 * @author mmc
 */
public final class Member extends DefinitionBase implements TypedModel
{
	private final Type type;
	private List<Property> properties;

	public Member(BasicClazz clazz, Type type, String name, EnumSet<Modifier> declaredModifiers)
	{
		super(clazz, name, declaredModifiers);
		this.type=Objects.requireNonNull(type);
		this.properties=new LinkedList<Property>();
	}

	@Override
	public String getPackageName()
	{
		return clazz.getPackageName();
	}

	@Override
	public Type getType()
	{
	    return type;
	}

	@Override
	public EnumSet<Modifier> getModifiers()
	{
		Set<Modifier> modifiers = new HashSet<>();

		if (clazz.isFinal())
		  modifiers.add(Modifier.PRIVATE);
		else  modifiers.add(Modifier.PROTECTED);

		if (declaredModifiers.contains(Modifier.STATIC))
			modifiers.add(Modifier.STATIC);

		if (clazz.getConfiguration().isFinalMembersAndParametersEnabled() && !isMutable())
			modifiers.add(Modifier.FINAL);

		return modifiers.isEmpty() ? EnumSet.noneOf(Modifier.class) : EnumSet.copyOf(modifiers);
	}

	public Property getGetter()
	{
		for (Property property : properties)
			if (property.isGetter())
				return property;

		return null;
	}

	public boolean isMutable()
	{
		boolean mutable = properties.stream().anyMatch(p -> p.isMutating());
		return mutable;
	}

	public boolean isEnsureNotNullEnabled()
	{
		return getConfiguration().isEnsureNotNullEnabled();
	}

    public void addPropertyMethod(Property propertyMethod)
    {
        properties.add(Objects.requireNonNull(propertyMethod));
    }

	public List<Property> getPropertyMethods() {
		return properties;
	}

	public List<Member> getOtherMembersBeforeThis()
	{
		List<Member> members = clazz.getMembers();

		List<Member> result = new ArrayList<Member>(members.size()-1);

		Iterator<Member> iter = members.iterator();
		boolean found = false;
		while (iter.hasNext() && !found)
		{
			Member m = iter.next();
			if (!(found=(m==this)))
				result.add(m);
		}

		return result;
	}

	public List<Member> getOtherMembersAfterThis()
	{
		List<Member> members = clazz.getMembers();

		List<Member> result = new ArrayList<Member>(members.size()-1);

		Iterator<Member> iter = members.iterator();
		boolean found = false;
		while (iter.hasNext())
		{
			Member m = iter.next();
			if (m==this) {
				found=true;
			} else if (found) {
				result.add(m);
			}
		}

		return result;
	}

	@Override
	public String toString(int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("Member [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
			sb.append(", name=" + name + ", type=" + type.getPrototypicalName() + ", properties=["+properties.stream().map(p -> p.name).collect(Collectors.joining(", "))+", declaredModifiers="+declaredModifiers+", modifiers="+getModifiers()+", mutable="+isMutable()+"]");

		sb.append("]");

		return sb.toString();
	}
}
