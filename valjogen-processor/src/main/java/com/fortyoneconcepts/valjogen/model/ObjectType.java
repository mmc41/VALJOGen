package com.fortyoneconcepts.valjogen.model;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fortyoneconcepts.valjogen.model.util.ToStringUtil;

/**
 * Represents a java true object data type (which is not an array and not a primitive data type).
 *
 * @author mmc
 */
public final class ObjectType extends Type
{
	private final List<GenericParameter> genericParameters;
	private final List<Type> superTypes;
	private final Set<Type> superTypesWithAncestors;
	private final HelperTypes helperTypes;

	public ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		this(clazzUsingType, qualifiedProtoTypicalTypeName, Collections.emptyList(), Collections.emptySet(), Collections.emptyList());// TODO: Add java.lang.Object as default supertype ??
	}

	public ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName, List<Type> superTypes, Set<Type> superTypesWithAncestors)
	{
		this(clazzUsingType, qualifiedProtoTypicalTypeName, superTypes, superTypesWithAncestors, Collections.emptyList());
	}

	public ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName, List<Type> superTypes, Set<Type> superTypesWithAncestors, List<GenericParameter> genericParameters)
	{
		super(clazzUsingType, qualifiedProtoTypicalTypeName);
		this.genericParameters=Objects.requireNonNull(genericParameters);
		this.superTypes=Objects.requireNonNull(superTypes);
		this.superTypesWithAncestors=Objects.requireNonNull(superTypesWithAncestors);
		this.helperTypes=clazzUsingType.getHelperTypes();
	}

	public List<Type> getSuperTypes()
	{
		return superTypes;
	}

	public Set<Type> getSuperTypesWithAncestors()
	{
		return superTypesWithAncestors;
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

	@Override
	public boolean isSerializable()
	{
		Type serializableType = helperTypes.getSerializableInterfaceType();
		if (this.equals(serializableType))
			return true;
		else return superTypesWithAncestors.contains(serializableType);
	}

	@Override
	public boolean isComparable()
	{
        // java.lang.Comparable generic i.e  java.lang.Comparable<java.lang.String> so our helperTypes need an arg for this to work ???

//		Type comparableType = helperTypes.getComparableInterfaceType();
		if (this.getQualifiedName().equals("java.lang.Comparable"))
			return true;

		//if (this.equals(comparableType))
		//	return true;

		boolean comparable = superTypesWithAncestors.stream().anyMatch(t -> t.getQualifiedName().equals("java.lang.Comparable"));
		return comparable;
	}

	@Override
	public String toString() {
		return "ObjectType [this=@"+ Integer.toHexString(System.identityHashCode(this))+", typeName = "+qualifiedProtoTypicalTypeName+ ", genericParameters="+genericParameters+", superTypes="+superTypes+"]";
	}
}
