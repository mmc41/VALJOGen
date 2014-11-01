/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fortyoneconcepts.valjogen.model.util.NamesUtil;
import com.fortyoneconcepts.valjogen.processor.STCustomModelAdaptor;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Information about a type that a model use or refer to. Actual types are divided into 3 concrete subclasses depending on the category
 * of the type. I.e. if the type is a primitive, an array or an object.
 *
 * @author mmc
 */
public abstract class Type extends ModelBase
{
	protected BasicClazz clazzUsingType; // May be set by subclass immediately after constructor but not changed afterwards.
	protected final String qualifiedProtoTypicalTypeName;

	protected Type(BasicClazz optClazzUsingType, String qualifiedProtoTypicalTypeName)
	{
		this.clazzUsingType = optClazzUsingType; // Must be set manually after constructor by subclass if null.
		this.qualifiedProtoTypicalTypeName =  Objects.requireNonNull(qualifiedProtoTypicalTypeName);
	}

	public abstract Type copy(BasicClazz optClazzUsingType);

	@Override
	public Configuration getConfiguration()
	{
		return clazzUsingType.getConfiguration();
	}

	@Override
	public BasicClazz getClazz()
	{
		return clazzUsingType;
	}

	/**
	 * Returns type package name of the type.
	 *
	 * @return The package name of the type.
	 */
	@Override
	public String getPackageName()
	{
		return getPackageFromQualifiedName(qualifiedProtoTypicalTypeName);
	}

	/**
	 * Returns a type name with package but without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The qualified type name
	 */
	public String getQualifiedName()
	{
		return stripGenericQualifier(qualifiedProtoTypicalTypeName);
	}

	/**
	 * Returns a full type name with package in front. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The fully qualifid prototypical type name.
	 */
	public String getPrototypicalQualifiedName() {
		return qualifiedProtoTypicalTypeName;
	}

	/**
	 * Checks if the type is in scope of the class being generated taking imports and default packages etc. into account (so it can be used without qualification).
	 *
	 * @return True if type is in scope of the generated class and its imported classes/pacakges.
	 */
	public abstract boolean isInImportScope();

	/**
	 * Returns a simple type name without package unless nedded and without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The simple type name
	 */
	public String getName()
	{
		if (isInImportScope())
			return getSimpleName();
		else return getQualifiedName();
	}

	/**
	 * Returns a simple type name without package and without any generic parts. I.e. no &lt;T&gt; suffix.
	 *
	 * @return The simple type name
	 */
	public String getSimpleName()
	{
		return getUnqualifiedName(getQualifiedName());
	}

	/**
	 * Returns a simple type name without package unless nedded. For generic types this is prototypical. I.e. ClassName&lt;T&gt;
	 *
	 * @return The prototypical type name without any package.
	 */
	public String getPrototypicalName()
	{
		// TODO: unqualify generic arguments also if these are in scope:

		if (isInImportScope())
			return getUnqualifiedName(getPrototypicalQualifiedName());
		else return getPrototypicalQualifiedName();
	}

	/**
	 * Return the name of the type when represented as an object. Only yields a different name for primitives.
	 *
	 * @return The wrapped type name or just type name if no wrapper exist.
	 */
	public String getWrapperName()
	{
	    if (isPrimitive())
	    	return getWrapperTypeName(getName());
	    else return getName();
	}

	/**
	 * Checks if the type is java.lang.Object
	 *
	 * @return True if the type represents java.lang.Object
	 */
	public boolean isRootObject()
	{
	    return false;
	}

    public boolean isPrimitive()
    {
    	return false;
    }

    public boolean isVoid()
    {
    	return false;
    }

    public boolean isPrimitiveByte()
	{
		return false;
	}

	public boolean isPrimitiveBoolean()
	{
		return false;
	}

	public boolean isPrimitiveShort()
	{
		return false;
	}

	public boolean isPrimitiveInt()
	{
		return false;
	}

	public boolean isPrimitiveLong()
	{
		return false;
	}

    public boolean isPrimitiveFloat()
    {
    	return false;
    }

    public boolean isPrimitiveDouble()
    {
    	return false;
    }

	public boolean isArray()
	{
		return false;
	}

	public boolean isObject()
	{
		return false;
	}

	public boolean isMultiDimensionalArray()
	{
		return false;
	}

	public DetailLevel getDetailLevel()
	{
		return DetailLevel.Low;
	}

	public boolean canBeMoreDetailed()
	{
		return false;
	}

	/**
	* Returns if the type is a generated type (Clazz)
	*
	* @return True if this type is the one being generated.
	*/
	public boolean isThisType()
	{
		return false;
	}

	/**
	* Returns if the type is the base class for the generated type (Clazz)
	*
	* @return True if this type is the base class for the class being generated.
	*/
	public boolean isThisSuperType()
	{
		return false;
	}

	/**
	* Returns this overall category (kind) of type this type is.
	*
	* @return The type category.
	*/
    public abstract TypeCategory getTypeCategory();

