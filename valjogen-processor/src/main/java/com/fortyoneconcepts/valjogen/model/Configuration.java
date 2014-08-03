/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;

/**
 * Contains methods that return the effective configuration taking processor options and annotated elements into account.
 *
 * All getters return simple values. No nulls are ever returned.
 *
 * @author mmc
 */
public class Configuration implements ConfigurationOptionKeys
{
	 private final VALJOGenerate generateAnnotation;
	 private final VALJOConfigure configureAnnotation;
	 private final Locale optDefaultLocale;
	 private final Map<String,String> options;

	 private static final int DEFAULT_LINEWIDTH = 60;

	 public Configuration(VALJOGenerate annotation, Locale optDefaultLocale, Map<String,String> options)
	 {
		 this.generateAnnotation=Objects.requireNonNull(annotation);
		 this.configureAnnotation=new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class).build();
		 this.optDefaultLocale=optDefaultLocale;
		 this.options=Objects.requireNonNull(options);
	 }

	 public Configuration(VALJOGenerate annotation, VALJOConfigure configureAnnotation, Locale optDefaultLocale, Map<String,String> options)
	 {
		 this.generateAnnotation=Objects.requireNonNull(annotation);
		 this.configureAnnotation=Objects.requireNonNull(configureAnnotation);
		 this.optDefaultLocale=optDefaultLocale;
		 this.options=Objects.requireNonNull(options);
	 }

	 public String getLocaleTag()
	 {
		 String value = options.get(localeTag);
		 if (value==null)
			 if (optDefaultLocale!=null)
				 value=optDefaultLocale.toLanguageTag();

		 if (value==null)
			 value=configureAnnotation.localeTag();

		 assert(value!=null);

		 return value;
	 }

	 public Locale getLocale()
	 {
		 try {
			 return Locale.forLanguageTag(getLocaleTag());
		 } catch (Throwable e)
		 {
			 throw new IllegalArgumentException("Option value "+getLocaleTag()+" for "+localeTag+" must be known IETF BCP 47 language tag string", e);
		 }
	 }

	 public int getLineWidth()
	 {
		 return getValue(lineWidth, DEFAULT_LINEWIDTH);
	 }

	 public String getClazzScope()
	 {
		 return getValue(clazzScope, configureAnnotation.clazzScope());
	 }

	 public boolean hasEmptyClazzScope()
	 {
		 return getClazzScope().length()==0;
	 }

	 public boolean isFinalMembersEnabled()
	 {
		 return getValue(finalMembersEnabled, configureAnnotation.finalMembersEnabled());
	 }

	 public boolean isFinalClassEnabled()
	 {
		 return getValue(finalClassEnabled, configureAnnotation.finalClassEnabled());
	 }

	 public boolean isFinalPropertiesEnabled()
	 {
		 return getValue(finalPropertiesEnabled, configureAnnotation.finalPropertiesEnabled());
	 }

	 public boolean isSynchronizedAccessEnabled()
	 {
		 return getValue(synchronizedAccessEnabled, configureAnnotation.synchronizedAccessEnabled());
	 }

	 public String getPackage()
	 {
	    return getValue(outputPackage, configureAnnotation.outputPackage());
	 }

	 public String getName()
	 {
         return getValue(name, generateAnnotation.name());
	 }

	 public String[] getExtraInterfaces()
	 {
		 return getValue(extraInterfaceNames, configureAnnotation.extraInterfaceNames());
	 }

	 public String getBaseClazzName()
	 {
		 return getValue(baseClazzName, configureAnnotation.baseClazzName());
	 }

	 public String getSuggestedVariablesPrefix()
	 {
		 return getValue(suggestedVariablesPrefix, configureAnnotation.suggestedVariablesPrefix());
	 }

	 public boolean isSerializableEnabled()
	 {
		 return getValue(serializableEnabled, configureAnnotation.serializableEnabled());
	 }

	 public boolean isEqualsEnabled()
	 {
		 return getValue(equalsEnabled, configureAnnotation.equalsEnabled());
	 }

	 public boolean isHashEnabled()
	 {
		 return getValue(hashEnabled, configureAnnotation.hashEnabled());
	 }

	 public boolean isComparableEnabled()
	 {
		 return getValue(comparableEnabled, configureAnnotation.comparableEnabled());
	 }

	 public boolean isToStringEnabled()
	 {
		 return getValue(toStringEnabled, configureAnnotation.toStringEnabled());
	 }

	 public boolean isJavadDocEnabled()
	 {
		 return getValue(javadDocEnabled, configureAnnotation.javadDocEnabled());
	 }

	 // ---- Internal helpers -----

	 private String getValue(String optionKey, String defaultValue)
	 {
		 return options.getOrDefault(optionKey, defaultValue).trim();
	 }

	 private String[] getValue(String optionKey, String[] defaultValue)
	 {
		 String value = options.get(optionKey);
		 if (value==null)
			 return defaultValue;

		 value=value.trim();
		 if (value.length() == 0 || value.trim().length() == 0)
			 return defaultValue;

		 return value.split(",");
	 }

	 private boolean getValue(String optionKey, boolean defaultValue)
	 {
		 String value = options.get(optionKey);
		 if (value==null || value.length() == 0 || value.trim().length() == 0)
		   return defaultValue;
		 if (value.equalsIgnoreCase("false"))
			 return false;
		 if (value.equalsIgnoreCase("true"))
			 return true;

		 throw new IllegalArgumentException("Option value "+value+" for key "+optionKey+" must be a boolean value");
	 }

	 private int getValue(String optionKey, int defaultValue)
	 {
		 String value = options.get(optionKey);
		 if (value==null || value.length() == 0 || value.trim().length() == 0)
		   return defaultValue;

		 try {
			 return Integer.parseInt(value);
		 } catch (Throwable e)
		 {
			 throw new IllegalArgumentException("Option value "+value+" for key "+optionKey+" must be an integer value", e);
		 }
	 }
}
