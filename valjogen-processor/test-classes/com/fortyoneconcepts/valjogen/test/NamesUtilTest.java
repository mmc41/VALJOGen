package com.fortyoneconcepts.valjogen.test;

import org.junit.Assert;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.processor.NamesUtil;

public class NamesUtilTest
{
	// @Test
	public void testMakeSafeJavaIdentifier_Escaped()
	{
		String actual = NamesUtil.makeSafeJavaIdentifier("int");
		Assert.assertEquals("keyword is not allowed", "_int", actual);
	}

	// @Test
	public void testMakeSafeJavaIdentifier_NotEscaped()
	{
		String actual = NamesUtil.makeSafeJavaIdentifier("x");
		Assert.assertEquals("non-keyword is allowed", "x", actual);
	}
}
