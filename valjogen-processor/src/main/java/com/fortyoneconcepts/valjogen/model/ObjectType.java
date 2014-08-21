package com.fortyoneconcepts.valjogen.model;

/**
 * Represents a java true object data type (which is not an array and not a primitive data type).
 *
 * @author mmc
 */
public final class ObjectType extends Type
{
	public ObjectType(Model modelUsingType, String qualifiedProtoTypicalTypeName)
	{
		super(modelUsingType, qualifiedProtoTypicalTypeName);
	}

	@Override
	public boolean isObject()
	{
		return true;
	}

	@Override
	public boolean isRootObject()
	{
	    return qualifiedProtoTypicalTypeName.equals(ConfigurationDefaults.RootObject);
	}

	@Override
    public TypeCategory getTypeCategory()
    {
  		return TypeCategory.OBJECT;
    }
}
