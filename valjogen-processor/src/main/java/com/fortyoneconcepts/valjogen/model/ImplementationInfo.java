package com.fortyoneconcepts.valjogen.model;

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
	* Assumed implemented by generated class.
	*/
	IMPLEMENTATION_CLAIMED_BY_GENERATED_OBJECT,

	/**
	* Synthetic method that belongs to no interface where implemention is optional (used for readResolve etc).
	*/
	IMPLEMENTATION_MAGIC,
}
