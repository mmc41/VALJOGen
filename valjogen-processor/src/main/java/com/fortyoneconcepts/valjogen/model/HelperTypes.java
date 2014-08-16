package com.fortyoneconcepts.valjogen.model;

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
	private final Type javaUtilArrays;
	private final Type javaUtilObjects;

	HelperTypes(Type javaUtilArrays, Type javaUtilObjects)
	{
		this.javaUtilArrays=javaUtilArrays;
		this.javaUtilObjects=javaUtilObjects;
	}

	/**
	* @return The Type object for {@link java.util.Arrays}
	*/
	public Type getJavaUtilArrays()
	{
		return javaUtilArrays;
	}

	/**
	* @return The Type object for {@link java.util.Objects}
	*/
	public Type getJavaUtilObjects()
	{
		return javaUtilObjects;
	}

	@Override
	public String toString()
	{
		return "HelperTypes";
	}
}
