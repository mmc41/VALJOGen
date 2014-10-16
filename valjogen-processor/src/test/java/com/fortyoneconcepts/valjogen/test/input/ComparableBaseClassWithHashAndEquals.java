/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ComparableBaseClassWithHashAndEquals implements Comparable<ComparableBaseClassWithHashAndEquals>
{
	protected int intField;
	protected String strField;
	protected List<String> strListField;

	protected ComparableBaseClassWithHashAndEquals()
	{
		this.intField=42;
		this.strField="hello";
		strListField=Collections.emptyList();
	}

	public ComparableBaseClassWithHashAndEquals(int baseIntField, String baseStrField)
	{
		this.intField=baseIntField;
		this.strField=baseStrField;
		strListField=Collections.emptyList();
	}

	public static int getSomethingStatic()
	{
		return 42;
	}

	public int getIntField()
	{
		return intField;
	}

	private String strField()
	{
		return strField;
	}

	protected List<String> getStrListField()
	{
		return strListField;
	}

	@Override
	public int compareTo(ComparableBaseClassWithHashAndEquals o)
	{
		return strField().compareTo(o.strField());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Objects.hashCode(intField);
		result = prime * result	+ Objects.hashCode(strField);
		result = prime * result	+ Objects.hashCode(strListField);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		ComparableBaseClassWithHashAndEquals other = (ComparableBaseClassWithHashAndEquals) obj;

		if (!Objects.equals(intField, other.intField))
			return false;
		if (!Objects.equals(strField, other.strField))
				return false;
		if (!Objects.equals(strListField, other.strListField))
				return false;

		return true;
	}
}
