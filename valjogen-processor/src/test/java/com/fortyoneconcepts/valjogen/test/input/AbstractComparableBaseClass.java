/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.Collections;
import java.util.List;

public abstract class AbstractComparableBaseClass implements Comparable<AbstractComparableBaseClass>
{
	protected int intField;
	protected String strField;
	protected List<String> strListField;

	protected AbstractComparableBaseClass()
	{
		this.intField=42;
		this.strField="hello";
		strListField=Collections.emptyList();
	}

	public AbstractComparableBaseClass(int baseIntField, String baseStrField)
	{
		this.intField=baseIntField;
		this.strField=baseStrField;
		strListField=Collections.emptyList();
	}

	public static int getSomethingStatic()
	{
		return 42;
	}

	public abstract float getFloat();

	public int getIntField()
	{
		return intField;
	}

	@SuppressWarnings("unused")
	private String strField()
	{
		return strField;
	}

	protected List<String> getStrListField()
	{
		return strListField;
	}
}
