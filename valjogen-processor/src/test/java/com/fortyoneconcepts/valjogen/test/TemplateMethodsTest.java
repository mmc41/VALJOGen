package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to the methods (incl. properties) inside the class. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateMethodsTest extends TemplateTestBase
{
	@Test
	public void testPublicGetter() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("public * int getIntValue() { return intValue; }", output);
	}

	@Test
	public void testPublicImmutableSetter() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class);

		assertContainsWithWildcards("public * ImmutableInterface setObjectValue(final Object objectValue) { return new TestImpl(this.intValue, objectValue); }", output);
	}

	@Test
	public void testPublicMutableSetter() throws Exception
	{
		configurationOptions.put(ConfigurationOptionKeys.ensureNotNullEnabled, "false");

		String output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("public * setObjectValue(final Object objectValue) { this.objectValue=objectValue; }", output);
//		assertContainsWithRegEx("public .*? void setObjectValue\\(final Object (objectValue|v)\\) \\{ this.objectValue=(objectValue|v); \\}", output);
	}

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
	public void testMutableGuardedSetter() throws Exception
	{
		configurationOptions.put(ConfigurationOptionKeys.ensureNotNullEnabled, "true");
		String output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("setObjectValue(final Object objectValue) { this.objectValue=Objects.requireNonNull(objectValue); }", output);
	}

	@Test
	public void testMutableNotGuardedSetter() throws Exception
	{
		configurationOptions.put(ConfigurationOptionKeys.ensureNotNullEnabled, "false");
		String output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("setObjectValue(final Object objectValue) { this.objectValue=objectValue; }", output);
	}
}
