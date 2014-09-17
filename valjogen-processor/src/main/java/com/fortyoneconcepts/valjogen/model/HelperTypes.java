package com.fortyoneconcepts.valjogen.model;

/**
 * Contains definitions of (external) java classes that templates may refer to. Instead of hardcoding a named reference to a java class
 * like "java.util.Objects" use the corresponding method getJavaUtilObjects to return the type and then call the name property on that
 * type to get a name that correctly refers to the java class. The names returned from the types may then be qualified or unqualified
 * automatically depending on if an import has been made.
 *
 * @author mmc
 */
public final class HelperTypes
{
	private final NoType noType;
	private final ObjectType javaLangObjectType;
	private final Type voidType;
	private final ObjectType serializableInterfaceType;
	private final ObjectType externalizableInterfaceType;
	private final ObjectType comparableInterfaceType;
	private final ObjectType javaUtilArraysType;
	private final ObjectType javaUtilObjectsInterfaceType;
	private final Type generatedAnnotationType;

	public HelperTypes(NoType noType, ObjectType javaLangObjectType, Type voidType,
			           ObjectType serializableInterfaceType, ObjectType externalizableInterfaceType,
			           ObjectType comparableInterfaceType, ObjectType javaUtilArraysType, ObjectType javaUtilObjectsInterfaceType,
			           Type generatedAnnotationType)
	{
		this.noType=noType;
		this.javaLangObjectType=javaLangObjectType;
		this.voidType=voidType;
		this.serializableInterfaceType=serializableInterfaceType;
		this.externalizableInterfaceType=externalizableInterfaceType;
		this.comparableInterfaceType=comparableInterfaceType;
		this.javaUtilArraysType=javaUtilArraysType;
		this.javaUtilObjectsInterfaceType=javaUtilObjectsInterfaceType;
		this.generatedAnnotationType=generatedAnnotationType;
	}

	/**
	* @return The Type object specifiying no type exist.
	*/
	public NoType getNoType()
	{
		return noType;
    }

	/**
	* @return The Type object for {@link java.lang.Object}
	*/
	public ObjectType getJavaLangObjectType()
	{
		return javaLangObjectType;
    }

	/**
	* @return The Type object for void.
	*/
	public Type getVoidType()
	{
		return voidType;
    }

	/**
	* @return The Type object for {@link java.io.Serializable}
	*/
	public ObjectType getSerializableInterfaceType()
	{
		return serializableInterfaceType;
    }

	/**
	* @return The Type object for {@link java.io.Externalizable}
	*/
	public ObjectType getExternalizableInterfaceType()
	{
		return externalizableInterfaceType;
    }

	/**
	* @return The (non-generic) Type object for {@link java.lang.Comparable}
	*/
	public ObjectType getComparableInterfaceType()
	{
		return comparableInterfaceType;
    }

	/**
	* @return The Type object for {@link java.util.Arrays}
	*/
	public ObjectType getJavaUtilArrays()
	{
		return javaUtilArraysType;
	}

	/**
	* @return The Type object for {@link java.util.Objects}
	*/
	public ObjectType getJavaUtilObjects()
	{
		return javaUtilObjectsInterfaceType;
	}

	/**
	* @return The Type object for {@link javax.annotation.Generated}
	*/
	public Type getGeneratedAnnotation()
	{
		return generatedAnnotationType;
	}

	@Override
	public String toString()
	{
		return "HelperTypes";
	}
}
