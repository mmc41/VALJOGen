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
	protected Clazz clazzUsingType; // May be set by subclass immediately after constructor but not changed afterwards.
	protected final String qualifiedProtoTypicalTypeName;

	protected Type(String qualifiedProtoTypicalTypeName)
	{
		this.qualifiedProtoTypicalTypeName =  Objects.requireNonNull(qualifiedProtoTypicalTypeName);
		this.clazzUsingType = null; // Must be set manually after constructor.
	}

	protected Type(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		this.clazzUsingType = Objects.requireNonNull(clazzUsingType);
		this.qualifiedProtoTypicalTypeName =  Objects.requireNonNull(qualifiedProtoTypicalTypeName);
	}

	@Override
	public Configuration getConfiguration()
	{
		return clazzUsingType.getConfiguration();
	}

	@Override
	public HelperTypes getHelperTypes()
	{
		return clazzUsingType.getHelperTypes();
	}

	@Override
	public Clazz getClazz()
	{
		return clazzUsingType.getClazz();
	}

	@Override
	public String getPackageName()
	{
		return getPackageFromQualifiedName(qualifiedProtoTypicalTypeName);
	}

	/**
	 * Returns a type name with package but without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The qualified type name
	 */
	public String getQualifiedName()
	{
		return stripGenericQualifier(qualifiedProtoTypicalTypeName);
	}

	/**
	 * Returns a full type name with package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The fully qualifid prototypical type name.
	 */
	public String getPrototypicalQualifiedName() {
		return qualifiedProtoTypicalTypeName;
	}

	/**
	 * Returns a simple type name without package and without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The simple type name
	 */
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

	/**
	 * Returns a type name but without package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The prototypical type name without any package.
	 */
	public String getPrototypicalName()
	{
		String qualifiedPrototypicalName = getPrototypicalQualifiedName();
		String qualifiedName = stripGenericQualifier(qualifiedPrototypicalName);

		// TODO: Remove unqualify generic arguments also:

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

	public boolean isSerializable()
	{
		return false;
	}

	public boolean isComparable()
	{
		return false;
	}

    public abstract TypeCategory getTypeCategory();

	@Override
	public int hashCode()
	{
		return qualifiedProtoTypicalTypeName.hashCode();
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
		if (clazzUsingType != other.clazzUsingType)
			return false;
		if (qualifiedProtoTypicalTypeName == null) {
			if (other.qualifiedProtoTypicalTypeName != null)
				return false;
		} else if (!qualifiedProtoTypicalTypeName
				.equals(other.qualifiedProtoTypicalTypeName))
			return false;
		return true;
	}
}
