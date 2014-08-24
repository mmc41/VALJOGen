package com.fortyoneconcepts.valjogen.model;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.util.ToStringUtil;

/**
 * Represents a java true object data type (which is not an array and not a primitive data type).
 *
 * @author mmc
 */
public final class ObjectType extends Type
{
	private List<Type> genericTypeArguments;
	private final List<Type> superTypes;
	private final Set<Type> superTypesWithAncestors;
	private final HelperTypes helperTypes;

	public ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		this(clazzUsingType, qualifiedProtoTypicalTypeName, Collections.emptyList(), Collections.emptySet(), null);// TODO: Add java.lang.Object as default supertype ??
	}

	public ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName, List<Type> superTypes, Set<Type> superTypesWithAncestors)
	{
		this(clazzUsingType, qualifiedProtoTypicalTypeName, superTypes, superTypesWithAncestors, null);
	}

	private ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName, List<Type> superTypes, Set<Type> superTypesWithAncestors, List<Type> genericTypeArguments)
	{
		super(clazzUsingType, qualifiedProtoTypicalTypeName);
		this.genericTypeArguments=genericTypeArguments;
		this.superTypes=Objects.requireNonNull(superTypes);
		this.superTypesWithAncestors=Objects.requireNonNull(superTypesWithAncestors);
		this.helperTypes=clazzUsingType.getHelperTypes();
	}

	public void setGenericTypeArguments(List<Type> genericTypeArguments)
	{
		if (this.genericTypeArguments!=null)
			throw new IllegalStateException("genericTypeArguments already specified");

		this.genericTypeArguments=Objects.requireNonNull(genericTypeArguments);
	}

	public List<Type> getSuperTypes()
	{
		return superTypes;
	}

	public Set<Type> getSuperTypesWithAncestors()
	{
		return superTypesWithAncestors;
	}

	public List<Type> getGenericTypeArguments()
	{
		return genericTypeArguments==null ? Collections.emptyList() : genericTypeArguments;
	}

	@Override
	public String getPrototypicalName()
	{
		String name = getPrototypicalQualifiedName();

		if (hasGenericQualifier(name))
		{
			StringBuilder sb = new StringBuilder();
			sb.append(getName());
			sb.append('<');
			if (genericTypeArguments!=null)
  			  sb.append(genericTypeArguments.stream().map(t -> t.getPrototypicalName()).collect(Collectors.joining("," )));
			sb.append('>');

			return sb.toString();
		} else return super.getPrototypicalName();
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
		return "ObjectType [this=@"+ Integer.toHexString(System.identityHashCode(this))+", qualifiedProtoTypicalTypeName = "+qualifiedProtoTypicalTypeName+ ", name="+getName()+", genericTypeArguments="+ToStringUtil.toRefsString(genericTypeArguments)+", superTypes="+superTypes+"]";
	}
}
