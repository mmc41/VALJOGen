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
		Output output = produceOutput(ImmutableInterface.class);
	    assertContains("final class "+generatedClassName, output.code);
	}

	@Test
	public void testImmutableAsNonAbstract() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class);
		assertNotContainsWithWildcards("abstract class "+generatedClassName, output.code);
	}

	@Test
	public void testUnknownNonPropertyMethodsAsAbstractClass() throws Exception
	{
		Output output = produceOutput(InterfaceWithNonPropertyMethods.class);
		assertContainsWithWildcards("abstract class "+generatedClassName, output.code);
	}

	@Test
	public void testPublicClassByDefault() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("public * class "+generatedClassName, output.code);
	}

	@Test
	public void testPrivateClassCanBeSet() throws Exception
	{
		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.clazzModifiers, "PRIVATE");
		Output output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("private *class "+generatedClassName, output.code);
	}

	@Test
	public void testGenerateClassNameWhenNotSet() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.build());
		assertContainsWithWildcards("class "+ImmutableInterface.class.getSimpleName().replace("Interface", "")+NamesUtil.ImplClassSuffix, output.code);
	}

	@Test
	public void testImplementsInterface() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class);
		assertContainsWithWildcards("class "+generatedClassName+" implements *"+ImmutableInterface.class.getSimpleName(), output.code);
	}

	@Test
	public void testImplementsGenericInterface() throws Exception
	{
		Output output = produceOutput(GenericInterface.class);
		assertContainsWithWildcards("class "+generatedClassName+" implements *GenericInterface<GT,ST,OT>", output.code);
	}

	@Test
	public void testExtraInterface() throws Exception
	{
		Output output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"com.fortyoneconcepts.valjogen.test.input.InterfaceWithoutAnnotation"}).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, InterfaceWithoutAnnotation", output.code);
	}

	@Test
	public void testExtraGenericInterface() throws Exception
	{
		Output output = produceOutput(InterfaceWithoutAnnotation.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.lang.Comparable<java.lang.String>"}).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements InterfaceWithoutAnnotation, Comparable<String>", output.code);
	}

	@Test
	public void testInterfaceWithBaseClass() throws Exception
	{
		Output output = produceOutput(InterfaceWithBaseClass.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.baseClazzName, ComparableBaseClass.class.getName()).build());
		assertContainsWithWildcards("class "+generatedClassName+" extends "+ComparableBaseClass.class.getSimpleName(), output.code);
	}

	@Test
	public void testClassAnntations() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder
				                                                .add(ConfigurationOptionKeys.clazzAnnotations,  new String[] { "@Generated", "@javax.annotation.Resource"})
				                                                .build());

		assertContainsWithWildcards("@Generated @javax.annotation.Resource public final class TestImpl", output.code);
	}

	@Test
	public void testClassJavaDoc() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder
				                                                .add(ConfigurationOptionKeys.clazzJavaDoc,  "class javaDoc here")
				                                                .build());

		assertContains("/** class javaDoc here */", output.code);
	}

	@Test
	public void testExtraGenericThisInterface() throws Exception
	{
		Output output = produceOutput(InterfaceWithoutAnnotation.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.lang.Comparable<$(This)>"}).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements InterfaceWithoutAnnotation, Comparable<"+generatedClassName+">", output.code);
	}

	@Test
	public void testDirectSerializableHasUID() throws Exception
	{
		Output output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.io.Serializable"}).add(ConfigurationOptionKeys.serialVersionUID, 42L).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, Serializable", output.code);
		assertContainsWithWildcards("private static final long serialVersionUID = 42;", output.code);
	}

	@Test
	public void testIndirectSerializableHasUID() throws Exception
	{
		Output output = produceOutput(SerializableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.serialVersionUID, 43L).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements SerializableInterface {", output.code);
		assertContainsWithWildcards("private static final long serialVersionUID = 43;", output.code);
	}

	@Test
	public void testSerializableWithNoUIDHasNoUID() throws Exception
	{
		Output output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.io.Serializable"}).add(ConfigurationOptionKeys.serialVersionUID, ConfigurationDefaults.SerialVersionUID_NotSet).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, Serializable", output.code);
		assertNotContains("serialVersionUID", output.code);
	}

	@Test
	public void testFileHeader() throws Exception
	{
		Output output = produceOutput(AnnotatedInterfaceWithHeader.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.headerFileName, "Header.txt").build());
		assertContainsWithWildcards(" This is a sample header for VALJOGen", output.code);
	}
}
