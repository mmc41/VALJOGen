package com.fortyoneconcepts.valjogen.model;

import java.util.Collections;

/**
 * Contains definitions of (external) java classes that templates may refer to. Instead of hardcoding
 * a named reference to a java class like "java.util.Objects" use the corresponding method
 * getJavaUtilObjects to return the type and then call the name property on that type
 * to get a name that correctly refers to the java class. This name may be qualified or unqualified
 * depending on if an import has been made.
 *
 * @author mmc
 */
public class HelperTypes
{
	private final Clazz clazz;

	public HelperTypes(Clazz clazz)
	{
		this.clazz=clazz;
	}

	/**
	* @return The Type object for {@link java.io.Serializable}
	*/
	public Type getSerializableInterfaceType()
	{
		return new ObjectType(clazz, "java.io.Serializable");
    }

	/**
	* @return The Type object for {@link java.io.Externalizable}
	*/
	public Type getExternalizableInterfaceType()
	{
		return new ObjectType(clazz, "java.io.Externalizable");
    }

	/**
	* @return The Type object for {@link java.lang.Comparable}
	*/
	public Type getComparableInterfaceType() // TODO: Parameterize ???
	{
		return new ObjectType(clazz, "java.lang.Comparable<T>");
    }

	/**
	* @return The Type object for {@link java.util.Arrays}
	*/
	public Type getJavaUtilArrays()
	{
		return new ObjectType(clazz, "java.util.Arrays");
	}

	/**
	* @return The Type object for {@link java.util.Objects}
	*/
	public Type getJavaUtilObjects()
	{
		return new ObjectType(clazz, "java.util.Objects");
	}

	@Override
	public String toString()
	{
		return "HelperTypes";
	}
}
