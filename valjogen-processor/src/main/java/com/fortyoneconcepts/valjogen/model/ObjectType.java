/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;
import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fortyoneconcepts.valjogen.model.util.ToStringUtil;

/**
 * Represents a java true object data type (which is not an array and not a primitive data type).
 *
 * @author mmc
 */
public class ObjectType extends Type
{
	protected List<Type> genericTypeArguments;
	protected ObjectType baseClazzType;
	protected List<Type> interfaceTypes;
	protected Set<Type> superTypesWithAscendants;

	protected boolean initializedType;

	public ObjectType(BasicClazz optClazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		this(optClazzUsingType, qualifiedProtoTypicalTypeName, null, Collections.emptyList(), Collections.emptySet(), Collections.emptyList());
	}

	protected ObjectType(BasicClazz clazzUsingType, String qualifiedProtoTypicalTypeName, ObjectType baseClazz, List<Type> superInterfaces, Set<Type> superTypesWithAncestors, List<Type> genericTypeArguments)
	{
		super(clazzUsingType, qualifiedProtoTypicalTypeName);
		this.genericTypeArguments=genericTypeArguments;
		this.baseClazzType = baseClazz;
		this.interfaceTypes= Objects.requireNonNull(superInterfaces);
		this.superTypesWithAscendants= Objects.requireNonNull(superTypesWithAncestors);
		initializedType=false;
	}

	@Override
	public Type copy(BasicClazz clazzUsingType)
	{
		ObjectType result = new ObjectType(clazzUsingType, this.qualifiedProtoTypicalTypeName, baseClazzType, interfaceTypes, superTypesWithAscendants, genericTypeArguments);
		result.initializedType=true;
		return result;
	}

	/**
     * Nb. Post-constructor for what this type is based on such as supertypes. This method must be called for the type to be fully initialized. Nb. Subclasses of this class may
     * require additional post-constructors to be called. Must be called only once.
	 *
	 * @param baseClazzType Base class of this type if any (NoType if no base class exist).
	 * @param interfaceTypes Direct super-interfaces of this type.
	 * @param superTypesWithAncestors All ancestor interfaces of this type.
	 * @param genericTypeArguments Generic arguments of this type.
	 */
	public void initType(ObjectType baseClazzType, List<Type> interfaceTypes, Set<Type> superTypesWithAncestors, List<Type> genericTypeArguments)
	{
		if (initializedType)
			throw new IllegalStateException("Clazz type aspects already initialized");

		assert superTypesWithAncestors.containsAll(interfaceTypes) : "All interfaces mentioned in interfaceTypes list must be contained in interfaceTypesWithAscendants set";

		this.baseClazzType=Objects.requireNonNull(baseClazzType);
		this.interfaceTypes=Objects.requireNonNull(interfaceTypes);
		this.superTypesWithAscendants=Objects.requireNonNull(superTypesWithAncestors);
		this.genericTypeArguments=Objects.requireNonNull(genericTypeArguments);

		initializedType=true;
	}

	public boolean initialized()
	{
		return initializedType;
	}

	@Override
	public boolean isInImportScope()
	{
		String qualifiedName =  getQualifiedName();

		if (hasPackage(qualifiedName, "java.lang"))
				return true;

		BasicClazz _clazz = getClazz();
		if (_clazz instanceof Clazz)
		{
			Clazz clazz = (Clazz)_clazz;

			if (hasPackage(qualifiedName, getClazz().getPackageName()))
			    return true;

			Stream<String> classesInScope = concat(clazz.getImportTypes().stream().map(t -> t.getQualifiedName()), of(getClazz().getQualifiedName()));
			if (classesInScope.anyMatch(name -> qualifiedName.equals(name)))
				return true;
		}

		return false;
	}

	@Override
	public boolean canBeMoreDetailed()
	{
		return true;
	}

	public ObjectType getBaseClazzType()
	{
		assert initializedType : "Type initialization missing";
		return Objects.requireNonNull(baseClazzType);
	}

	public List<Type> getInterfaceTypes()
	{
		assert initializedType : "Type initialization missing";
		return interfaceTypes;
	}

	public Set<Type> getSuperTypesWithAscendants()
	{
		assert initializedType : "Type initialization missing";
		return superTypesWithAscendants;
	}

