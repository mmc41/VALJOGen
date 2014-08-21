package com.fortyoneconcepts.valjogen.model;

/**
 * Represents a primitive java data type.
 *
 * @author mmc
 */
public final class PrimitiveType extends Type
{
	public PrimitiveType(Model modelUsingType, String qualifiedProtoTypicalTypeName)
	{
		super(modelUsingType, qualifiedProtoTypicalTypeName);
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public boolean isPrimitiveFloat() {
		return (qualifiedProtoTypicalTypeName.equals("float"));
	}

	@Override
	public boolean isPrimitiveDouble() {
		return (qualifiedProtoTypicalTypeName.equals("double"));
	}

	@Override
    public TypeCategory getTypeCategory()
    {
  		return TypeCategory.PRIMITIVE;
    }
}