    /**
     * Returns if type is equal to or implements/inherites from specified class/interface. Unqualified names
     * are checked against java.lang or java.util packages. If not a match the unqualified name is assumed
     * to belong to the same package as the generated class.
     *
     * Nb. {@link STCustomModelAdaptor}  recoginze this method under the magic name ofType_xxx.
     *
     * @param classOrInterfaceName The name (qualified or unqualified) of the class/interface.
     *
     * @return True if type is equal to or implements/inherites from specified class/interface
     */
	public final boolean isOfType(String classOrInterfaceName)
	{
		String qualifiedClassOrInterfaceName;

		if (!isQualifiedName(classOrInterfaceName)) {
			if (NamesUtil.isJavaLangClassName(classOrInterfaceName))
				qualifiedClassOrInterfaceName=ensureQualifedName(classOrInterfaceName, "java.lang");
			else if (NamesUtil.isJavaUtilClassName(classOrInterfaceName))
				qualifiedClassOrInterfaceName=ensureQualifedName(classOrInterfaceName, "java.util");
			else qualifiedClassOrInterfaceName=ensureQualifedName(classOrInterfaceName, this.getGeneratedClazz().getPackageName());
		} else qualifiedClassOrInterfaceName=classOrInterfaceName;

		return isOfQualifiedType(qualifiedClassOrInterfaceName);
	}

    /**
     * Returns if type is equal to or implements/inherites from specified qualified class/interface. Called by isOfType with the qualified name (possibly expanded).
     *
     * @param qualifiedClassOrInterfaceName The qualified name of the class/interface.
     *
     * @return True if type is equal to or implements/inherites from specified class/interface
     */
	public boolean isOfQualifiedType(String qualifiedClassOrInterfaceName)
	{
		if (getQualifiedName().equals(qualifiedClassOrInterfaceName))
			return true;

		// The default implementation just use reflection for a known classes. Other implementations can deal with new classes also.
		Class<?> clazz = tryGetReflectionClass();
		if (clazz==null)
			throw new RuntimeException("No detailed information available about "+getQualifiedName());

		Stream<Class<?>> clazzAndSuperClazzes = getReflectionSuperTypesWithAscendants(clazz);

		clazzAndSuperClazzes = getReflectionSuperTypesWithAscendants(clazz);

		boolean match = clazzAndSuperClazzes.anyMatch( c -> c.getName().equals(qualifiedClassOrInterfaceName));
		return match;
	}

	/**
	 * Returns if type has a static non-private method with the specified overload name.
	 *
	 * Nb. {@link STCustomModelAdaptor} recoginze this method under the magic name staticMethod_xxx.
	 *
	 * Note that because of type erasure the type of argument may be a plain Object when using reflection for lookup as used in default implementation
	 * When this method is overidden erasure is not used so this should not be an issue for overriding subclasses.
	 *
	 * @param overloadName overload name of form <code> &lt;methodName&gt; "(" &lt;unqualifed parameter type name&gt; { "," &lt;unqualifed parameter type name&gt; } ")"</code>
	 *
	 * @return True if method exist.
	 */
	public boolean hasStaticMethod(String overloadName)
	{
		// The default implementation just use reflection for a known classes. Other implementations can deal with new classes also.
		Class<?> clazz = tryGetReflectionClass();
		if (clazz==null)
			throw new RuntimeException("No detailed information available about "+getQualifiedName());

		final int reqModifierFlags = java.lang.reflect.Modifier.STATIC;
		final int reqNotModifierFlags = java.lang.reflect.Modifier.PRIVATE;
		return Arrays.stream(clazz.getMethods()).anyMatch(m -> matchingOverloads(getReflectionOverloadName(m, true), overloadName, false) && ((m.getModifiers() & reqModifierFlags)==reqModifierFlags) && ((m.getModifiers() & reqNotModifierFlags)==0));
	}

	/**
	 * Returns if type has a non-private instance method with the specified overload name.
	 *
	 * Note that because of type erasure the type of argument may be a plain Object when using reflection for lookup as used in default implementation
	 * When this method is overidden erasure is not used so this should not be an issue for overriding subclasses.
	 *
	 * Nb. {@link STCustomModelAdaptor} recoginze this method under the magic name instanceMethod_xxx.
	 *
	 * @param overloadName overload name of form <code> &lt;methodName&gt; "(" &lt;unqualifed parameter type name&gt; { "," &lt;unqualifed parameter type name&gt; } ")"</code>
	 *
	 * @return True if method exist.
	 */
	public boolean hasInstanceMethod(String overloadName)
	{
		// The default implementation just use reflection for a known classes. Other implementations can deal with new classes also.
		Class<?> clazz = tryGetReflectionClass();
		if (clazz==null)
			throw new RuntimeException("No detailed information available about "+getQualifiedName());

		final int reqNotModifierFlags = java.lang.reflect.Modifier.STATIC | java.lang.reflect.Modifier.PRIVATE;
		return Arrays.stream(clazz.getMethods()).anyMatch(m -> matchingOverloads(getReflectionOverloadName(m, true), overloadName, false) && ((m.getModifiers() & reqNotModifierFlags)==0));
	}

