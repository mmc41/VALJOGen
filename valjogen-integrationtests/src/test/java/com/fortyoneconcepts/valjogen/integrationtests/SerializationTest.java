/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests;

import java.io.Serializable;
import java.lang.reflect.*;

import org.junit.Assert;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;
import com.fortyoneconcepts.valjogen.testsources.util.TestUtil;

import static com.fortyoneconcepts.valjogen.integrationtests.util.TestSupport.*;
import static com.fortyoneconcepts.valjogen.integrationtests.util.SerializationUtil.*;

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

	/***
	 * Test both that serialization ID is added and that default package configuration works.
	 *
	 * @throws Throwable
	 */
	@Test
	public void testSimpleClassCanSerialize() throws Throwable
	{
		@SuppressWarnings("unchecked")
		Class<Serializable> clazz = (Class<Serializable>)TestUtil.getTestClass(TestClassConstants.SerializableClass);

		Serializable o = createInstanceUsingFactory(clazz);

		byte[] serializedData = write(o);

		Serializable deserialized = read(serializedData);

		Assert.assertTrue(compareInstanceFields(o,deserialized));
	}

	/***
	 * Test both that serialization ID is added and that default package configuration works.
	 *
	 * @throws Throwable
	 */
	@Test
	public void testClassWithBaseClassCanSerialize() throws Throwable
	{
		@SuppressWarnings("unchecked")
		Class<Serializable> clazz = (Class<Serializable>)TestUtil.getTestClass(TestClassConstants.SerializableWithBaseClass);

		Serializable o = createInstanceUsingFactory(clazz);

		byte[] serializedData = write(o);

		Serializable deserialized = read(serializedData);

		Assert.assertTrue(compareInstanceFields(o,deserialized));
	}

	/***
	 * Test both that externalization with base classes work.
	 *
	 * @throws Throwable
	 */
	@Test
	public void testExternalizableClassWithBaseClassCanSerialize() throws Throwable
	{
		@SuppressWarnings("unchecked")
		Class<Serializable> clazz = (Class<Serializable>)TestUtil.getTestClass(TestClassConstants.ExternalizableMutableWithBaseClass);

		Serializable o = createInstanceUsingFactory(clazz);

		byte[] serializedData = write(o);

		Serializable deserialized = read(serializedData);

		Assert.assertTrue(compareInstanceFields(o,deserialized));
	}
}
