/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import org.junit.Assert;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/**
 * Low-level unit tests that names, packages etc. are computed correctly.
 *
 * @author mmc
 */
public class NamesUtilTest
{
	@Test
	public void testMakeSafeJavaIdentifier_Escaped()
	{
		String actual = NamesUtil.makeSafeJavaIdentifier("int");
		Assert.assertEquals("keyword is not allowed", "_int", actual);
	}

	@Test
	public void testMakeSafeJavaIdentifier_NotEscaped()
	{
		String actual = NamesUtil.makeSafeJavaIdentifier("x");
		Assert.assertEquals("non-keyword is allowed", "x", actual);
	}

	@Test
	public void testNameFromInterface_InterfaceFilteretOut()
	{
		String actual = NamesUtil.createNewClassNameFromInterfaceName("TestInterface");
		Assert.assertEquals("Class name not constructed correctly", "Test"+NamesUtil.ImplClassSuffix, actual);
	}

	@Test
	public void testNameFromInterface_IFilteretOut()
	{
		String actual = NamesUtil.createNewClassNameFromInterfaceName("ITest");
		Assert.assertEquals("Class name not constructed correctly", "Test"+NamesUtil.ImplClassSuffix, actual);
	}

	@Test
	public void testNameFromInterface_INotFilteretOut()
	{
		String actual = NamesUtil.createNewClassNameFromInterfaceName("ImmutableBla");
		Assert.assertEquals("Class name not constructed correctly", "ImmutableBla"+NamesUtil.ImplClassSuffix, actual);
	}

	@Test
	public void testNameFromInterface_Simple()
	{
		String actual = NamesUtil.createNewClassNameFromInterfaceName("Test");
		Assert.assertEquals("Class name not constructed correctly", "Test"+NamesUtil.ImplClassSuffix, actual);
	}

    @Test
	public void testPackageFromQualifiedName()
	{
		String actual = NamesUtil.getPackageFromQualifiedName("java.lang.Comparable");
		Assert.assertEquals("Class name not constructed correctly", "java.lang", actual);
	}

    @Test
	public void testPackageFromQualifiedGenericName()
	{
		String actual = NamesUtil.getPackageFromQualifiedName("java.lang.Comparable<java.lang.String>");
		Assert.assertEquals("Class name not constructed correctly", "java.lang", actual);
	}

	@Test
	public void testStripQualifiedName()
	{
		String actual = NamesUtil.stripGenericQualifier("java.lang.Comparable<java.lang.String>");
		Assert.assertEquals("Class name not constructed correctly", "java.lang.Comparable", actual);
	}

	@Test
	public void testIsQualifiedForQualifiedName()
	{
		boolean isQualified = NamesUtil.isQualified("java.lang.Comparable");
		Assert.assertTrue("Class name not constructed correctly", isQualified);
	}

	@Test
	public void testIsQualifiedForUnQualifiedName()
	{
		boolean isQualified = NamesUtil.isQualified("Comparable");
		Assert.assertFalse("Class name not constructed correctly", isQualified);
	}

	@Test
	public void testIsQualifiedForUnQualifiedGenericName()
	{
		boolean isQualified = NamesUtil.isQualified("Comparable<java.lang.String>");
		Assert.assertFalse("Class name not constructed correctly", isQualified);
	}

	@Test
	public void testEnsureQualifedNameAllreadyQualified()
	{
		String actual = NamesUtil.ensureQualifedName("java.lang.Comparable", "xx");
		Assert.assertEquals("Class name not constructed correctly", "java.lang.Comparable", actual);
	}

	@Test
	public void testEnsureQualifedNameNotQualified()
	{
		String actual = NamesUtil.ensureQualifedName("Comparable", "java.lang");
		Assert.assertEquals("Class name not constructed correctly", "java.lang.Comparable", actual);
	}

	@Test
	public void testUnqualifiedName()
	{
		String actual = NamesUtil.getUnqualifiedName("java.lang.Comparable");
		Assert.assertEquals("Class name not constructed correctly", "Comparable", actual);
	}

	@Test
	public void testUnqualifiedGenericName()
	{
		String actual = NamesUtil.getUnqualifiedName("java.lang.Comparable<java.lang.String>");
		Assert.assertEquals("Class name not constructed correctly", "Comparable<java.lang.String>", actual);
	}

	@Test
	public void testGetGenericQualifierNames()
	{
		String[] qualifiers = NamesUtil.getGenericQualifierNames("test.GenericInterface<java.lang.String,test.OtherInterface>");
		Assert.assertArrayEquals("Class name not constructed correctly", new String[] { "java.lang.String", "test.OtherInterface"}, qualifiers);
	}

	@Test
	public void testGetGenericQualifierNamesNOGenericType()
	{
		String[] qualifiers = NamesUtil.getGenericQualifierNames("test.GenericInterface");
		Assert.assertArrayEquals("Class name not constructed correctly", new String[] {}, qualifiers);
	}

	@Test
	public void testMatchingOverloadsNoArgs()
	{
		Assert.assertTrue(NamesUtil.matchingOverloads("test()", "test()", false));
	}

	@Test
	public void testMatchingOverloadsNoArgsOmitParenthesis()
	{
		Assert.assertTrue(NamesUtil.matchingOverloads("test", "test()", false));
	}

	@Test
	public void testMatchingOverloadsWithArgs()
	{
		Assert.assertTrue(NamesUtil.matchingOverloads("test(int,String)", "test(int,String)", false));
	}

	@Test
	public void testMatchingOverloadsWithJavaLangPrefix()
	{
		Assert.assertTrue(NamesUtil.matchingOverloads("test(String)", "test(java.lang.String)", false));
	}

	@Test
	public void testMatchingOverloadsWithWildcards()
	{
		Assert.assertTrue(NamesUtil.matchingOverloads("test(*,String)", "test(int,*)", false));
	}

	@Test
	public void testNotMatchingOverloadsWithMethodWildcards()
	{
		Assert.assertTrue(NamesUtil.matchingOverloads("*(*,*)", "test(int,String)", false));
	}

	@Test
	public void testNotMatchingOverloadsWithArgumentWildcards()
	{
		Assert.assertFalse(NamesUtil.matchingOverloads("test(int,Double)", "test(int,String)", false));
	}

	@Test
	public void testNotMatchingDifferentNumberArgsOverloads()
	{
		Assert.assertFalse(NamesUtil.matchingOverloads("*(*,*)", "test(int)", false));
	}

	@Test
	public void testNotMatchingDifferentNumberArgsOverloads2()
	{
		Assert.assertFalse(NamesUtil.matchingOverloads("*(*)", "test()", false));
	}
}
