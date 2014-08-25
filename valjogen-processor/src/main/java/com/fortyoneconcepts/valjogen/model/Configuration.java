/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;
import com.fortyoneconcepts.valjogen.model.util.SelfReference;

/**
 * Contains methods that return the effective configuration taking processor options and annotated elements into account.
 *
 * All getters return simple values. No nulls are ever returned.
 *
 * @author mmc
 */
public final class Configuration implements ConfigurationOptionKeys
{
	 private final VALJOGenerate generateAnnotation;
	 private final VALJOConfigure configureAnnotation;
	 private final Locale optDefaultLocale;
	 private final Map<String,String> options;

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
		 return getValue(lineWidth, configureAnnotation.lineWidth());
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

	 public boolean isEnsureNotNullEnabled()
	 {
		 return getValue(ensureNotNullEnabled, configureAnnotation.ensureNotNullEnabled());
	 }

	 public boolean isSynchronizedAccessEnabled()
	 {
		 return getValue(synchronizedAccessEnabled, configureAnnotation.synchronizedAccessEnabled());
	 }

	 public boolean isMalformedPropertiesIgnored()
	 {
		 return getValue(ignoreMalformedProperties, configureAnnotation.ignoreMalformedProperties());
	 }

	 public String getPackage()
	 {
	    return getValue(outputPackage, configureAnnotation.outputPackage());
	 }

	 public String getName()
	 {
         return getValue(name, generateAnnotation.name());
	 }

	 public String[] getImportClasses()
	 {
		 return getValue(importClasses, configureAnnotation.importClasses());
	 }

	 public String[] getExtraInterfaces()
	 {
		 return getValue(extraInterfaceNames, configureAnnotation.extraInterfaceNames());
	 }

	 public String[] getGetterPrefixes()
	 {
		 return getValue(getterPrefixes, configureAnnotation.getterPrefixes());
	 }

	 public String[] getSetterPrefixes()
	 {
		 return getValue(setterPrefixes, configureAnnotation.setterPrefixes());
	 }

	 public String getBaseClazzName()
	 {
		 return getValue(baseClazzName, configureAnnotation.baseClazzName());
	 }

	 public String getSuggestedVariablesPrefix()
	 {
		 return getValue(suggestedVariablesPrefix, configureAnnotation.suggestedVariablesPrefix());
	 }

	 public long getSerialVersionUID()
	 {
		 return getValue(serialVersionUID, configureAnnotation.serialVersionUID());
	 }

	 public boolean getSerialVersionUIDSpecified()
	 {
		 return getSerialVersionUID()!=ConfigurationDefaults.SerialVersionUID_NotSet;
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

	 public boolean isDebugInfoEnabled()
	 {
		 return getValue(debugInfo, configureAnnotation.debugInfo());
	 }

	 public String[] getImplementedMethodNames()
	 {
		 return getValue(implementedMethodNames, configureAnnotation.implementedMethodNames());
	 }

	 // ---- Internal helpers -----

	 private String preformMagicReplacements(String rawValue)
	 {
		if (rawValue==null || rawValue.equals(ConfigurationDefaults.NotApplicable))
			return null;

		return rawValue.replace(ConfigurationDefaults.GeneratedClassNameReference, SelfReference.class.getName());
	 }

	 private String[] preformMagicReplacements(String[] rawValues)
	 {
		if (rawValues==null)
			return null;

		ArrayList<String> values = new ArrayList<String>();
		for (int i=0; i<rawValues.length; ++i)
		{
			String rawValue = rawValues[i];
			String value = preformMagicReplacements(rawValue);
			if (value!=null)
				values.add(value);
		}

		return values.toArray(new String[values.size()]);
	 }

	 private String getValue(String optionKey, String rawDefaultValue)
	 {
		 String value = preformMagicReplacements(options.get(optionKey));

		 if (value==null || value.length() == 0 || value.trim().length() == 0)
			 value=preformMagicReplacements(rawDefaultValue);

		 if (value!=null)
  		     value=value.trim();

		 return value;
	 }

	 private String[] getValue(String optionKey, String[] defaultValue)
	 {
		 String value = preformMagicReplacements(options.get(optionKey));
		 if (value==null)
			 return preformMagicReplacements(defaultValue);

		 value=value.trim();
		 if (value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationDefaults.NotApplicable))
			 return defaultValue;

		 return value.split(",");
	 }

	 private boolean getValue(String optionKey, boolean defaultValue)
	 {
		 String value = options.get(optionKey);
		 if (value==null || value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationDefaults.NotApplicable))
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
		 if (value==null || value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationDefaults.NotApplicable))
		   return defaultValue;

		 try {
			 return Integer.parseInt(value);
		 } catch (Throwable e)
		 {
			 throw new IllegalArgumentException("Option value "+value+" for key "+optionKey+" must be an integer value", e);
		 }
	 }

	 private long getValue(String optionKey, long defaultValue)
	 {
		 String value = options.get(optionKey);
		 if (value==null || value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationDefaults.NotApplicable))
		   return defaultValue;

		 try {
			 return Long.parseLong(value);
		 } catch (Throwable e)
		 {
			 throw new IllegalArgumentException("Option value "+value+" for key "+optionKey+" must be an long integer value", e);
		 }
	 }

	@Override
	public String toString()
	{
		// Create toString by calling all getters programatically so we do not have to maintain this method (which is for debugging only anyway):
		java.lang.reflect.Method[] methods = this.getClass().getMethods();
		String nameValues = Arrays.stream(methods).filter(m -> m.getParameterCount()==0 && m.getDeclaringClass()==this.getClass() && NamesUtil.isGetterMethod(m.getName(), new String[] { "is", "get"}) && !m.getName().equals("toString") && ((m.getModifiers() & java.lang.reflect.Modifier.PUBLIC)!=0)).map(m-> {
			try {
				String name = m.getName();
				Object value = m.invoke(this);
				String stringValue;
				if (value instanceof String[])
					stringValue="["+Arrays.stream((String[])value).map(s -> '"'+s+'"').collect(Collectors.joining(", "))+"]";
				else if (value==null)
					stringValue="null";
			    else stringValue=value.toString();
				return name+"="+stringValue;
			} catch (Exception e)
			{
				return name+"=<error "+e.getMessage()+">";
			}
		}).collect(Collectors.joining(", "));

		return "Configuration [this=@"+ Integer.toHexString(System.identityHashCode(this))+", "+nameValues+"]";
	}
}
