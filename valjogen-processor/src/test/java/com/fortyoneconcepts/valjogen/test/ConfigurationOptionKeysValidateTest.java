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
			boolean found = (configureAnnotationMethodsString.contains(optionName));
			Assert.assertTrue("Option ConfigurationOptionKeys."+optionName+" must correspond with attribute method on either "+configureAnnotation.getSimpleName()+" or "+generateAnnotation.getSimpleName(), found);
		}
	}
}
