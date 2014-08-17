/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Stream.*;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Meta-information about a type that our model use or refer to.
 *
 * @author mmc
 */
public class Type implements Model
{
	private Model modelUsingType;
	private final TypeMirror type;

	public Type(Model modelUsingType, TypeMirror type)
	{
		this.modelUsingType = Objects.requireNonNull(modelUsingType);
		this.type = Objects.requireNonNull(type);
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
		return getPackageFromQualifiedName(type.toString());
	}

	public String getQualifiedName()
	{
		return stripGenericQualifier(type.toString());
	}

	/**
	 * Returns a full class type name with package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The fully qualifid prototypical class type name.
	 */
	public String getPrototypicalQualifiedName() {
		return type.toString();
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
	    return type.toString().equals(ConfigurationDefaults.RootObject);
	}

    public boolean isPrimitive()
    {
    	return type instanceof PrimitiveType;
    }

    public boolean isPrimitiveFloat()
    {
    	return (type.toString().equals("float"));
    }

    public boolean isPrimitiveDouble()
    {
    	return (type.toString().equals("double"));
    }

	public boolean isArray()
	{
		return (type.getKind()==TypeKind.ARRAY);
	}

	public boolean isObject()
	{
		return (!isPrimitive());
	}

	public boolean isMultiDimensionalArray()
	{
		if (!isArray())
			return false;
		ArrayType arrayType = (ArrayType)type;
		TypeMirror componentType = arrayType.getComponentType();
		return (componentType instanceof ArrayType);
	}

	public Type getArrayComponentType()
	{
		if (!isArray())
			throw new UnsupportedOperationException("Operation only supported for arrays");

		ArrayType arrayType = (ArrayType)type;
		TypeMirror componentType = arrayType.getComponentType();
		return new Type(this, componentType);
	}

    public TypeCategory getTypeCategory()
    {
    	if (isPrimitive())
    		return TypeCategory.PRIMITIVE;
    	else if (isArray())
    		return TypeCategory.ARRAY;
    	else return TypeCategory.OBJECT;
    }

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((type == null) ? 0 : type.toString().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Type other = (Type) obj;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.toString().equals(other.type.toString()))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Type [type = "+getName()+ "]";
	}
}