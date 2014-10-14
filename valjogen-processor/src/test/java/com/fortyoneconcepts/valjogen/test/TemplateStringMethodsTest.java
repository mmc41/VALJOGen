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
 * Stubbed integration test of StringTemplate generation of code related to string telated methods. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateStringMethodsTest extends TemplateTestBase
{
	@Test
	public void testToStringIfEnabled() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.toStringEnabled, true).build(), false, true);

		assertContainsWithWildcards("String toString()", output.code);
	}

	@Test
	public void testNoEqualsIfDisabled() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.toStringEnabled, false).build(), false, true);

		assertNotContainsWithWildcards("String toString()", output.code);
	}
}
