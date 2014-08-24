package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;

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
		configurationOptions.put(ConfigurationOptionKeys.ensureNotNullEnabled, "true");
		String output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards(generatedClassName+"(final int intValue, final Object objectValue) { this.intValue=intValue; this.objectValue=Objects.requireNonNull(objectValue); }", output);
	}

	@Test
	public void testNotGuardedConstructor() throws Exception
	{
		configurationOptions.put(ConfigurationOptionKeys.ensureNotNullEnabled, "false");
		String output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards(generatedClassName+"(final int intValue, final Object objectValue) { this.intValue=intValue; this.objectValue=objectValue; }", output);
	}


	@Test
	public void testComparable() throws Exception
	{
		String output = produceOutput(ComparableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.comparableEnabled, true).build());

		System.out.println(output);
		// assertContainsWithWildcards("public int compareTo(ComparableInterface obj) { ", output);
	}

}
