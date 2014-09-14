/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests;

import java.lang.reflect.*;

import org.junit.Assert;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;
import com.fortyoneconcepts.valjogen.testsources.util.TestUtil;


public class SerializationTest
{
	/***
	 * Test both that serialization ID is added and that default package configuration works.
	 *
	 * @throws Throwable
	 */
	@Test
	public void testCorrectSerializationIDAsInheritedFromPackageConfiguration() throws Throwable
	{
		Class<?> clazz = TestUtil.getTestClass(TestClassConstants.SerializableClass);

		Field serialVersionUIDField = clazz.getDeclaredField("serialVersionUID");
		serialVersionUIDField.setAccessible(true);

		long id= serialVersionUIDField.getLong(null);
		Assert.assertEquals("ID specified in package's @VALJOConfigure expected", 42, id);
	}

	// TODO: Add test that the instance can actually serialize
}
