/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

/**
 * Specifies how to calculate the ST template name for a method.
 *
 * @author mmc
 */
public enum TemplateKind
{
	/**
	 * The template name is the name of the method followed by underscore seperated unqualified type names in parenthesis. All type names are unqualified so not guarenteed to be unique.
	 */
	TYPED,

	/**
	 * The template name is the name of the method. I.e. the same template can handle many different arguments.
	 */
	UNTYPED,

	/**
	 * The template is the fixed constructor template (which can handle many different arguments).
	 */
	CONSTRUCTOR,

	/**
	 * The template is the fixed property template (which can handle many different arguments).
	 */
	PROPERTY
}
