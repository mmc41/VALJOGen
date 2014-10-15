/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.ConfigurationDefaults;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase.Output;

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
	public void testCallsBaseClassConstructor() throws Exception
	{
		Output output = produceOutput(InterfaceWithAbstractComparableBaseClass.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.baseClazzName, AbstractComparableBaseClass.class.getName()).build());
		assertContainsWithWildcards("class "+generatedClassName+" extends "+AbstractComparableBaseClass.class.getSimpleName(), output.code);
	}

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
	public void testStaticFactory() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.staticFactoryMethodEnabled, "true");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("public static "+generatedClassName+" valueOf(", output.code);
		assertContainsWithWildcards("private "+generatedClassName+"(", output.code);
	}

	@Test
	public void testNoStaticFactory() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.staticFactoryMethodEnabled, "false");
		Output output = produceOutput(MutableInterface.class);

		assertNotContainsWithWildcards(generatedClassName+" valueOf(", output.code);
		assertContainsWithWildcards("public "+generatedClassName+"(", output.code);
	}

	@Test
	public void testInHeritedJavaDoc() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.insertInheritDocOnMethodsEnabled, "true");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("{@inheritDoc}*public *int getIntValue()", output.code);
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

	@Test
	public void testConstructorAnntations() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder
				                                                .add(ConfigurationOptionKeys.constructorAnnotations,  new String[] { "@Generated", "@javax.annotation.Resource"})
				                                                .build());

		assertContainsWithWildcards("@Generated @javax.annotation.Resource private TestImpl", output.code);
	}

	@Test
	public void testFactoryMethodAnntations() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder
				                                                .add(ConfigurationOptionKeys.factoryMethodAnnotations,  new String[] { "@Generated", "@javax.annotation.Resource"})
				                                                .build());

		assertContainsWithWildcards("@Generated @javax.annotation.Resource public static TestImpl valueOf", output.code);
	}
}
