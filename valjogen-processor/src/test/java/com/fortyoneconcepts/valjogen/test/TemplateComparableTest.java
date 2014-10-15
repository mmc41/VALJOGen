/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Test;
import org.junit.Assert;

import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to implementing Comparable for the class. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateComparableTest extends TemplateTestBase
{
	@Test
	public void testAutoComparable() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.add(ConfigurationOptionKeys.name, generatedPackageName+"."+generatedClassName).build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.comparableMembers, new String[] { }).build(), false, true);

		assertContainsWithWildcards("int compareTo(final ComparableInterface *) { ", output.code);
		assertContainsWithWildcards("if ((_result=Integer.compare(intValue, arg0.getIntValue()))!=0) return _result; if ((_result=stringValue.compareTo(arg0.getStringValue()))!=0) return _result;", output.code);

		Assert.assertTrue("Single warning about not all members being comparable expected", output.warnings.size()==1 && output.warnings.get(0).contains("not all members are comparable"));
	}

	@Test
	public void testSpecificComparable() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.comparableMembers, new String[] { "stringValue", "intValue" }) .build());

		assertContainsWithWildcards("int compareTo(final ComparableInterface *) { ", output.code);
		assertContainsWithWildcards("if ((_result=stringValue.compareTo(arg0.getStringValue()))!=0) return _result; if ((_result=Integer.compare(intValue, arg0.getIntValue()))!=0) return _result;", output.code);

		Assert.assertEquals("no warnings expected", 0, output.warnings.size());
	}

	@Test
	public void testIllegalSpecificComparable() throws Exception
	{
		Output output = produceOutput(ComparableInterface.class, generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.comparableMembers, new String[] { "unknownMemberName" }) .build(), true, false);

		Assert.assertTrue("Single error about unknown member expected", output.errors.size()==1 && output.errors.get(0).contains("Could not find member unknownMemberName"));
	}

	@Test
	public void testComparableThis() throws Exception
	{
		Output output = produceOutput(InterfaceWithoutAnnotation.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.lang.Comparable<$(This)>"}).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements InterfaceWithoutAnnotation, Comparable<"+generatedClassName+">", output.code);
		assertContainsWithWildcards("int compareTo(final TestImpl *) { ", output.code);
		assertContainsWithWildcards("if ((_result=Long.compare(baseValue, arg0.baseValue))!=0) return _result;", output.code);

		Assert.assertEquals("no warnings expected", 0, output.warnings.size());
	}

	@Test
	public void testNotComparableIfProvidedByBaseClass() throws Exception
	{
		Output output = produceOutput(InterfaceWithAbstractComparableBaseClass.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.baseClazzName, AbstractComparableBaseClass.class.getName()).build());
		assertNotContainsWithWildcards("int compareTo(", output.code); // No compare generated.
		assertNotContainsWithWildcards("abstract class "+generatedClassName, output.code); // No compare needed (so class is not abstract).
	}
}
