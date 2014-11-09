/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.lang.model.SourceVersion;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.annotations.internal.ThisReference;
import com.fortyoneconcepts.valjogen.model.Configuration;
import com.fortyoneconcepts.valjogen.model.ConfigurationDefaults;
import com.fortyoneconcepts.valjogen.model.ConfigurationMacros;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;

/**
 * Test configuration aspects
 */
public class ConfigurationTest
{
	private VALJOGenerate generate;
	private VALJOConfigure configure;
	private Map<String,String> configurationOptions;

	@Before
	public void init() throws URISyntaxException {
		generate = new AnnotationProxyBuilder<VALJOGenerate>(VALJOGenerate.class).build();
		configure = new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class).build();
		configurationOptions = new HashMap<String,String>();
	}

	/**
	 * Everthing can be configured by annotions or by options keys. Verify that there is a 1-1 relationships and with no misspellings.
	 *
	 * @throws NoSuchMethodException not expected in test.
	 * @throws SecurityException not expected in test.
	 */
	@Test
	public void checkConfigurationOptionKeysCorrespondToAnnotationMethods() throws NoSuchMethodException, SecurityException
	{
		Class<VALJOConfigure> configureAnnotation = VALJOConfigure.class;
		Class<VALJOGenerate> generateAnnotation = VALJOGenerate.class;

		Set<String> configureAnnotationMethodsString = concat(Arrays.stream(configureAnnotation.getMethods())
																	.filter(m -> m.getDeclaringClass().equals(configureAnnotation))
				                                                    .map(m -> m.getName()),
				                                              Arrays.stream(generateAnnotation.getMethods())
				                                              		.filter(m -> m.getDeclaringClass().equals(generateAnnotation))
				                                                    .map(m -> m.getName()))
				                                              .collect(toSet());

		for (Field f : ConfigurationOptionKeys.class.getFields())
		{
			String optionName = f.getName();

			boolean optionOnly = optionName.toUpperCase().endsWith(optionName); // All upper-case named options are not applicable as annotations.
			if (!optionOnly) {
				boolean found = (configureAnnotationMethodsString.contains(optionName));
				Assert.assertTrue("Option ConfigurationOptionKeys."+optionName+" must correspond with attribute method on either "+configureAnnotation.getSimpleName()+" or "+generateAnnotation.getSimpleName(), found);
			}
		}
	}

	/**
	 * Check all option constants has values identical to names.
	 *
	 * @throws Exception not expected for test.
	 */
	@Test
	public void checkConfigurationOptionKeysHasCorrectValues() throws Exception
	{
		Class<?> optionClass = ConfigurationOptionKeys.class;

		Field[] fields = optionClass.getFields();

		for (int i=0; i<fields.length; ++i)
		{
			Field f = fields[i];

			Assert.assertEquals("All field of "+optionClass.getName()+" should be static and public",  java.lang.reflect.Modifier.STATIC, f.getModifiers() & java.lang.reflect.Modifier.STATIC);

			String key = f.getName();
			String value = (String) f.get(null);

 		    Assert.assertEquals("Field names and values of "+optionClass.getName()+" should be equal", key, value);
		}
	}

	@Test
	public void testSystemPropertiesMacro() throws Exception
	{
		String sourceElementName = "test";

		List<Object> keys = Collections.list(System.getProperties().keys());
		for (Object _key : keys)
		{
			String key = (String)_key;

			String valueWithSystemPropertyMacro = "prop is '"+ConfigurationMacros.MacroPrefix+key+ConfigurationMacros.MacroSuffix+"'";

			configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.comment, valueWithSystemPropertyMacro);

			Configuration configuration = new Configuration(sourceElementName, SourceVersion.latest(), generate, configure, Locale.ENGLISH, configurationOptions);

			String actualExpandedValue = configuration.getComment();

			String expectedExpandedValue = ("prop is '"+System.getProperty(key)+"'");

			Assert.assertEquals(expectedExpandedValue, actualExpandedValue);
		}
	}

	@Test
	public void testBuildInMacros() throws Exception
	{
		String sourceElementName = "test";

		String[] macros = { ConfigurationMacros.NotApplicableMacro, ConfigurationMacros.GeneratedClassNameMacro, ConfigurationMacros.MasterInterfaceMacro, ConfigurationMacros.ExecutionDateMacro };

		for (int i=0; i<macros.length; ++i) {
			configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.comment, macros[i]);

			Configuration configuration = new Configuration(sourceElementName, SourceVersion.latest(), generate, configure, Locale.ENGLISH, configurationOptions);

			String[] values = { null, ThisReference.class.getName(), sourceElementName, String.format("%tFT%<tRZ", configuration.getExecutionDate()) };

			String actualExpandedValue = configuration.getComment();
			String expectedExpandedValue = values[i];

			Assert.assertEquals(expectedExpandedValue, actualExpandedValue);
		}
	}
}
