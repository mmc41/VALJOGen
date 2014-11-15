/*
 * Copyright (C) 2014 41concepts Aps
 */
package com.fortyoneconcepts.valjogen.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

import javax.lang.model.SourceVersion;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.annotations.internal.*;
import com.fortyoneconcepts.valjogen.annotations.types.*;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.model.util.KeyValuePair;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/**
 * Contains methods that return the effective configuration taking processor
 * options and annotated elements into account.
 *
 * All getters return simple values. No nulls are ever returned.
 *
 * @author mmc
 */
public class Configuration implements ConfigurationOptionKeys {
	private final String sourceElementName;
	private final VALJOGenerate generateAnnotation;
	private final VALJOConfigure configureAnnotation;
	private final Locale optDefaultLocale;
	private final Map<String, String> options;
	private final Date processorExecutionDate;
	private final SourceVersion targetSourceVersion;

	/**
	 * This decl only shows the custom macros. In addition system properties
	 * will be added as macros too.
	 */
	@SuppressWarnings("serial")
	private final HashMap<String, Supplier<String>> macros = new HashMap<String, Supplier<String>>() {
		{
			put(ConfigurationMacros.NotApplicableMacro, () -> null);
			put(ConfigurationMacros.GeneratedClassNameMacro,() -> ThisReference.class.getName()); // Will be replaced with real class name later.
			put(ConfigurationMacros.MasterInterfaceMacro, () -> getSourceElementName());
			put(ConfigurationMacros.ExecutionDateMacro,	() -> String.format("%tFT%<tRZ", getExecutionDate()));
		}
	};

