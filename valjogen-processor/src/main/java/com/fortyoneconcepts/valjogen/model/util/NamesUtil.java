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

	private static final String[] emptyArray = new String[0];

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
		unqualifedInterfaceName=unqualifedInterfaceName.replaceFirst("(^I(?=[A-Z])(nterface)?)|(I|i)nterface", "");

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

	public static String[] getGenericQualifierNames(String className)
	{
		int genericQualifierPos = className.indexOf("<");
		int genericQualifierEndPos = className.lastIndexOf(">");
		if (genericQualifierPos>=0 && genericQualifierEndPos<=0 || genericQualifierPos>genericQualifierEndPos)
			throw new IllegalArgumentException("Illegal format of generic qualifier in "+className);

		if (genericQualifierPos>=0) {
			String qualifiers = className.substring(genericQualifierPos+1, genericQualifierEndPos);
			return qualifiers.split("\\,");
		}
		else return emptyArray;
	}

	public static boolean hasGenericQualifier(String name)
	{
		return name.indexOf("<")>=0;
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
		return stripGenericQualifier(name).indexOf(".")>=0;
	}

	public static String getUnqualifiedName(String name)
	{
		int lastSep = stripGenericQualifier(name).lastIndexOf('.');
		if (lastSep>=0)
			return name.substring(lastSep+1);
		else return name;
	}

	public static String getPackageFromQualifiedName(String name)
	{
		int lastSep = stripGenericQualifier(name).lastIndexOf('.');
		if (lastSep>=0)
			return name.substring(0,lastSep);
		else return "";
	}

	public static boolean hasPackage(String qualifiedName, String packageName)
	{
		return getPackageFromQualifiedName(qualifiedName).equals(packageName);
	}

	public static boolean isGetterMethod(String methodName, String[] getterPrefixes)
	{
		return Arrays.stream(getterPrefixes).anyMatch(p -> methodName.startsWith(p) && methodName.length()>p.length());
	}

	public static boolean isSetterMethod(String methodName, String[] setterPrefixes)
	{
		return Arrays.stream(setterPrefixes).anyMatch(p -> methodName.startsWith(p) && methodName.length()>p.length());
	}

	public static String getWrapperTypeName(String typeName)
	{
	  String wrapper = primitiveToWrapperMap.get(typeName);
	  if (wrapper!=null)
		  return wrapper;
	  else throw new IllegalArgumentException("Type "+typeName+" is not a primitive type");
	}

	public static boolean hasWilcard(String wildcardSpecifier)
	{
	  return wildcardSpecifier.contains("*");
	}

	/**
	 * Check if two overload names wtih optional * wildcards except of method name or a type paramter match. Use same format as {@link com.fortyoneconcepts.valjogen.model.Method#getOverloadName}
	 *
	 * @param overloadNamesSpecifier1
	 * @param overloadNamesSpecifier2
	 * @param ignoreAllPackages True all if packages should be ignored in comparison, false if only java.lang packages should be ignored.
	 *
	 * @return True if mathes.
	 */
	public static boolean matchingOverloads(String overloadNamesSpecifier1, String overloadNamesSpecifier2, boolean ignoreAllPackages)
	{
		overloadNamesSpecifier1=overloadNamesSpecifier1.trim();
		overloadNamesSpecifier2=overloadNamesSpecifier2.trim();

		int nameQualifierPos1 = overloadNamesSpecifier1.indexOf("(");
		int nameQualifierPos2 = overloadNamesSpecifier2.indexOf("(");

		String name1 = nameQualifierPos1>=0 ? overloadNamesSpecifier1.substring(0, nameQualifierPos1) : overloadNamesSpecifier1;
		String name2 = nameQualifierPos2>=0 ? overloadNamesSpecifier2.substring(0, nameQualifierPos2) : overloadNamesSpecifier2;

		if (!name1.equals("*") && !name2.equals("*") && !name1.endsWith(name2))
			return false;

		String nameTypes1String=nameQualifierPos1>=0 ? overloadNamesSpecifier1.substring(nameQualifierPos1+1, overloadNamesSpecifier1.length()-1) : "";
		String[] nameTypes1 = !nameTypes1String.isEmpty() ? nameTypes1String.split(",") : new String[0];

		String nameTypes2String = nameQualifierPos2>=0 ? overloadNamesSpecifier2.substring(nameQualifierPos2+1, overloadNamesSpecifier2.length()-1) : "";
		String[] nameTypes2 = !nameTypes2String.isEmpty() ? nameTypes2String.split(",") : new String[0];

		if (nameTypes1.length!=nameTypes2.length)
			return false;


		String defaultPackage = "java.lang";
		for (int i=0; i<nameTypes1.length; ++i)
		{
			String type1 = nameTypes1[i].trim();
			if (ignoreAllPackages || hasPackage(type1, defaultPackage))
				type1=getUnqualifiedName(type1);

			String type2 = nameTypes2[i].trim();
			if (ignoreAllPackages || hasPackage(type2, defaultPackage))
				type2=getUnqualifiedName(type2);

			if (!type1.equals("*") && !type2.equals("*") && !type1.equals(type2))
				return false;
		}

		return true;
	}
}
