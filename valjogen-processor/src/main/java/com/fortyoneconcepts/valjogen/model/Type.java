/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Stream.*;
import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Information about a type that a model use or refer to. Actual types are divided into 3 concrete subclasses depending on the category
 * of the type. I.e. if the type is a primitive, an array or an object.
 *
 * @author mmc
 */
public abstract class Type implements Model
{
	protected final Model modelUsingType;
	protected final String qualifiedProtoTypicalTypeName; // or just a simple name for primities.

	public Type(Model modelUsingType, String qualifiedProtoTypicalTypeName)
	{
		this.modelUsingType = Objects.requireNonNull(modelUsingType);
		this.qualifiedProtoTypicalTypeName =  Objects.requireNonNull(qualifiedProtoTypicalTypeName);
	}

	@Override
	public Configuration getConfiguration()
	{
		return modelUsingType.getConfiguration();
	}

	@Override
	public HelperTypes getHelperTypes()
	{
		return modelUsingType.getHelperTypes();
	}

	@Override
	public Clazz getClazz()
	{
		return modelUsingType.getClazz();
	}

	@Override
	public String getPackageName()
	{
		return getPackageFromQualifiedName(qualifiedProtoTypicalTypeName);
	}

	public String getQualifiedName()
	{
		return stripGenericQualifier(qualifiedProtoTypicalTypeName);
	}

	/**
	 * Returns a full class type name with package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The fully qualifid prototypical class type name.
	 */
	public String getPrototypicalQualifiedName() {
		return qualifiedProtoTypicalTypeName;
	}

	public String getName()
	{
		String qualifiedName = getQualifiedName();

		if (hasPackage(qualifiedName,"java.lang") || hasPackage(qualifiedName,getClazz().getPackageName()))
			return getUnqualifiedName(qualifiedName);

		Stream<String> classesInScope = concat(getClazz().getImportTypes().stream().map(t -> t.getQualifiedName()), of(getClazz().getQualifiedName()));
		if (classesInScope.anyMatch(name -> qualifiedName.equals(name)))
			return getUnqualifiedName(qualifiedName);

		return qualifiedName;
	}

	public String getPrototypicalName()
	{
		String qualifiedPrototypicalName = getPrototypicalQualifiedName();
		String qualifiedName = stripGenericQualifier(qualifiedPrototypicalName);

		if (hasPackage(qualifiedPrototypicalName,"java.lang") || hasPackage(qualifiedPrototypicalName,getClazz().getPackageName()))
			return getUnqualifiedName(qualifiedPrototypicalName);

		Stream<String> classesInScope = concat(getClazz().getImportTypes().stream().map(t -> t.getQualifiedName()), of(getClazz().getQualifiedName()));
		if (classesInScope.anyMatch(name -> qualifiedName.equals(name)))
			return getUnqualifiedName(qualifiedPrototypicalName);

		return qualifiedPrototypicalName;
	}

	public String getWrapperName()
	{
	    if (isPrimitive())
	    	return getWrapperTypeName(getName());
	    else return getName();
	}

	public boolean isRootObject()
	{
	    return false;
	}

    public boolean isPrimitive()
    {
    	return false;
    }

    public boolean isPrimitiveFloat()
    {
    	return false;
    }

    public boolean isPrimitiveDouble()
    {
    	return false;
    }

	public boolean isArray()
	{
		return false;
	}

	public boolean isObject()
	{
		return false;
	}

	public boolean isMultiDimensionalArray()
	{
		return false;
	}

    public abstract TypeCategory getTypeCategory();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((modelUsingType == null) ? 0 : modelUsingType.hashCode());
		result = prime
				* result
				+ ((qualifiedProtoTypicalTypeName == null) ? 0
						: qualifiedProtoTypicalTypeName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		if (modelUsingType != other.modelUsingType)
			return false;
		if (qualifiedProtoTypicalTypeName == null) {
			if (other.qualifiedProtoTypicalTypeName != null)
				return false;
		} else if (!qualifiedProtoTypicalTypeName
				.equals(other.qualifiedProtoTypicalTypeName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Type [type = "+getName()+ "]";
	}
}
