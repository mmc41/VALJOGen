package com.fortyoneconcepts.valjogen.test;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;
import static java.util.stream.Stream.concat;

import org.junit.Assert;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;

/**
 * Everthing can be configured by annotions or by options keys. Verify that there is a 1-1 relationships and with no misspellings.
 */
public class ConfigurationOptionKeysValidateTest
{
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

	@Test
	public void checkConfigurationOptionKeysHasCorrectValues() throws NoSuchMethodException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		Class<?> optionClass = ConfigurationOptionKeys.class;

		Field[] fields = optionClass.getFields();

		for (int i=0; i<fields.length; ++i)
		{
			Field f = fields[i];

			Assert.assertEquals("All field of "+optionClass.getName()+" should be static and public", Modifier.STATIC, f.getModifiers() & Modifier.STATIC);

			String key = f.getName();
			String value = (String) f.get(null);

 		    Assert.assertEquals("Field names and values of "+optionClass.getName()+" should be equal", key, value);
		}
	}
}
