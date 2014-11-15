/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.annotations.types;

/**
 * Used by annotations to specify which xml/json conversions the generated object should support.
 *
 * @author mmc
 */
public enum DataConversion
{
	/**
	* Generated code will not contain any annotations suitable for json or xml conversions.
	*/
	NONE,

	/**
	* Generated code will contain annotations suitable for conversion to/from json using the 3rd party jackson processor compatible with java 6+.
	*
	* @see <a href="https://github.com/FasterXML/jackson-annotations">jackson-annotations</a>
	*/
	JACKSON_DATABIND_ANNOTATIONS,

	/**
	* Generated code will contain annotations suitable for conversion to/from json using the 3rd party jackson processor compatible with java 8+ when using the -parameters option.
	*
	* @see <a href="https://github.com/FasterXML/jackson-annotations">jackson-annotations</a>
	*/
	JACKSON_DATABIND_ANNOTATIONS_WITH_JDK8_PARAMETER_NAMES;
}
