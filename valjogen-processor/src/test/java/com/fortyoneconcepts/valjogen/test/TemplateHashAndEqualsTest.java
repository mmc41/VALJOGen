/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to the hash and equlas methods. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateHashAndEqualsTest extends TemplateTestBase
{
	@Test
	public void testEqualsIfEnabled() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.equalsEnabled, true).build(), false, true);

		assertContainsWithWildcards("boolean equals(*Object*)", output.code);
	}

	@Test
	public void testNoEqualsIfDisabled() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.equalsEnabled, false).build(), false, true);

		assertNotContainsWithWildcards("boolean equals(*Object*)", output.code);
	}

	@Test
	public void testHashIfEnabled() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.hashEnabled, true).build(), false, true);

		assertContainsWithWildcards("int hashCode()", output.code);
	}

	@Test
	public void testHashIfDisabled() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.hashEnabled, false).build(), false, true);

		assertNotContainsWithWildcards("int hashCode()", output.code);
	}

}
