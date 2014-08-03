/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.annotations;

import java.lang.annotation.*;

/**
* Use this annotation on Java interface to request a Value Object Class to be generated that implements the
* interface. See also {@link VALJOConfigure} for how to configure the details of the generated class.
*
* @author mmc
*/
@Retention(RetentionPolicy.SOURCE)
@Target(value=ElementType.TYPE)
public @interface VALJOGenerate
{
	/**
	* Simple name or fully qualified name of generated class. If name is not set then a reasonable class name will
	* be generated automatically.
	*
	* May also (as all other annotated values) be overruled globally with equivalent annotation processor key
	* (even though it is generally a bad idea because of name clashes when generating multiple classes).
	*/
    String name() default "";
}
