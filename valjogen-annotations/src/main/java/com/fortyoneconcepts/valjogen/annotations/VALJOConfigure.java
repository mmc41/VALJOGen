/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.annotations;

import java.lang.annotation.*;

/**
 * Specifies details about the code that should be generated. May be specified on a package (package-info.java) or on a inteface alongside
 * the {@link VALJOGenerate} annotation. If specified on both a package and an interface then the packge specification is ignored. Has no effect
 * unless affected interfaces also has a {@link VALJOGenerate} annotation. All details may be overruled by setting indentically named key/values
 * in the annotation processor.
 *
 * @author mmc
 */
@Retention(RetentionPolicy.SOURCE)
@Target(value={ElementType.TYPE, ElementType.PACKAGE})
public @interface VALJOConfigure
{
	/**
	* Package name of generated class. May be overruled by a fully qualified name on VALJOGenerate or by equivalent annotation processor key.
	*/
    String outputPackage() default "";

	/**
	* Access modifier for generated class. May be overruled by equivalent annotation processor key.
	*/
    String clazzScope() default "public";

	/**
	*  Experimental IETF BCP 47 language tag string descripting internal locale to use for annotation processor. May be overruled by equivalent annotation processor key.
	*
	*  @see Locale.forLanguageTag
	*/
    String localeTag() default "en-US";

	/**
	* Linewidth for generated code. 0 if unlimited. May be overruled by equivalent annotation processor key.
	*/
    int lineWidth() default 0;

	/**
	* Specifies if generated members should be final if possible. May be overruled by equivalent annotation processor key.
	*/
    boolean finalMembersEnabled() default true;

	/**
	* Specifies if generated classes should be final if possible. May be overruled by equivalent annotation processor key.
	*/
    boolean finalClassEnabled() default true;

	/**
	* Specifies if generated property methods should be final if possible. May be overruled by equivalent annotation processor key.
	*/
	boolean finalPropertiesEnabled() default true;

	/**
	* Specifies if generated properties/methods for mutable members should be synchronized. May be overruled by equivalent annotation processor key.
	*/
	boolean synchronizedAccessEnabled() default true;

	/**
	* Specifies prefix to use by temporary variables in method. May be overruled by equivalent annotation processor key.
	*/
	String suggestedVariablesPrefix() default "_";

	/**
	* Specifies if class should be serializable. May be overruled by equivalent annotation processor key.
	*
	* NB: NOT IMPLEMENTED YET!
	*/
	boolean serializableEnabled() default true;

	/**
	* Specifies if equals method should be generated for the class. May be overruled by equivalent annotation processor key.
	*/
	boolean equalsEnabled() default true;

	/**
	* Specifies if hash method should be generated for the class. May be overruled by equivalent annotation processor key.
	*/
	boolean hashEnabled() default true;

	/**
	* Specifies if compareTo method should be generated for the class. May be overruled by equivalent annotation processor key.
	*
	*  NB: NOT IMPLEMENTED YET!
	*/
	boolean comparableEnabled() default true;

	/**
	* Specifies if a toString method should be generated the class. May be overruled by equivalent annotation processor key.
	*
	*/
	boolean toStringEnabled() default true;

	/**
	* Specifies if javaDoc should be added to the generated class. May be overruled by equivalent annotation processor key.
	*
	* NB: In progress - NOT IMPLEMENTED YET!
	*/
	boolean javadDocEnabled() default true;

	/**
	* Specifies if javaDoc should be added to the generated class. May be overruled by equivalent annotation processor key.
	*
	* NB: NOT IMPLEMENTED YET!
	*/
    String[] extraInterfaceNames() default {};

	/**
	* Specifies if javaDoc should be added to the generated class. May be overruled by equivalent annotation processor key.
	*
	* NB: In progress - NOT FULLY IMPLEMENTED YET!
	*/
    String baseClazzName() default "";
}
