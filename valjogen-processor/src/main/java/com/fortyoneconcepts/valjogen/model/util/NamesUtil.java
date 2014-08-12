/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Low-level utilties that helps dealing with names of classes, methods/properties, members, types, packages etc.
 *
 * @author mmc
 */
public class NamesUtil
{
	public static final String ImplClassSuffix = "Impl";

	@SuppressWarnings("serial")
	private static final Map<String,String> primitiveToWrapperMap = new HashMap<String,String>() {{
	            put("char", "Character");
			    put("int", "Integer");
			    put("long", "Long");
			    put("double", "Double");
			    put("float", "Float");
			    put("boolean", "Boolean");
			    put("byte", "Byte");
			    put("short", "Short");
			    put("void", "Void");
	}};

	private static final Set<String> reservedWordsSet = new HashSet<String>(Arrays.asList(
							"abstract", "assert", "boolean",
							"break", "byte", "case", "catch", "char", "class", "const",
							"continue", "default", "do", "double", "else", "extends", "false",
							"final", "finally", "float", "for", "goto", "if", "implements",
							"import", "instanceof", "int", "interface", "long", "native",
							"new", "null", "package", "private", "protected", "public",
							"return", "short", "static", "strictfp", "super", "switch",
							"synchronized", "this", "throw", "throws", "transient", "true",
							"try", "void", "volatile", "while",
							"string", "object", "hashCode", "equals", "clone", "toString", "notify",
							"notifyAll", "wait", "finalize", "java"));

	private NamesUtil() {}

	private static boolean isReserved(String identifier) {
		return reservedWordsSet.contains(identifier);
	}

	public static String makeSafeJavaIdentifier(String identifier) {
		if (identifier==null || identifier.isEmpty())
			throw new IllegalArgumentException("non-null/empty name must be specified");
		if (isReserved(identifier) || !Character.isJavaIdentifierStart(identifier.charAt(0)))
			return "_"+identifier;
		else return identifier;
	}

	/**
	* Create a class name from an interface name by removing package, adding a implementation suffix and removing I/Interface from name while retaining any generic part.
	*
	* @param interfaceName The name of the interface that the class name should be generated from.
	* @return The class name that should be used for the generated valjo object
	*/
	public static String createNewClassNameFromInterfaceName(String interfaceName)
	{
		int unqualifedNamePos = interfaceName.lastIndexOf('.')+1;

		String unqualifedInterfaceName = interfaceName.substring(unqualifedNamePos);
		unqualifedInterfaceName=unqualifedInterfaceName.replaceFirst("(^I(nterface)?)|(I|i)nterface", "");

		int genericQualifierPos = unqualifedInterfaceName.indexOf("<");
		String result;

		if (genericQualifierPos<0)
			result=unqualifedInterfaceName+ImplClassSuffix;
	    else {
	    	result=unqualifedInterfaceName.substring(0,genericQualifierPos)+ImplClassSuffix+unqualifedInterfaceName.substring(genericQualifierPos);
	    }

		return result;
	}

	public static String getGenericQualifier(String className)
	{
		int genericQualifierPos = className.indexOf("<");
		if (genericQualifierPos>=0)
			return className.substring(genericQualifierPos);
		else return "";
	}

	public static String stripGenericQualifier(String className)
	{
		int genericQualifierPos = className.indexOf("<");
		if (genericQualifierPos<0)
			return className;
		else return className.substring(0, genericQualifierPos);
	}


	public static String ensureQualifedName(String name, String defaultPackage)
	{
		if (!name.contains(".") && !defaultPackage.isEmpty())
			return defaultPackage+"."+name;
		else return name;
	}

	public static boolean isQualified(String name)
	{
		return name.indexOf(".")>=0;
	}

	public static String getUnqualifiedName(String name)
	{
		int lastSep = name.lastIndexOf('.');
		if (lastSep>=0)
			return name.substring(lastSep+1);
		else return name;
	}

	public static String removeUnnecessaryPackageFromName(String qualifiedName, String packageContext)
	{
		if (hasPackage(qualifiedName, packageContext) || hasPackage(qualifiedName,"java.lang"))
		  return getUnqualifiedName(qualifiedName);
		else
		  return qualifiedName;
	}

	public static String getPackageFromQualifiedName(String name)
	{
		int lastSep = name.lastIndexOf('.');
		if (lastSep>=0)
			return name.substring(0,lastSep);
		else return "";
	}

	public static boolean hasPackage(String qualifiedName, String packageName)
	{
		return getPackageFromQualifiedName(qualifiedName).equals(packageName);
	}

	public static boolean isGetterMethod(String methodName)
	{
		return (methodName.startsWith("get") && methodName.length()>3) || (methodName.startsWith("is") && methodName.length()>2);
	}

	public static boolean isSetterMethod(String methodName)
	{
		return (methodName.startsWith("set") && methodName.length()>3);
	}

	public static boolean isPropertyMethod(String methodName)
	{
		return isGetterMethod(methodName) || isSetterMethod(methodName);
	}

	public static String getWrapperTypeName(String typeName)
	{
	  String wrapper = primitiveToWrapperMap.get(typeName);
	  if (wrapper!=null)
		  return wrapper;
	  else throw new IllegalArgumentException("Type "+typeName+" is not a primitive type");
	}
}
