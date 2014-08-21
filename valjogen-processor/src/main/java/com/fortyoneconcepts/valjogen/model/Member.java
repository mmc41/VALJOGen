/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;

/***
 * Meta-data about a membervariable that backs a property getter and/or setter method.
 *
 * @author mmc
 */
public final class Member implements Model
{
	private final Clazz clazz;
	private final String name;
	private final Type type;
	private List<Property> properties;

	public Member(Clazz clazz, Type type, String name)
	{
		this.clazz=Objects.requireNonNull(clazz);
		this.type=Objects.requireNonNull(type);
		this.name=Objects.requireNonNull(name);
		this.properties=new LinkedList<Property>();
	}

	@Override
	public Configuration getConfiguration()
	{
		return clazz.getConfiguration();
	}

	@Override
	public HelperTypes getHelperTypes()
	{
		return clazz.getHelperTypes();
	}

	@Override
	public Clazz getClazz()
	{
		return clazz;
	}

	@Override
	public String getPackageName()
	{
		return clazz.getPackageName();
	}

	public String getName() {
		return name;
	}

	public Type getType()
	{
	    return type;
	}

	public boolean isFinal()
	{
		return properties.stream().noneMatch(p -> p.isSetter() && !p.isSelfReturnType()) && getConfiguration().isFinalMembersEnabled();
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
	public String toString() {
		return "Member [clazz = "+clazz.getName()+", name=" + name + ", type=" + getType().getName() + "]";
	}
}
