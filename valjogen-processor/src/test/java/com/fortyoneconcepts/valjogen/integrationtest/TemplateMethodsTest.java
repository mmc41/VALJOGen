/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtest;

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
	public void testCallsAllBaseClassConstructorsByDefault() throws Exception
	{
		Output output = produceOutput(InterfaceWithComparableHashEqualsBaseClass.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.baseClazzName, ComparableBaseClassWithHashAndEquals.class.getName()).build());

		assertContainsWithWildcards("TestImpl(final int myValue, final *List<Double> myDoubleList) { super();", output.code);
		assertContainsWithWildcards("TestImpl(final int baseIntField, final String baseStrField, final int myValue, final *List<Double> myDoubleList) { super(baseIntField, baseStrField);", output.code);
	}

	@Test
	public void testCallsoOnlySelectedBaseClassConstructor1() throws Exception
	{
		Output output = produceOutput(InterfaceWithComparableHashEqualsBaseClass.class, configureAnnotationBuilder
				                                                              .add(ConfigurationOptionKeys.baseClazzName, ComparableBaseClassWithHashAndEquals.class.getName())
				                                                              .add(ConfigurationOptionKeys.baseClazzConstructors, new String[] { "()" })
				                                                              .build());

		assertContainsWithWildcards("TestImpl(final int myValue, final *List<Double> myDoubleList) { super();", output.code);
		assertNotContainsWithWildcards("TestImpl(final int baseIntField, final String baseStrField, final int myValue, final *List<Double> myDoubleList) { super(baseIntField, baseStrField);", output.code);
	}

	@Test
	public void testCallsoOnlySelectedBaseClassConstructor2() throws Exception
	{
		Output output = produceOutput(InterfaceWithComparableHashEqualsBaseClass.class, configureAnnotationBuilder
				                                                              .add(ConfigurationOptionKeys.baseClazzName, ComparableBaseClassWithHashAndEquals.class.getName())
				                                                              .add(ConfigurationOptionKeys.baseClazzConstructors, new String[] { "(int, String)" })
				                                                              .build());

		assertNotContainsWithWildcards("TestImpl(final int myValue, final *List<Double> myDoubleList) { super();", output.code);
		assertContainsWithWildcards("TestImpl(final int baseIntField, final String baseStrField, final int myValue, final *List<Double> myDoubleList) { super(baseIntField, baseStrField);", output.code);
	}

	@Test
	public void testDefaultConstructorForCompletelyMutableClass() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "false");
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.staticFactoryMethodEnabled, "false");

		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("public TestImpl() {", output.code);
	}

	@Test
	public void testNoArgsFactoryMethodForCompletelyMutableClass() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "false");
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.staticFactoryMethodEnabled, "true");

		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards("public static TestImpl valueOf() {", output.code);
	}

	@Test
	public void testGuardedConstructor() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "true");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards(generatedClassName+"(final int intValue, final Object objectValue) { *this.intValue=intValue; this.objectValue=Objects.requireNonNull(objectValue); }", output.code);
	}

	@Test
	public void testNotGuardedConstructor() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.ensureNotNullEnabled, "false");
		Output output = produceOutput(MutableInterface.class);

		assertContainsWithWildcards(generatedClassName+"(final int intValue, final Object objectValue) { *this.intValue=intValue; this.objectValue=objectValue; }", output.code);
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
				                                                .add(ConfigurationOptionKeys.methodAnnotations,  new String[] { "(**)=@Generated", "(**)=@javax.annotation.Resource"})
				                                                .build());

		assertContainsWithWildcards("@Generated @javax.annotation.Resource * TestImpl", output.code);
	}

	@Test
	public void testFactoryMethodAnntations() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder
				                                                .add(ConfigurationOptionKeys.methodAnnotations,  new String[] { "valueOf(**)=@Generated", "valueOf(**)=@javax.annotation.Resource"})
				                                                .build());

		assertContainsWithWildcards("@Generated @javax.annotation.Resource * static TestImpl valueOf", output.code);
	}

	@Test
	public void testMethodAnntations() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder
				                                                .add(ConfigurationOptionKeys.methodAnnotations,  new String[] { "hashCode()=@Generated"})
				                                                .build());

		assertContainsWithWildcards("@Generated @Override public int hashCode()", output.code);
	}

	@Test
	public void testPropertyAnntations() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder
				                                                .add(ConfigurationOptionKeys.methodAnnotations,  new String[] { "getIntValue()=@Generated"})
				                                                .build());

		assertContainsWithWildcards("@Generated @Override public int getIntValue()", output.code);
	}
}
