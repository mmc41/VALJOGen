package com.fortyoneconcepts.valjogen.model;

/**
 * Specifies a non-exisisting type. Type type of nothing.
 * @author mmc
 *
 */
public class NoType extends Type
{
	public NoType(Clazz clazzUsingType)
	{
		super(clazzUsingType, "");
	}

	@Override
	public TypeCategory getTypeCategory()
	{
		return TypeCategory.NONE;
	}

	@Override
	public String toString() {
		return "<NONE>";
	}
}
