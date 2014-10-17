/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Objects;

/**
 * Represents a java array data type.
 *
 * @author mmc
 */
public final class ArrayType extends Type
{
	private final Type componentType;

	public ArrayType(BasicClazz clazzUsingType, String qualifiedProtoTypicalTypeName, Type componentType)
	{
		super(clazzUsingType, qualifiedProtoTypicalTypeName);
		this.componentType=Objects.requireNonNull(componentType);
	}

	@Override
	public Type copy(BasicClazz clazzUsingType)
	{
		return new ArrayType(clazzUsingType, this.qualifiedProtoTypicalTypeName, componentType.copy(clazzUsingType));
	}

	@Override
	public boolean isMultiDimensionalArray()
	{
		return componentType.isArray();
	}

	@Override
	public boolean isArray()
	{
		return true;
	}

	public Type getArrayComponentType()
	{
		return componentType;
	}

	@Override
    public TypeCategory getTypeCategory()
    {
  		return TypeCategory.ARRAY;
    }

	@Override
	public boolean isInImportScope()
	{
		return componentType.isInImportScope();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((componentType == null) ? 0 : componentType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ArrayType other = (ArrayType) obj;
		if (componentType == null) {
			if (other.componentType != null)
				return false;
		} else if (!componentType.equals(other.componentType))
			return false;
		return true;
	}

	@Override
	public String toString(int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("ArrayType [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
		  sb.append(", typeName = "+qualifiedProtoTypicalTypeName+ ", componentType="+componentType.toString(level+1));

		sb.append("]");

		return sb.toString();
	}
}
