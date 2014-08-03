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
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#localeTag(String)
	 */
	 public static String localeTag = "localeTag";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#lineWidth(String)
	 */
	 public static String lineWidth = "lineWidth";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#clazzScope(String)
	 */
	 public static String clazzScope = "clazzScope";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#finalMembersEnabled(String)
	 */
	 public static String finalMembersEnabled = "finalMembersEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#finalClassEnabled(String)
	 */
	 public static String finalClassEnabled = "finalClassEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#finalPropertiesEnabled(String)
	 */
	 public static String finalPropertiesEnabled = "finalPropertiesEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#synchronizedAccessEnabled(String)
	 */
	 public static String synchronizedAccessEnabled = "synchronizedAccessEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#outputPackage(String)
	 */
	 public static String outputPackage = "outputPackage";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#suggestedVariablesPrefix(String)
	 */
	 public static String suggestedVariablesPrefix = "suggestedVariablesPrefix";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#serializableEnabled(String)
	 */
	 public static String serializableEnabled = "serializableEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#equalsEnabled(String)
	 */
	 public static String equalsEnabled = "equalsEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#hashEnabled(String)
	 */
	 public static String hashEnabled = "hashEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#comparableEnabled(String)
	 */
	 public static String comparableEnabled = "comparableEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#toStringEnabled(String)
	 */
	 public static String toStringEnabled = "toStringEnabled";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#javadDocEnabled(String)
	 */
	 public static String javadDocEnabled = "javadDocEnabled";

	 /**
	 *
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOGenerate#name(String)
	 */
	 public static String name = "name";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#extraInterfaceNames(String)
	 */
	 public static String extraInterfaceNames = "extraInterfaceNames";

	 /**
	 * @see com.fortyoneconcepts.valjogen.annotation.VALJOConfigure#baseClazzName(String)
	 */
	 public static String baseClazzName = "baseClazzName";
}
