/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtest;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of special code related to a custom string template. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * Note that the generated output from these templates can compile but make very little/no sense.
 *
 * @author mmc
 */
public class TemplateCustomTest extends TemplateTestBase
{
	@Test
	public void testCustomTemplate() throws Exception
	{
		Output output = produceOutput(CustomTemplateInterface.class, configureAnnotationBuilder
				                                                     .add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.lang.Comparable<$(This)>"})
				                                                     .add(ConfigurationOptionKeys.customJavaTemplateFileName, "custom_template.stg").build());

		String[] searchStrings = { "class annotation", "import",
				                   "class javadoc", "getter javadoc", "mutable setter javadoc", "immutable setter javadoc", "equals javadoc", "hashCode javadoc", "toString javadoc", "compareTo javadoc",
				                   "before static members", "after static members",
				                   "before instance members", "after instance members",
				                   "before static methods", "after static methods",
				                   "before instance methods", "after instance methods",
				                   "member mutableObject annotation",
				                   "constructor annotation", "factory annotation",
				                   "equals annotation", "hashCode annotation", "toString annotation", "compareTo annotation", "getter getMutableObject annotation", "immutable setter setImmutableObject annotation", "mutable setter setMutableObject annotation",
				                   "constructor preamble", "factory preamble",
				                   "equals preamble", "hashCode preamble", "toString preamble", "compareTo preamble", "getter getMutableObject preamble", "immutable setter setImmutableObject preamble", "mutable setter setMutableObject preamble",
				                   "equals postamble", "hashCode postamble", "toString postamble", "compareTo postamble", "getter getMutableObject postamble", "immutable setter setImmutableObject postamble", "mutable setter setMutableObject postamble"
		};

		for (String searchString : searchStrings)
		  assertContainsWithWildcards("Inserted "+searchString+" stuff here.", output.code);
	}

	@Test
	public void testCustomTemplateReturnValues() throws Exception
	{
		Output output = produceOutput(CustomTemplateInterface.class, configureAnnotationBuilder
				                                                     .add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.lang.Comparable<$(This)>"})
				                                                     .add(ConfigurationOptionKeys.customJavaTemplateFileName, "custom_template.stg").build());

		String[] searchStrings = { "4242", "!(!", "\"dummyValue\"", "9999", "(null)", "(this)" };

		for (String searchString : searchStrings)
		  assertContainsWithWildcards(searchString, output.code);
	}

	@Test
	public void testCustomSerializableWithCustomMethodsAndNonFinalMethods() throws Exception
	{
		Output output = produceOutput(SerializableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.serialVersionUID, 1)
																   .add(ConfigurationOptionKeys.finalMethodsEnabled, false)
				                                                   .add(ConfigurationOptionKeys.customJavaTemplateFileName, "custom_serializable.stg")
				                                                   .build());

		assertContainsWithWildcards("public void validateObject() throws java.io.InvalidObjectException {", output.code);
		assertContainsWithWildcards("private Object readResolve() throws java.io.ObjectStreamException {", output.code);
		assertContainsWithWildcards("private void writeObject(final java.io.ObjectOutputStream out) throws java.io.IOException {", output.code);
		assertContainsWithWildcards("private Object writeReplace() throws java.io.ObjectStreamException {", output.code);
		assertContainsWithWildcards("private void readObject(final java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {", output.code);
		assertContainsWithWildcards("private void readObjectNoData() throws java.io.ObjectStreamException {", output.code);
	}

	@Test
	public void testCustomMethodsWithOverloads() throws Exception
	{
		Output output = produceOutput(OverloadedInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.serialVersionUID, 1)
				                                                   .add(ConfigurationOptionKeys.customJavaTemplateFileName, "custom_overload.stg").build());

		assertContainsWithWildcards("OverloadedInterface customMethod(final String stringValue, final int intValue) {", output.code);
		assertContainsWithWildcards("void customMethod(final int intValue) {", output.code);
		assertContainsWithWildcards("void customMethod(final String stringValue) {", output.code);
	}
}
