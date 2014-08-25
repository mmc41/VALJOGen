package com.fortyoneconcepts.valjogen.model;

/**
 * Represents a primitive java data type.
 *
 * @author mmc
 */
public final class PrimitiveType extends Type
{
	public PrimitiveType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		super(clazzUsingType, qualifiedProtoTypicalTypeName);
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

	@Override
	public String toString(int level) {
		return "PrimitiveType [this=@"+ Integer.toHexString(System.identityHashCode(this))+", typeName = "+qualifiedProtoTypicalTypeName+ "]";
	}
}
