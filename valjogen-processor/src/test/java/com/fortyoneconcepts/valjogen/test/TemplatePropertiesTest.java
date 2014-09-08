/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.ConfigurationDefaults;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to the properties (methods) inside the class. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplatePropertiesTest extends TemplateTestBase
{
	@Test
	public void testPublicGetter() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("public * int getIntValue() { return intValue; }", output.code);
	}

	@Test
	public void testPublicImmutableSetter() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class);

		assertContainsWithWildcards("public * ImmutableInterface setObjectValue(final Object objectValue) { return new TestImpl(this.intValue, objectValue); }", output.code);
	}

	@Test
	public void testCustomSetterAndGetter() throws Exception
	{
		Output output = produceOutput(CustomPropertiesInterface.class,
				                      generateAnnotationBuilder.build(),
				                      configureAnnotationBuilder.add(ConfigurationOptionKeys.getterPrefixes, new String[] { "should"})
				                      .add(ConfigurationOptionKeys.setterPrefixes, new String[] { "with"})
				                      .build());

		assertContainsWithWildcards("boolean shouldRequire() { return require; }", output.code);
		assertContainsWithWildcards("CustomPropertiesInterface withRequire(final boolean require) { return new CustomPropertiesImpl(require); }", output.code);
	}

	@Test
	public void testPublicMutableSetter() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "false");

		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("public * setObjectValue(final Object objectValue) { this.objectValue=objectValue; }", output.code);
	}

	@Test
	public void testMutableGuardedSetter() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "true");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("setObjectValue(final Object objectValue) { this.objectValue=Objects.requireNonNull(objectValue); }", output.code);
	}

	@Test
	public void testMutableNotGuardedSetter() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "false");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("setObjectValue(final Object objectValue) { this.objectValue=objectValue; }", output.code);
	}
}
