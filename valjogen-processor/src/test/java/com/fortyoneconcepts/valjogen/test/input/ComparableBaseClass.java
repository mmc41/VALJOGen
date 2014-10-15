/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.Collections;
import java.util.List;

public class ComparableBaseClass implements Comparable<ComparableBaseClass>
{
	protected int intField;
	protected String strField;
	protected List<String> strListField;

	protected ComparableBaseClass()
	{
		this.intField=42;
		this.strField="hello";
		strListField=Collections.emptyList();
	}

	public ComparableBaseClass(int intField, String strField)
	{
		this.intField=intField;
		this.strField=strField;
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
	public int compareTo(ComparableBaseClass o)
	{
		return strField().compareTo(o.strField());
	}
}
