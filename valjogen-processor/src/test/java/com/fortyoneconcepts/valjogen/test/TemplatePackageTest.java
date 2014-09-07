/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;
import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to the package of the class. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplatePackageTest extends TemplateTestBase
{
	@Test
	public void testCorrectNonDefaultPackage() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("package "+generatedPackageName+";", output);
	}

	@Test
	public void testInterfacePackageByDefault() throws Exception
	{

		String output = produceOutput(ImmutableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.build());
		assertContainsWithWildcards("package "+ImmutableInterface.class.getPackage().getName()+";", output);
	}

	@Test
	public void testDefaultPackageWorksToo() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.outputPackage, "").build());
		assertNotContainsWithWildcards("package *;", output);
	}
}
