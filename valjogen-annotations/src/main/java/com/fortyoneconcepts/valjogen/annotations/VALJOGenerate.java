/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.annotations;

import java.lang.annotation.*;

/**
* Use this annotation on Java interface to request a Value Object Class to be generated that implements the
* interface. See also {@link VALJOConfigure} for how to configure the details of the generated class.
*
* <p><b>Usage example (SimpleInterface.java):</b></p>
* <pre>
* <code>
* <span class="keyword">import</span> <span class="identifier">com.fortyoneconcepts.valjogen.annotations.VALJOGenerate</span>;
*
*{@literal @}VALJOGenerate(<span class="string">"MySimpleImpl"</span>)
* <span class="keyword">public interface</span> <span class="identifier">SimpleInterface</span>
* {
*    <span class="keyword">public</span> <span class="identifier">Object getObjectValue</span>();
*    <span class="keyword">public</span> <span class="identifier">String getStringValue</span>();
* }
* </code>
* </pre>
*
* The above code will instruct the VALJOGen annotation processor to generate a value object class called MySimpleImpl with members
* objectValue (of type Object) and stringValue (of type String).
*
* @author mmc
*/
@Retention(RetentionPolicy.SOURCE)
@Target(value=ElementType.TYPE)
public @interface VALJOGenerate
{
	/**
	* Simple name or fully qualified name of generated class. If name is not specified (set to $(N/A) then a reasonable class name will
	* be generated automatically.
	*
	* May also (as all other annotated values) be overruled globally with equivalent annotation processor key
	* (even though it is generally a bad idea because of name clashes when generating multiple classes).
	*
	* @return The short name or fully qualified name of the generated class.
	*/
    String name() default "$(N/A)";

    /**
	* An option user supplied comment. Not used by default but could potentially be used in a custom template or by a build tool.
	*
	* @return An optional comment.
	*/
    String comment() default "$(N/A)";
}