	public Configuration(String sourceElementName, SourceVersion targetSourceVersion, VALJOGenerate annotation, Locale optDefaultLocale, Map<String, String> options)
	{
		this(sourceElementName, targetSourceVersion, annotation, new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class).build(), optDefaultLocale, options);
	}

	public Configuration(String sourceElementName, SourceVersion targetSourceVersion, VALJOGenerate annotation,	VALJOConfigure configureAnnotation, Locale optDefaultLocale, Map<String, String> options)
	{
		this.sourceElementName = sourceElementName;
		this.targetSourceVersion = targetSourceVersion;
		this.generateAnnotation = Objects.requireNonNull(annotation);
		this.configureAnnotation = Objects.requireNonNull(configureAnnotation);
		this.optDefaultLocale = optDefaultLocale;
		this.options = new HashMap<String, String>(Objects.requireNonNull(options));
		this.processorExecutionDate = new Date();

		// Merge user options with config file options if there are any (with
		// user options taking precedence).
		Properties fileOptions = getConfigFileOptions();
		for (Entry<Object, Object> fileOption : fileOptions.entrySet()) {
			this.options.putIfAbsent((String) fileOption.getKey(),
					(String) fileOption.getValue());
		}

		// All system properties should work as macros too.
		for (Entry<Object, Object> systemProperty : System.getProperties()
				.entrySet()) {
			final String propertyName = (String) systemProperty.getKey();
			final String macroName = ConfigurationMacros.MacroPrefix
					+ propertyName + ConfigurationMacros.MacroSuffix;
			macros.putIfAbsent(macroName,
					() -> System.getProperty(propertyName));
		}
	}

	/**
	 * The java source target version that generated code must conform to.
	 *
	 * @return Required java source version of generated code.
	 */
	public SourceVersion getTargetSourceVersion() {
		return targetSourceVersion;
	}

	/**
	 * Return true if java source target version is 6.0 or higher.
	 *
	 * @return True if at least Java 6.0 version
	 */
	public boolean isTargetSourceVersionJava6OrHigher() {
		return targetSourceVersion.compareTo(SourceVersion.RELEASE_6) >= 0;
	}

	/**
	 * Return true if java source target version is 7.0 or higher.
	 *
	 * @return True if at least Java 7.0 version
	 */
	public boolean isTargetSourceVersionJava7OrHigher() {
		return targetSourceVersion.compareTo(SourceVersion.RELEASE_7) >= 0;
	}

	/**
	 * Return true if java source target version is 8.0 or higher.
	 *
	 * @return True if at least Java 8.0 version
	 */
	public boolean isTargetSourceVersionJava8OrHigher() {
		return targetSourceVersion.compareTo(SourceVersion.RELEASE_8) >= 0;
	}

	/**
	 * Return a property object with deserialized configuration file options (if
	 * the file exist).
	 *
	 * @return The properties from the configuration file or empty object
	 *         otherwise.
	 */
	private final Properties getConfigFileOptions() {
		Properties properties = new Properties();

		try (InputStream in = getClass().getResourceAsStream(
				"/" + ConfigurationDefaults.SETTINGS_CONFIG_FILE)) {
			if (in != null)
				properties.load(in);
		} catch (IOException e) {
			// Ignore errors.
		}
		;

		return properties;
	}

	public String getSourceElementName() {
		return sourceElementName;
	}

	public Date getExecutionDate() {
		return processorExecutionDate;
	}

	public String getLocaleTag() {
		String value = options.get(localeTag);
		if (value == null)
			if (optDefaultLocale != null)
				value = optDefaultLocale.toLanguageTag();

		if (value == null)
			value = configureAnnotation.localeTag();

		assert (value != null);

		return value;
	}

	public Locale getLocale() {
		try {
			return Locale.forLanguageTag(getLocaleTag());
		} catch (Throwable e) {
			throw new IllegalArgumentException("Option value " + getLocaleTag()
					+ " for " + localeTag
					+ " must be known IETF BCP 47 language tag string", e);
		}
	}

	public int getLineWidth() {
		return getIntValue(lineWidth, configureAnnotation.lineWidth());
	}

	public EnumSet<Modifier> getClazzModifiers() {
		String[] modifierStrings = getStringArrayValue(clazzModifiers, configureAnnotation.clazzModifiers());
		if (modifierStrings.length == 0)
			return null;

		Set<Modifier> modifers = new HashSet<Modifier>();
		for (String modifierString : modifierStrings) {
			Modifier modifier;

			try {
				modifier = Enum.valueOf(Modifier.class,
						modifierString.toUpperCase());
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Option value "
						+ modifierString + " for key " + clazzModifiers
						+ " must be a known modifier string", e);
			}

			modifers.add(modifier);
		}

		return modifers.size() > 0 ? EnumSet.copyOf(modifers) : EnumSet
				.noneOf(Modifier.class);
	}

	public Mutability getMutability() {
		return getEnumValue(mutability, Mutability.class, configureAnnotation.mutability());
	}

	public DataConversion getDataConversion() {
		return getEnumValue(dataConversion, DataConversion.class, configureAnnotation.dataConversion());
	}

	public boolean isFinalMembersAndParametersEnabled() {
		return getBooleanValue(finalMembersAndParametersEnabled, configureAnnotation.finalMembersAndParametersEnabled());
	}

	public boolean isFinalMethodsEnabled() {
		return getBooleanValue(finalMethodsEnabled,	configureAnnotation.finalMethodsEnabled());
	}

	public boolean isStaticFactoryMethodEnabled() {
		return getBooleanValue(staticFactoryMethodEnabled, configureAnnotation.staticFactoryMethodEnabled());
	}

	public boolean isEnsureNotNullEnabled() {
		return getBooleanValue(ensureNotNullEnabled, configureAnnotation.ensureNotNullEnabled());
	}

	public boolean isSynchronizedAccessEnabled() {
		return getBooleanValue(synchronizedAccessEnabled, configureAnnotation.synchronizedAccessEnabled());
	}

	public boolean isMalformedPropertiesIgnored() {
		return getBooleanValue(ignoreMalformedProperties, configureAnnotation.ignoreMalformedProperties());
	}

	public String getPackage() {
		return getStringValue(outputPackage, configureAnnotation.outputPackage());
	}

	public String getName() {
		return getStringValue(name, generateAnnotation.name());
	}

	public String[] getImportClasses() {
		return getStringArrayValue(importClasses, configureAnnotation.importClasses());
	}

	public String[] getExtraInterfaces() {
		return getStringArrayValue(extraInterfaceNames, configureAnnotation.extraInterfaceNames());
	}

	public String[] getGetterPrefixes() {
		return getStringArrayValue(getterPrefixes, configureAnnotation.getterPrefixes());
	}

	public String[] getSetterPrefixes() {
		return getStringArrayValue(setterPrefixes, configureAnnotation.setterPrefixes());
	}

	public boolean isThisAsImmutableSetterReturnTypeEnabled() {
		return getBooleanValue(forceThisAsImmutableSetterReturnType, configureAnnotation.forceThisAsImmutableSetterReturnType());
	}

	public String getBaseClazzName() {
		return getStringValue(baseClazzName, configureAnnotation.baseClazzName());
	}

	public String[] getBaseClazzConstructors() {
		return getStringArrayValue(baseClazzConstructors, configureAnnotation.baseClazzConstructors());
	}

	public String getSuggestedVariablesPrefix() {
		return getStringValue(suggestedVariablesPrefix,	configureAnnotation.suggestedVariablesPrefix());
	}

	public long getSerialVersionUID() {
		return getLongValue(serialVersionUID,
				configureAnnotation.serialVersionUID());
	}

	public boolean getSerialVersionUIDSpecified() {
		return getSerialVersionUID() != ConfigurationDefaults.SerialVersionUID_NotSet;
	}

	public boolean isEqualsEnabled() {
		return getBooleanValue(equalsEnabled, configureAnnotation.equalsEnabled());
	}

	public boolean isHashEnabled() {
		return getBooleanValue(hashEnabled, configureAnnotation.hashEnabled());
	}

	public String[] getComparableMembers() {
		return getStringArrayValue(comparableMembers, configureAnnotation.comparableMembers());
	}

	public boolean isToStringEnabled() {
		return getBooleanValue(toStringEnabled,	configureAnnotation.toStringEnabled());
	}

	public boolean isInsertInheritDocOnMethodsEnabled() {
		return getBooleanValue(insertInheritDocOnMethodsEnabled, configureAnnotation.insertInheritDocOnMethodsEnabled());
	}

	public String getHeaderFileName() {
		return getStringValue(headerFileName, configureAnnotation.headerFileName());
	}

	public String getCustomJavaTemplateFileName() {
		return getStringValue(customJavaTemplateFileName, configureAnnotation.customJavaTemplateFileName());
	}

	public String getClazzJavaDoc() {
		return getStringValue(clazzJavaDoc, configureAnnotation.clazzJavaDoc());
	}

	public String[] getClazzAnnotations() {
		return getStringArrayValue(clazzAnnotations, configureAnnotation.clazzAnnotations());
	}

	public List<KeyValuePair<String, String>> getMethodAnnotations() {
		return getMethodAnnotations(p -> true);
	}

	public List<KeyValuePair<String, String>> getMethodAnnotations(Predicate<String> overloadNameFilter) {
		String[] pairs = getStringArrayValue(methodAnnotations,	configureAnnotation.methodAnnotations());
		List<KeyValuePair<String, String>> result = pairsToMap(pairs, overloadNameFilter);
		return result;
	}

	public List<KeyValuePair<String, String>> getMemberAnnotations() {
		return getMemberAnnotations(p -> true);
	}

	public List<KeyValuePair<String, String>> getMemberAnnotations(Predicate<String> overloadNameFilter) {
		String[] pairs = getStringArrayValue(memberAnnotations,	configureAnnotation.memberAnnotations());
		List<KeyValuePair<String, String>> result = pairsToMap(pairs, overloadNameFilter);
		return result;
	}

	public boolean isWarningAboutSynthesisedNamesEnabled() {
		return getBooleanValue(warnAboutSynthesisedNames, configureAnnotation.warnAboutSynthesisedNames());
	}

	public Level getLogLevel() {
		String level = getStringValue(logLevel, configureAnnotation.logLevel());
		if (level != null)
			return Level.parse(level);
		return Level.ALL;
	}

	public boolean isDebugStringTemplatesEnabled() {
		return getBooleanValue(debugStringTemplates, configureAnnotation.debugStringTemplates());
	}

	public String getComment() {
		return getStringValue(comment, generateAnnotation.comment(),
				configureAnnotation.comment());
	}

	public String getSourcePath() {
		return getStringValue(SOURCEPATH);
	}

	public String getLogFile() {
		return getStringValue(LOGFILE);
	}

	// ---- Internal helpers -----

	private String preformMagicReplacements(String rawValue)
	{
		if (rawValue == null)
			return null;

		String value = rawValue;
		for (Map.Entry<String, Supplier<String>> entry : macros.entrySet()) {
			String key = entry.getKey();
			String replacement = entry.getValue().get();
			if (value.equals(key)) {
				value = replacement;
				break;
			} else if (replacement != null)
				value = value.replace(key, replacement);
		}

		if (value != null && value.matches("[^\\$]*\\$\\(.*"))
			throw new IllegalArgumentException("Unknown macros in " + rawValue);

		return value;
	}

	private String[] preformMagicReplacements(String[] rawValues)
	{
		if (rawValues == null)
			return null;

		ArrayList<String> values = new ArrayList<String>();
		for (int i = 0; i < rawValues.length; ++i) {
			String rawValue = rawValues[i];
			String value = preformMagicReplacements(rawValue);
			if (value != null)
				values.add(value);
		}

		return values.toArray(new String[values.size()]);
	}

	private String getStringValue(String optionKey, String... rawDefaultValues)
	{
		String value = preformMagicReplacements(options
				.get(ConfigurationDefaults.OPTION_QUALIFIER + optionKey));

		if (value == null || value.length() == 0 || value.trim().length() == 0) {
			for (int i = 0; i < rawDefaultValues.length && value == null; i++)
				value = preformMagicReplacements(rawDefaultValues[i]);
		}

		if (value != null)
			value = value.trim();

		return value;
	}

	private String[] getStringArrayValue(String optionKey, String[] defaultValue)
	{
		String value = preformMagicReplacements(options
				.get(ConfigurationDefaults.OPTION_QUALIFIER + optionKey));
		if (value == null)
			return preformMagicReplacements(defaultValue);

		value = value.trim();
		if (value.length() == 0 || value.trim().length() == 0
				|| value.equals(ConfigurationMacros.NotApplicableMacro))
			return defaultValue;

		return value.split(",");
	}

	private <T extends Enum<T>> T getEnumValue(String optionKey, Class<T> enumType, T defaultValue)
	{
		String value = options.get(ConfigurationDefaults.OPTION_QUALIFIER
				+ optionKey);
		if (value == null || value.length() == 0 || value.trim().length() == 0
				|| value.equals(ConfigurationMacros.NotApplicableMacro))
			return defaultValue;

		try {
			return Enum.valueOf(enumType, value);
		} catch (Throwable e) {
			throw new IllegalArgumentException("Option value " + value
					+ " for key " + optionKey
					+ " must be an enum value of type" + enumType.toString(), e);
		}
	}

	private boolean getBooleanValue(String optionKey, boolean defaultValue)
	{
		String value = options.get(ConfigurationDefaults.OPTION_QUALIFIER
				+ optionKey);
		if (value == null || value.length() == 0 || value.trim().length() == 0
				|| value.equals(ConfigurationMacros.NotApplicableMacro))
			return defaultValue;
		if (value.equalsIgnoreCase("false"))
			return false;
		if (value.equalsIgnoreCase("true"))
			return true;

		throw new IllegalArgumentException("Option value " + value
				+ " for key " + optionKey + " must be a boolean value");
	}

	private int getIntValue(String optionKey, int defaultValue)
	{
		String value = options.get(ConfigurationDefaults.OPTION_QUALIFIER
				+ optionKey);
		if (value == null || value.length() == 0 || value.trim().length() == 0
				|| value.equals(ConfigurationMacros.NotApplicableMacro))
			return defaultValue;

		try {
			return Integer.parseInt(value);
		} catch (Throwable e) {
			throw new IllegalArgumentException("Option value " + value
					+ " for key " + optionKey + " must be an integer value", e);
		}
	}

	private long getLongValue(String optionKey, long defaultValue)
	{
		String value = options.get(ConfigurationDefaults.OPTION_QUALIFIER
				+ optionKey);
		if (value == null || value.length() == 0 || value.trim().length() == 0
				|| value.equals(ConfigurationMacros.NotApplicableMacro))
			return defaultValue;

		try {
			return Long.parseLong(value);
		} catch (Throwable e) {
			throw new IllegalArgumentException("Option value " + value
					+ " for key " + optionKey
					+ " must be an long integer value", e);
		}
	}

	private List<KeyValuePair<String, String>> pairsToMap(String[] keyValueStrings, Predicate<String> keyFilter)
	{
		List<KeyValuePair<String, String>> result = new ArrayList<KeyValuePair<String, String>>();

		for (int i = 0; i < keyValueStrings.length; ++i) {
			String keyValue = keyValueStrings[i];
			int sepIndex = keyValue.indexOf('=');

			if (sepIndex == 0 || sepIndex + 1 >= keyValue.length())
				throw new IllegalArgumentException(
						"Illegal key value specification " + keyValue);

			String key = keyValue.substring(0, sepIndex);
			String value = keyValue.substring(sepIndex + 1);

			if (keyFilter.test(key))
				result.add(new KeyValuePair<String,String>(key, value));
		}

		if (result.isEmpty())
			return Collections.emptyList();

		return result;
	}

	@Override
	public String toString()
	{
		// Create toString by calling all getters programatically so we do not
		// have to maintain this method (which is for debugging only anyway):
		java.lang.reflect.Method[] methods = this.getClass().getMethods();
		String nameValues = Arrays
				.stream(methods)
				.filter(m -> m.getParameterCount() == 0
						&& m.getDeclaringClass() == this.getClass()
						&& NamesUtil.isGetterMethod(m.getName(), new String[] {
								"is", "get" })
						&& !m.getName().equals("toString"))
				.map(m -> {
					try {
						String name = m.getName();
						Object value = m.invoke(this);
						String stringValue;
						if (value instanceof String[])
							stringValue = "["
									+ Arrays.stream((String[]) value)
											.map(s -> '"' + s + '"')
											.collect(Collectors.joining(", "))
									+ "]";
						else if (value == null)
							stringValue = "null";
						else
							stringValue = value.toString();
						return name + "=" + stringValue;
					} catch (Exception e) {
						return name + "=<error " + e.getMessage() + ">";
					}
				}).collect(Collectors.joining(", "));

		return this.getClass().getSimpleName() + "(this=@"
				+ Integer.toHexString(System.identityHashCode(this)) + ", "
				+ nameValues + ")";
	}
}
