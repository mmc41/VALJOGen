/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;
import com.fortyoneconcepts.valjogen.model.util.ThisReference;

/**
 * Contains methods that return the effective configuration taking processor options and annotated elements into account.
 *
 * All getters return simple values. No nulls are ever returned.
 *
 * @author mmc
 */
public final class Configuration implements ConfigurationOptionKeys
{
	 private final String sourceElementName;
	 private final VALJOGenerate generateAnnotation;
	 private final VALJOConfigure configureAnnotation;
	 private final Locale optDefaultLocale;
	 private final Map<String,String> options;
	 private final Date processorExecutionDate;

	 @SuppressWarnings("serial")
	 private final HashMap<String, Supplier<String>> macros = new HashMap<String, Supplier<String>>() {{
		 put(ConfigurationMacros.NotApplicableMacro, () -> null);
		 put(ConfigurationMacros.GeneratedClassNameMacro, () -> ThisReference.class.getName());
		 put(ConfigurationMacros.MasterInterfaceMacro, () -> getSourceElementName());
		 put(ConfigurationMacros.ExecutionDateMacro, () -> String.format("%tFT%<tRZ", getExecutionDate()));
	 }};

	 public Configuration(String sourceElementName, VALJOGenerate annotation, Locale optDefaultLocale, Map<String,String> options)
	 {
		 this(sourceElementName, annotation, new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class).build(), optDefaultLocale, options);
	 }

	 public Configuration(String sourceElementName, VALJOGenerate annotation, VALJOConfigure configureAnnotation, Locale optDefaultLocale, Map<String,String> options)
	 {
		 this.sourceElementName=sourceElementName;
		 this.generateAnnotation=Objects.requireNonNull(annotation);
		 this.configureAnnotation=Objects.requireNonNull(configureAnnotation);
		 this.optDefaultLocale=optDefaultLocale;
		 this.options=Objects.requireNonNull(options);
		 this.processorExecutionDate=new Date();
	 }

	 public String getSourceElementName()
	 {
		 return sourceElementName;
	 }

	 public Date getExecutionDate()
	 {
		 return processorExecutionDate;
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
		 return getIntValue(lineWidth, configureAnnotation.lineWidth());
	 }

	 public String getClazzScope()
	 {
		 return getStringValue(clazzScope, configureAnnotation.clazzScope());
	 }

	 public boolean hasEmptyClazzScope()
	 {
		 return getClazzScope().length()==0;
	 }

	 public boolean isFinalMembersEnabled()
	 {
		 return getBooleanValue(finalMembersEnabled, configureAnnotation.finalMembersEnabled());
	 }

	 public boolean isFinalClassEnabled()
	 {
		 return getBooleanValue(finalClassEnabled, configureAnnotation.finalClassEnabled());
	 }

	 public boolean isFinalPropertiesEnabled()
	 {
		 return getBooleanValue(finalPropertiesEnabled, configureAnnotation.finalPropertiesEnabled());
	 }

	 public boolean isStaticFactoryMethodEnabled()
	 {
		 return getBooleanValue(staticFactoryMethodEnabled, configureAnnotation.staticFactoryMethodEnabled());
	 }

	 public boolean isEnsureNotNullEnabled()
	 {
		 return getBooleanValue(ensureNotNullEnabled, configureAnnotation.ensureNotNullEnabled());
	 }

	 public boolean isSynchronizedAccessEnabled()
	 {
		 return getBooleanValue(synchronizedAccessEnabled, configureAnnotation.synchronizedAccessEnabled());
	 }

	 public boolean isMalformedPropertiesIgnored()
	 {
		 return getBooleanValue(ignoreMalformedProperties, configureAnnotation.ignoreMalformedProperties());
	 }

	 public String getPackage()
	 {
	    return getStringValue(outputPackage, configureAnnotation.outputPackage());
	 }

	 public String getName()
	 {
         return getStringValue(name, generateAnnotation.name());
	 }

	 public String[] getImportClasses()
	 {
		 return getStringArrayValue(importClasses, configureAnnotation.importClasses());
	 }

	 public String[] getExtraInterfaces()
	 {
		 return getStringArrayValue(extraInterfaceNames, configureAnnotation.extraInterfaceNames());
	 }

	 public String[] getGetterPrefixes()
	 {
		 return getStringArrayValue(getterPrefixes, configureAnnotation.getterPrefixes());
	 }

	 public String[] getSetterPrefixes()
	 {
		 return getStringArrayValue(setterPrefixes, configureAnnotation.setterPrefixes());
	 }

	 public boolean isThisAsImmutableSetterReturnTypeEnabled()
	 {
		 return getBooleanValue(forceThisAsImmutableSetterReturnType, configureAnnotation.forceThisAsImmutableSetterReturnType());
	 }

	 public String getBaseClazzName()
	 {
		 return getStringValue(baseClazzName, configureAnnotation.baseClazzName());
	 }

	 public String getSuggestedVariablesPrefix()
	 {
		 return getStringValue(suggestedVariablesPrefix, configureAnnotation.suggestedVariablesPrefix());
	 }

	 public long getSerialVersionUID()
	 {
		 return getLongValue(serialVersionUID, configureAnnotation.serialVersionUID());
	 }

	 public boolean getSerialVersionUIDSpecified()
	 {
		 return getSerialVersionUID()!=ConfigurationDefaults.SerialVersionUID_NotSet;
	 }

	 public boolean isEqualsEnabled()
	 {
		 return getBooleanValue(equalsEnabled, configureAnnotation.equalsEnabled());
	 }

	 public boolean isHashEnabled()
	 {
		 return getBooleanValue(hashEnabled, configureAnnotation.hashEnabled());
	 }

	 public boolean isComparableEnabled()
	 {
		 return getBooleanValue(comparableEnabled, configureAnnotation.comparableEnabled());
	 }

	 public String[] getComparableMembers()
	 {
		 return getStringArrayValue(comparableMembers, configureAnnotation.comparableMembers());
	 }

	 public boolean isToStringEnabled()
	 {
		 return getBooleanValue(toStringEnabled, configureAnnotation.toStringEnabled());
	 }

	 public boolean isInsertInheritDocOnMethodsEnabled()
	 {
		 return getBooleanValue(insertInheritDocOnMethodsEnabled, configureAnnotation.insertInheritDocOnMethodsEnabled());
	 }

	 public String getHeaderFileName()
	 {
		 return getStringValue(headerFileName, configureAnnotation.headerFileName());
	 }

	 public String getCustomTemplateFileName()
	 {
		 return getStringValue(customTemplateFileName, configureAnnotation.customTemplateFileName());
	 }

	 public String getClazzJavaDoc()
	 {
		 return getStringValue(clazzJavaDoc, configureAnnotation.clazzJavaDoc());
	 }

	 public String[] getClazzAnnotations()
	 {
		 return getStringArrayValue(clazzAnnotations, configureAnnotation.clazzAnnotations());
	 }

	 public String[] getConstructorAnnotations()
	 {
		 return getStringArrayValue(constructorAnnotations, configureAnnotation.constructorAnnotations());
	 }

	 public String[] getFactoryMethodAnnotations()
	 {
		 return getStringArrayValue(factoryMethodAnnotations, configureAnnotation.factoryMethodAnnotations());
	 }

	 public boolean isWarningAboutSynthesisedNamesEnabled()
	 {
		 return getBooleanValue(warnAboutSynthesisedNames, configureAnnotation.warnAboutSynthesisedNames());
	 }

	 public Level getLogLevel()
	 {
		 String level = getStringValue(logLevel, configureAnnotation.logLevel());
		 if (level!=null)
			 return Level.parse(level);
		 return Level.ALL;
	 }

	 public boolean isDebugStringTemplatesEnabled()
	 {
		 return getBooleanValue(debugStringTemplates, configureAnnotation.debugStringTemplates());
	 }

	 public Set<String> getImplementedMethodNames()
	 {
		 String[] names = getStringArrayValue(implementedMethodNames, configureAnnotation.implementedMethodNames());
		 return new HashSet<String>(Arrays.asList(names));
	 }

	 public String getComment()
	 {
		 return getStringValue(comment, generateAnnotation.comment(), configureAnnotation.comment());
	 }

	 // ---- Internal helpers -----

	 private String preformMagicReplacements(String rawValue)
	 {
		if (rawValue==null)
			return null;

		String value = rawValue;
		for (Map.Entry<String, Supplier<String>> entry : macros.entrySet()) {
			String key = entry.getKey();
			String replacement = entry.getValue().get();
			if (value.equals(key)) {
			  value=replacement;
			  break;
			}
			else if (replacement!=null)
			  value=value.replace(key, replacement);
		}

		if (value!=null && value.matches("[^\\$]*\\$\\(.*"))
			 throw new IllegalArgumentException("Unknown macros in "+rawValue);

		return value;
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

	 private String getStringValue(String optionKey, String ... rawDefaultValues)
	 {
		 String value = preformMagicReplacements(options.get(ConfigurationDefaults.OPTION_QUALIFIER+optionKey));

		 if (value==null || value.length() == 0 || value.trim().length() == 0) {
			 for (int i=0; i < rawDefaultValues.length && value==null; i++)
			  value=preformMagicReplacements(rawDefaultValues[i]);
		 }

		 if (value!=null)
  		     value=value.trim();

		 return value;
	 }

	 private String[] getStringArrayValue(String optionKey, String[] defaultValue)
	 {
		 String value = preformMagicReplacements(options.get(ConfigurationDefaults.OPTION_QUALIFIER+optionKey));
		 if (value==null)
			 return preformMagicReplacements(defaultValue);

		 value=value.trim();
		 if (value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationMacros.NotApplicableMacro))
			 return defaultValue;

		 return value.split(",");
	 }

	 private boolean getBooleanValue(String optionKey, boolean defaultValue)
	 {
		 String value = options.get(ConfigurationDefaults.OPTION_QUALIFIER+optionKey);
		 if (value==null || value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationMacros.NotApplicableMacro))
		   return defaultValue;
		 if (value.equalsIgnoreCase("false"))
			 return false;
		 if (value.equalsIgnoreCase("true"))
			 return true;

		 throw new IllegalArgumentException("Option value "+value+" for key "+optionKey+" must be a boolean value");
	 }

	 private int getIntValue(String optionKey, int defaultValue)
	 {
		 String value = options.get(ConfigurationDefaults.OPTION_QUALIFIER+optionKey);
		 if (value==null || value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationMacros.NotApplicableMacro))
		   return defaultValue;

		 try {
			 return Integer.parseInt(value);
		 } catch (Throwable e)
		 {
			 throw new IllegalArgumentException("Option value "+value+" for key "+optionKey+" must be an integer value", e);
		 }
	 }

	 private long getLongValue(String optionKey, long defaultValue)
	 {
		 String value = options.get(ConfigurationDefaults.OPTION_QUALIFIER+optionKey);
		 if (value==null || value.length() == 0 || value.trim().length() == 0 || value.equals(ConfigurationMacros.NotApplicableMacro))
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
