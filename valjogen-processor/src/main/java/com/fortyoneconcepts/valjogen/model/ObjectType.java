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
public class ObjectType extends Type
{
	protected HelperTypes helperTypes;

	protected List<Type> genericTypeArguments;

	protected Type baseClazzType;
	protected List<Type> interfaceTypes;
	protected Set<Type> interfaceTypesWithAscendants;

	protected boolean initializedType;

	protected ObjectType(String qualifiedProtoTypicalTypeName)
	{
		super(qualifiedProtoTypicalTypeName);
	    // All fields must be set manually after constructor.
	}

	public ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		this(clazzUsingType, qualifiedProtoTypicalTypeName, new NoType(clazzUsingType), Collections.emptyList(), Collections.emptySet(), null);// TODO: Add java.lang.Object as default supertype ??
	}

	/*
	public ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName, Type baseClazz, List<Type> superInterfaces, Set<Type> superInterfacesWithAncestors)
	{
		this(clazzUsingType, qualifiedProtoTypicalTypeName, baseClazz, superInterfaces, superInterfacesWithAncestors, null);
	}*/

	private ObjectType(Clazz clazzUsingType, String qualifiedProtoTypicalTypeName, Type baseClazz, List<Type> superInterfaces, Set<Type> superInterfacesWithAncestors, List<Type> genericTypeArguments)
	{
		super(clazzUsingType, qualifiedProtoTypicalTypeName);
		this.genericTypeArguments=genericTypeArguments;
		this.baseClazzType = Objects.requireNonNull(baseClazz);
		this.interfaceTypes=Objects.requireNonNull(superInterfaces);
		this.interfaceTypesWithAscendants=Objects.requireNonNull(superInterfacesWithAncestors);
		this.helperTypes=clazzUsingType.getHelperTypes();
	}

	public void initType(Type baseClazzType, List<Type> interfaceTypes, Set<Type> interfaceTypesWithAscendants, List<Type> genericTypeArguments)
	{
		if (initializedType)
			throw new IllegalStateException("Clazz type aspects already initialized");

		this.baseClazzType=Objects.requireNonNull(baseClazzType);

		this.interfaceTypes=Objects.requireNonNull(interfaceTypes);
		this.interfaceTypesWithAscendants=Objects.requireNonNull(interfaceTypesWithAscendants);
		assert interfaceTypesWithAscendants.containsAll(interfaceTypes) : "All interfaces mentioned in interfaceTypes list must be contained in interfaceTypesWithAscendants set";

		this.genericTypeArguments=Objects.requireNonNull(genericTypeArguments);

		initializedType=true;
	}

	public boolean initialized()
	{
		return initializedType;
	}

	public Type getBaseClazzType()
	{
		return Objects.requireNonNull(baseClazzType);
	}

	public List<Type> getInterfaceTypes()
	{
		return interfaceTypes;
	}

	public Set<Type> getInterfaceTypesWithAscendants()
	{
		return interfaceTypesWithAscendants;
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
		else return interfaceTypesWithAscendants.contains(serializableType);
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

		boolean comparable = interfaceTypesWithAscendants.stream().anyMatch(t -> t.getQualifiedName().equals("java.lang.Comparable"));
		return comparable;
	}

	@Override
	public String toString() {
		return "ObjectType [this=@"+ Integer.toHexString(System.identityHashCode(this))+", initialized="+initialized()+", qualifiedProtoTypicalTypeName = "+qualifiedProtoTypicalTypeName+ ", name="+getName()+", genericTypeArguments="+ToStringUtil.toRefsString(genericTypeArguments)+", baseClass="+baseClazzType+", interfaceTypes="+interfaceTypes+"]";
	}
}
