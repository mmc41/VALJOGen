/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Option keys for configuration. Values are exactly the same as the method names of VALJOConfigure or VALJOGenerate annotation.
 * When specified as annotation processor options, they must be qualified with {@link ConfigurationDefaults#OPTION_QUALIFIER} package prefix.
 *
 * See the descriptions on the annotations for details.
 *
 * @author mmc
 */
public interface ConfigurationOptionKeys
{
	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#localeTag
	 */
	 public static String localeTag = "localeTag";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#lineWidth
	 */
	 public static String lineWidth = "lineWidth";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#clazzModifiers
	 */
	 public static String clazzModifiers = "clazzModifiers";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#finalMembersAndParametersEnabled
	 */
	 public static String finalMembersAndParametersEnabled = "finalMembersAndParametersEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#finalMethodsEnabled
	 */
	 public static String finalMethodsEnabled = "finalMethodsEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#staticFactoryMethodEnabled
	 */
	 public static String staticFactoryMethodEnabled = "staticFactoryMethodEnabled";

	 /**
     * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#ensureNotNullEnabled
	 */
	 public static String ensureNotNullEnabled = "ensureNotNullEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#synchronizedAccessEnabled
	 */
	 public static String synchronizedAccessEnabled = "synchronizedAccessEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#outputPackage
	 */
	 public static String outputPackage = "outputPackage";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#suggestedVariablesPrefix
	 */
	 public static String suggestedVariablesPrefix = "suggestedVariablesPrefix";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#serialVersionUID
	 */
	 public static String serialVersionUID = "serialVersionUID";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#equalsEnabled
	 */
	 public static String equalsEnabled = "equalsEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#hashEnabled
	 */
	 public static String hashEnabled = "hashEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#comparableMembers
	 */
	 public static String comparableMembers = "comparableMembers";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#toStringEnabled
	 */
	 public static String toStringEnabled = "toStringEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#insertInheritDocOnMethodsEnabled
	 */
	 public static String insertInheritDocOnMethodsEnabled = "insertInheritDocOnMethodsEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#ignoreMalformedProperties
	 */
	 public static String ignoreMalformedProperties = "ignoreMalformedProperties";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#clazzJavaDoc
	 */
	 public static String clazzJavaDoc = "clazzJavaDoc";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#clazzAnnotations
	 */
	 public static String clazzAnnotations = "clazzAnnotations";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#constructorAnnotations
	 */
	 public static String constructorAnnotations = "constructorAnnotations";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#factoryMethodAnnotations
	 */
	 public static String factoryMethodAnnotations = "factoryMethodAnnotations";

	 /**
	 *
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOGenerate#name
	 */
	 public static String name = "name";

	 /**
	 *
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#importClasses
	 */
	 public static String importClasses = "importClasses";

	 /**
	 *
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#getterPrefixes
	 */
	 public static String getterPrefixes = "getterPrefixes";

	 /**
	 *
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#setterPrefixes
	 */
	 public static String setterPrefixes = "setterPrefixes";

	 /**
	 *
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#forceThisAsImmutableSetterReturnType
	 */
	 public static String forceThisAsImmutableSetterReturnType = "forceThisAsImmutableSetterReturnType";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#extraInterfaceNames
	 */
	 public static String extraInterfaceNames = "extraInterfaceNames";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#baseClazzName
	 */
	 public static String baseClazzName = "baseClazzName";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#headerFileName
	 */
     public static String headerFileName = "headerFileName";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#customJavaTemplateFileName
	 */
     public static String customJavaTemplateFileName = "customJavaTemplateFileName";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#warnAboutSynthesisedNames
	 */
     public static String warnAboutSynthesisedNames = "warnAboutSynthesisedNames";

     /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#logLevel
	 */
     public static String logLevel = "logLevel";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#debugStringTemplates
	 */
	 public static String debugStringTemplates = "debugStringTemplates";

	 /**
	 * Java source path to where source code is stored. Required option for some other options to work. Differently from all other options, this is an annotation
	 * processor option only (no annotation exist for this). Mutiple path entries may be specified seperated by a platform dependent "path.separator" (";" or ":").
	 * Path entries must be absolute and point to existing readable directories.
	 */
	 public static String SOURCEPATH = "SOURCEPATH";

     /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#comment
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOGenerate#comment
	 */
     public static String comment = "comment";
}
