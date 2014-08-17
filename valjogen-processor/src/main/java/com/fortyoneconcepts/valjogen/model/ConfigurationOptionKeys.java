/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Option keys for configuration. Values are exactly the same as the method names of VALJOConfigure or VALJOGenerate annotation.
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
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#clazzScope
	 */
	 public static String clazzScope = "clazzScope";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#finalMembersEnabled
	 */
	 public static String finalMembersEnabled = "finalMembersEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#finalClassEnabled
	 */
	 public static String finalClassEnabled = "finalClassEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#finalPropertiesEnabled
	 */
	 public static String finalPropertiesEnabled = "finalPropertiesEnabled";

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
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#serializableEnabled
	 */
	 public static String serializableEnabled = "serializableEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#equalsEnabled
	 */
	 public static String equalsEnabled = "equalsEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#hashEnabled
	 */
	 public static String hashEnabled = "hashEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#comparableEnabled
	 */
	 public static String comparableEnabled = "comparableEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#toStringEnabled
	 */
	 public static String toStringEnabled = "toStringEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#javadDocEnabled
	 */
	 public static String javadDocEnabled = "javadDocEnabled";


	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#ignoreMalformedProperties
	 */
	 public static String ignoreMalformedProperties = "ignoreMalformedProperties";

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
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#extraInterfaceNames
	 */
	 public static String extraInterfaceNames = "extraInterfaceNames";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotations.VALJOConfigure#baseClazzName
	 */
	 public static String baseClazzName = "baseClazzName";
}
