/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Represents a primitive java data type.
 *
 * @author mmc
 */
public class PrimitiveType extends Type
{
	public PrimitiveType(BasicClazz clazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		super(clazzUsingType, qualifiedProtoTypicalTypeName);
	}

	@Override
	public Type copy(BasicClazz clazzUsingType)
	{
		return new PrimitiveType(clazzUsingType, this.qualifiedProtoTypicalTypeName);
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}
/*
	@Override
	public boolean isComparable()
	{
		return true;
	}
*/
	@Override
    public boolean isVoid()
    {
		return (qualifiedProtoTypicalTypeName.equals("void"));
    }

	@Override
	public boolean isPrimitiveByte()
	{
		return (qualifiedProtoTypicalTypeName.equals("byte"));
	}

	@Override
	public boolean isPrimitiveBoolean()
	{
		return (qualifiedProtoTypicalTypeName.equals("boolean"));
	}

	@Override
	public boolean isPrimitiveShort()
	{
		return (qualifiedProtoTypicalTypeName.equals("short"));
	}

	@Override
	public boolean isPrimitiveInt()
	{
		return (qualifiedProtoTypicalTypeName.equals("int"));
	}

	@Override
	public boolean isPrimitiveLong()
	{
		return (qualifiedProtoTypicalTypeName.equals("long"));
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
	public boolean isInImportScope()
	{
		return true;
	}

	@Override
	public String toString(int level) {
		return "PrimitiveType [this=@"+ Integer.toHexString(System.identityHashCode(this))+", typeName = "+qualifiedProtoTypicalTypeName+ "]";
	}
}
