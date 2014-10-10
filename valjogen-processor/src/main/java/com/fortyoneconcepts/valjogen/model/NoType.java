package com.fortyoneconcepts.valjogen.model;

/**
 * Specifies a non-exisisting type. Type of nothing.
 *
 * @author mmc
 *
 */
public class NoType extends Type
{
	public NoType(BasicClazz clazzUsingType)
	{
		super(clazzUsingType, "NONE");
	}

	@Override
	public TypeCategory getTypeCategory()
	{
		return TypeCategory.NONE;
	}

	@Override
	public String toString(int level)
	{
		return "<NONE>";
	}

	@Override
	public boolean isInImportScope()
	{
		return true;
	}
}