	public List<Type> getGenericTypeArguments()
	{
		assert initializedType : "Type initialization missing";
		return genericTypeArguments==null ? Collections.emptyList() : genericTypeArguments;
	}

	public boolean hasPrimitiveMembers() {
		return getMembers().stream().anyMatch(m -> m.getType().isPrimitive());
	}

	public boolean hasArrayMembers() {
		return getMembers().stream().anyMatch(m -> m.getType().isArray());
	}

	public boolean hasAnyMembers() {
		return !getMembers().isEmpty();
	}

	public List<Member> getMembers() {
		return Collections.emptyList();
	}

	public List<Method> getMethods() {
		return Collections.emptyList();
	}

	public List<Constructor> getConstructors() {
		return getMethods().stream().filter(m -> m.isConstructor()).map(m -> (Constructor)m).collect(Collectors.toList());
	}

	@Override
	public String getPrototypicalName()
	{
		if (!initializedType)
			assert initializedType : "Type initialization missing for type "+qualifiedProtoTypicalTypeName;

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
	public boolean isCollection()
	{
		assert initializedType : "Type initialization missing for type "+qualifiedProtoTypicalTypeName;

		if (this.getQualifiedName().equals("java.util.Collection"))
			return true;

		boolean collection = getSuperTypesWithAscendants().stream().anyMatch(t -> t.getQualifiedName().equals("java.util.Collection"));
		return collection;
	}

	@Override
	public boolean isMap()
	{
		assert initializedType : "Type initialization missing for type "+qualifiedProtoTypicalTypeName;

		if (this.getQualifiedName().equals("java.util.Map"))
			return true;

		boolean map = getSuperTypesWithAscendants().stream().anyMatch(t -> t.getQualifiedName().equals("java.util.Map"));
		return map;
	}

	@Override
	public boolean isIterable()
	{
		assert initializedType : "Type initialization missing for type "+qualifiedProtoTypicalTypeName;

		if (this.getQualifiedName().equals("java.lang.Iterable"))
			return true;

		boolean iterable = getSuperTypesWithAscendants().stream().anyMatch(t -> t.getQualifiedName().equals("java.lang.Iterable"));
		return iterable;
	}

	@Override
	public boolean isSerializable()
	{
		assert initializedType : "Type initialization missing for type "+qualifiedProtoTypicalTypeName;

	    /*
		Type serializableType = getHelperTypes().getSerializableInterfaceType();
		if (this.equals(serializableType))
			return true;
		else return getSuperTypesWithAscendants().contains(serializableType);
		*/

	    // Hmm: Above does not work - workaround.
		if (this.getQualifiedName().equals("java.io.Serializable"))
			return true;

		boolean serializable = getSuperTypesWithAscendants().stream().anyMatch(t -> t.getQualifiedName().equals("java.io.Serializable"));
		return serializable;
	}

	@Override
	public boolean isComparable()
	{
		assert initializedType : "Type initialization missing for type "+qualifiedProtoTypicalTypeName;

	    /*
	    Type comparableType = getHelperTypes().getComparableInterfaceType();
	    if (this.equals(comparableType))
			return true;
		else return getSuperTypesWithAscendants().contains(comparableType);
        */

        //  Hmm: Generic qualified gets in the way - workaround.
		if (this.getQualifiedName().equals("java.lang.Comparable"))
			return true;

		boolean comparable = getSuperTypesWithAscendants().stream().anyMatch(t -> t.getQualifiedName().equals("java.lang.Comparable"));
		return comparable;
	}

	@Override
	public String toString(int level)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("ObjectType [this=@"+ Integer.toHexString(System.identityHashCode(this)));

		if (level<MAX_RECURSIVE_LEVEL)
		  sb.append(", initialized="+initialized()+
				    ", qualifiedProtoTypicalTypeName = "+
				    qualifiedProtoTypicalTypeName+ ", name="+
				    getName()+", genericTypeArguments="+
				    ToStringUtil.toString(genericTypeArguments, ", ", level+1)+System.lineSeparator()+
				    ", baseClass="+baseClazzType.toString(level+1)+
				    ", interfaceTypes="+ToStringUtil.toString(interfaceTypes, System.lineSeparator(), level+1));

		sb.append("]");

		return sb.toString();
	}
}
