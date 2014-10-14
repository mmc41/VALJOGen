package com.fortyoneconcepts.valjogen.model;

import java.util.EnumSet;

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
	 * @return True if a final modifier present.
	 *
	 */
	public default boolean isPublic()
	{
		return getModifiers().contains(Modifier.PUBLIC);
	}

	/**
	 * Checkes if one of the modifiers used for code generation is PRIVATE.
     *
	 * @return True if a final modifier present.
	 *
	 */
	public default boolean isPrivate()
	{
		return getModifiers().contains(Modifier.PRIVATE);
	}

	/**
	 * Checkes if one of the modifiers used for code generation is PROTECTED.
     *
	 * @return True if a final modifier present.
	 *
	 */
	public default boolean isProtected()
	{
		return getModifiers().contains(Modifier.PROTECTED);
	}
}
