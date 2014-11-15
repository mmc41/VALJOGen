/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;
import java.util.List;

/**
 * A model that is also a definition of something like a member, parameter, method or class.
 *
 * @author mmc
 */
public interface Definition extends Model
{
	/**
	 * The name of the element being defined.
	 *
	 * @return The definition name.
	 */
	public String getName();

	/**
	 * Get modifiers as orginally declared in inteface or base class (or none if new method).
	 *
	 * @return The set of orginal modifiers for reference
	 */
	public EnumSet<Modifier> getDeclaredModifiers();

	/**
	 * Get modifiers as they should be used generated code when defining something.
	 *
	 * @return The set of modifiers to use in generated code when defining something.
	 */
	public EnumSet<Modifier> getModifiers();

	/**
	 * Get annotations associated with this definition.
	 *
	 * @return The list of anntations to use in generated code when defining something.
	 */
	public List<Annotation> getAnnotations();

	/**
	 * Checkes if one of the modifiers used for code generation is STATIC.
     *
	 * @return True if a static modifier present.
	 *
	 */
	public default boolean isStatic()
	{
		return getModifiers().contains(Modifier.STATIC);
	}

	/**
	 * Checkes if one of the modifiers used for code generation is ABSTRACT.
     *
	 * @return True if a abstract modifier present.
	 *
	 */
	public default boolean isAbstract()
	{
		return getModifiers().contains(Modifier.ABSTRACT);
	}

	/**
	 * Checkes if one of the modifiers used for code generation is FINAL.
     *
	 * @return True if a final modifier present.
	 *
	 */
	public default boolean isFinal()
	{
		return getModifiers().contains(Modifier.FINAL);
	}

	/**
	 * Checkes if one of the modifiers used for code generation is PUBLIC.
     *
	 * @return True if a public modifier present.
	 *
	 */
	public default boolean isPublic()
	{
		return getModifiers().contains(Modifier.PUBLIC);
	}

	/**
	 * Checkes if one of the modifiers used for code generation is PRIVATE.
     *
	 * @return True if a privage modifier present.
	 *
	 */
	public default boolean isPrivate()
	{
		return getModifiers().contains(Modifier.PRIVATE);
	}

	/**
	 * Checkes if one of the modifiers used for code generation is PROTECTED.
     *
	 * @return True if a protected modifier present.
	 *
	 */
	public default boolean isProtected()
	{
		return getModifiers().contains(Modifier.PROTECTED);
	}
}
