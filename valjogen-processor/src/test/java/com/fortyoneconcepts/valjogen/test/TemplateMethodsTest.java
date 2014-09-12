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
 * Stubbed integration test of StringTemplate generation of code related to the methods (excl. properties) inside the class. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateMethodsTest extends TemplateTestBase
{
	@Test
	public void testGuardedConstructor() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "true");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards(generatedClassName+"(final int intValue, final Object objectValue) { this.intValue=intValue; this.objectValue=Objects.requireNonNull(objectValue); }", output.code);
	}

	@Test
	public void testNotGuardedConstructor() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "false");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards(generatedClassName+"(final int intValue, final Object objectValue) { this.intValue=intValue; this.objectValue=objectValue; }", output.code);
	}

	@Test
	public void testInHeritedJavaDoc() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.insertInheritDocOnMethodsEnabled, "true");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("{@inheritDoc}*public final int getIntValue()", output.code);
	}

	@Test
	public void testNoInHeritedJavaDoc() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.insertInheritDocOnMethodsEnabled, "false");
		Output output = produceOutput(MutableInterface.class);

		assertNotContainsWithWildcards("{@inheritDoc}*public final int getIntValue()", output.code);
	}

	@Test
	public void testDefaultMethodIgnored() throws Exception
	{
		Output output = produceOutput(InterfaceWithDefaultMethod.class);

		assertNotContainsWithWildcards("getDefMethod", output.code);
	}
}
