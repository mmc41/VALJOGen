package com.fortyoneconcepts.valjogen.test;

import org.junit.Assert;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/**
 * Low-level unit tests that names, packages etc. are computed correctly.
 *
 * TODO: Add more tests.
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
}
