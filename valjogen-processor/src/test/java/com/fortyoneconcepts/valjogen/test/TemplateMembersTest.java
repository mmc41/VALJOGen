/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to the data members inside the class. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateMembersTest extends TemplateTestBase
{
	@Test
	public void testImmutableHasPrivateFinalMembers() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("private final int intValue;", output.code);
	}

	@Test
	public void testAbstractHasProtectedFinalMembers() throws Exception
	{
		Output output = produceOutput(InterfaceWithNonPropertyMethods.class);
		assertContainsWithWildcards("protected final int intValue;", output.code);
	}

}
