/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Objects;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;

/**
 * Represents a java array data type.
 *
 * @author mmc
 */
public class ArrayType extends Type
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
	public void print(IndentedPrintWriter writer, int detailLevel)
	{
		if (detailLevel>=MAX_RECURSIVE_LEVEL) {
			writer.print(qualifiedProtoTypicalTypeName+" ");
			return;
		}

		if (detailLevel>0)
			writer.increaseIndent();

		writer.ensureNewLine();

		writer.print(this.getClass().getSimpleName()+"(this=@"+ Integer.toHexString(System.identityHashCode(this))+", qualifiedProtoTypicalTypeName="+ qualifiedProtoTypicalTypeName);
		writer.increaseIndent();

		writer.print("componentType= ");
		componentType.print(writer, detailLevel+1);

		writer.decreaseIndent();
		writer.println(")");

		if (detailLevel>0)
			writer.decreaseIndent();
	}
}
