/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Specifies what is implemented already and what will be implemented by generated class and what is missing.
 *
 * @author mmc
 */
public enum ImplementationInfo
{
	/**
	* Not implemented.
	*/
	IMPLEMENTATION_MISSING,

	/**
	* Implemented by base class.
	*/
	IMPLEMENTATION_PROVIDED_BY_BASE_OBJECT,

	/**
	* Default metod.
	*/
	IMPLEMENTATION_DEFAULT_PROVIDED,

	/**
	* Implemented by the object holding the method or at least assumed implemented by generated class if this is the object holding the method.
	*/
	IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT,

	/**
	* Synthetic method that belongs to no interface where implemention is optional (used for readResolve etc).
	*/
	IMPLEMENTATION_MAGIC,
}
