/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Information about a type that a model use or refer to. Actual types are divided into 3 concrete subclasses depending on the category
 * of the type. I.e. if the type is a primitive, an array or an object.
 *
 * @author mmc
 */
public abstract class Type extends ModelBase
{
	protected BasicClazz clazzUsingType; // May be set by subclass immediately after constructor but not changed afterwards.
	protected final String qualifiedProtoTypicalTypeName;

	protected Type(String qualifiedProtoTypicalTypeName)
	{
		this.qualifiedProtoTypicalTypeName =  Objects.requireNonNull(qualifiedProtoTypicalTypeName);
		this.clazzUsingType = null; // Must be set manually after constructor.
	}

	protected Type(BasicClazz clazzUsingType, String qualifiedProtoTypicalTypeName)
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
	public BasicClazz getClazz()
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
	 * Checks if the type is in scope of the class being generated taking imports and default packages etc. into account (so it can be used without qualification).
	 *
	 * @return True if type is in scope of the generated class and its imported classes/pacakges.
	 */
	public abstract boolean isInImportScope();

	/**
	 * Returns a simple type name without package unless nedded and without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The simple type name
	 */
	public String getName()
	{
		if (isInImportScope())
			return getUnqualifiedName(getQualifiedName());
		else return getQualifiedName();
	}

	/**
	 * Returns a simple type name without package unless nedded. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The prototypical type name without any package.
	 */
	public String getPrototypicalName()
	{
		// TODO: unqualify generic arguments also if these are in scope:

		if (isInImportScope())
			return getUnqualifiedName(getPrototypicalQualifiedName());
		else return getPrototypicalQualifiedName();
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

    public boolean isVoid()
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

	public boolean isThisType()
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
