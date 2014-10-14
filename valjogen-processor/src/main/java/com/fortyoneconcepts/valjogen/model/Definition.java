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
	 * The name of the element being defined.
	 *
	 * @return The definition name.
	 */
	public String getName();
}