	/**
	 * Returns if type has a static non-private field with the specified name.
	 *
	 * Nb. {@link STCustomModelAdaptor} recoginze this method under the magic name staticMember_xxx.
	 *
	 * @param name name of field.
	 *
	 * @return True if field exist.
	 */
	public boolean hasStaticMember(String name)
	{
		// The default implementation just use reflection for a known classes. Other implementations can deal with new classes also.
		Class<?> clazz = tryGetReflectionClass();
		if (clazz==null)
			throw new RuntimeException("No detailed information available about "+getQualifiedName());

		final int reqModifierFlags = java.lang.reflect.Modifier.STATIC;
		final int reqNotModifierFlags = java.lang.reflect.Modifier.PRIVATE;
		return Arrays.stream(clazz.getFields()).anyMatch(f -> f.getName().equals(name) && ((f.getModifiers() & reqModifierFlags)==reqModifierFlags) && ((f.getModifiers() & reqNotModifierFlags)==0));
	}

	/**
	 * Returns if type has a non-private instance member field with the specified name.
	 *
	 * Nb. {@link STCustomModelAdaptor} recoginze this method under the magic name instanceMember_xxx.
	 *
	 * @param name name of field.
	 *
	 * @return True if field exist.
	 */
	public boolean hasInstanceMember(String name)
	{
		// The default implementation just use reflection for a known classes. Other implementations can deal with new classes also.
		Class<?> clazz = tryGetReflectionClass();
		if (clazz==null)
			throw new RuntimeException("No detailed information available about "+getQualifiedName());

		final int reqNotModifierFlags = java.lang.reflect.Modifier.STATIC | java.lang.reflect.Modifier.PRIVATE;
		return Arrays.stream(clazz.getFields()).anyMatch(f -> f.getName().equals(name) && ((f.getModifiers() & reqNotModifierFlags)==0));
	}

	private final Stream<Class<?>> getReflectionSuperTypesWithAscendants(Class<?> clazz)
	{
		Class<?> superClazz = clazz.getSuperclass();
		Stream<Class<?>> interfaces = Arrays.stream(clazz.getInterfaces());

		Stream<Class<?>> all;
		if (superClazz!=null)
			all=Stream.concat(Stream.of(superClazz), interfaces);
		else all=interfaces;

		return Stream.concat(Stream.of(clazz), all.flatMap(c -> getReflectionSuperTypesWithAscendants(c)));
	}

	/**
	 * Only supported for existing classes on classpath. Other implementations that deal with generated/new classes should not use reflection anyway.
	 *
	 * @return The class or null if unsupported.
	 */
	private final Class<?> tryGetReflectionClass()
	{
		try {
	      String qName = getQualifiedName();
		  boolean isArray = qName.endsWith("[]");
		  String lookupName = stripArrrayQualifier(qName);
		  if (isArray) {
			  switch (lookupName)
			  {
			   case "boolean": return boolean[].class;
			   case "char": return char[].class;
			   case "byte": return byte[].class;
			   case "short": return short[].class;
			   case "int": return int[].class;
			   case "long": return long[].class;
			   case "float": return float[].class;
			   case "double": return double[].class;
			   default: return Class.forName("[L"+lookupName+";");
			  }
		  } else {
			  switch (lookupName)
			  {
			   case "boolean": return boolean.class;
			   case "char": return char.class;
			   case "byte": return byte.class;
			   case "short": return short.class;
			   case "int": return int.class;
			   case "long": return long.class;
			   case "float": return float.class;
			   case "double": return double.class;
			   default: return Class.forName(lookupName);
			  }
		  }
		} catch(Exception e)
		{
			return null; // Return null if failed.
		}
	}

	private final String getReflectionOverloadName(java.lang.reflect.Method m, boolean includeMethodName)
	{
		StringBuilder sb = new StringBuilder();

		if (includeMethodName)
			sb.append(m.getName());

		sb.append("(");
		sb.append(Arrays.stream(m.getParameters()).map(p -> {
		  return p.getType().getSimpleName();
		}).collect(Collectors.joining(",")));

		sb.append(")");

		return sb.toString();
	}

	@Override
	public int hashCode()
	{
		return qualifiedProtoTypicalTypeName.hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Type))
			return false;
		Type other = (Type) obj;
		if (clazzUsingType != other.clazzUsingType)
			return false;
		if (qualifiedProtoTypicalTypeName == null) {
			if (other.qualifiedProtoTypicalTypeName != null)
				return false;
		} else if (!qualifiedProtoTypicalTypeName
				.equals(other.qualifiedProtoTypicalTypeName))
			return false;
		return true;
	}
}
