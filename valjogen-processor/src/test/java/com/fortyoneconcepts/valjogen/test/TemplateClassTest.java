/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to the overall class itself (not the methods/members/properties inside). See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateClassTest extends TemplateTestBase
{
	@Test
	public void testImmutableAsFinal() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class);
	    assertContains("final class "+generatedClassName, output);
	}

	@Test
	public void testImmutableAsNonAbstract() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class);
		assertNotContainsWithWildcards("abstract class "+generatedClassName, output);
	}

	@Test
	public void testUnknownNonPropertyMethodsAsAbstractClass() throws Exception
	{
		String output = produceOutput(InterfaceWithNonPropertyMethods.class);
		assertContainsWithWildcards("abstract class "+generatedClassName, output);
	}

	@Test
	public void testPublicClassByDefault() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("public * class "+generatedClassName, output);
	}

	@Test
	public void testPrivateClassCanBeSet() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.clazzScope, "private");
		String output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("private * class "+generatedClassName, output);
	}

	@Test
	public void testGenerateClassNameWhenNotSet() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.build());
		assertContainsWithWildcards("class "+ImmutableInterface.class.getSimpleName().replace("Interface", "")+NamesUtil.ImplClassSuffix, output);
	}

	@Test
	public void testImplementsInterface() throws Exception
	{
		String output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("class "+generatedClassName+" implements *"+ImmutableInterface.class.getSimpleName(), output);
	}

	@Test
	public void testImplementsGenericInterface() throws Exception
	{
		String output = produceOutput(GenericInterface.class);
		assertContainsWithWildcards("class "+generatedClassName+" implements *GenericInterface<GT,ST,OT>", output);
	}

	@Test
	public void testExtraInterface() throws Exception
	{
		String output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"com.fortyoneconcepts.valjogen.test.input.InterfaceWithoutAnnotation"}).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, InterfaceWithoutAnnotation", output);
	}

	@Test
	public void testExtraGenericInterface() throws Exception
	{
		String output = produceOutput(InterfaceWithoutAnnotation.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.lang.Comparable<java.lang.String>"}).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements InterfaceWithoutAnnotation, Comparable<String>", output);
	}


	@Test
	public void testExtraGenericThisInterface() throws Exception
	{
		String output = produceOutput(InterfaceWithoutAnnotation.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.lang.Comparable<$(This)>"}).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements InterfaceWithoutAnnotation, Comparable<"+generatedClassName+">", output);
	}

	@Test
	public void testDirectSerializableHasUID() throws Exception
	{
		String output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.io.Serializable"}).add(ConfigurationOptionKeys.serialVersionUID, 42L).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, Serializable", output);
		assertContainsWithWildcards("private static final long serialVersionUID = 42;", output);
	}

	@Test
	public void testIndirectSerializableHasUID() throws Exception
	{
		String output = produceOutput(SerializableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.serialVersionUID, 43L).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements SerializableInterface {", output);
		assertContainsWithWildcards("private static final long serialVersionUID = 43;", output);
	}

	@Test
	public void testSerializableWithNoUIDHasNoUID() throws Exception
	{
		String output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.io.Serializable"}).add(ConfigurationOptionKeys.serialVersionUID, ConfigurationDefaults.SerialVersionUID_NotSet).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, Serializable", output);
		assertNotContains("serialVersionUID", output);
	}

	@Test
	public void testFileHeader() throws Exception
	{
		String output = produceOutput(AnnotatedInterfaceWithHeader.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.headerFileName, "Header.txt").build());
		assertContainsWithWildcards(" This is a sample header for VALJOGen", output);
	}
}
