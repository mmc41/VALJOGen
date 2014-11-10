package com.fortyoneconcepts.valjogen.integrationtests;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;
import com.fortyoneconcepts.valjogen.testsources.util.TestUtil;

import static com.fortyoneconcepts.valjogen.integrationtests.util.TestSupport.*;

public class JacksonTest {

	/***
	 * Test both that object can be converted to/from json with jackson.
	 *
	 * @throws Throwable
	 */
	@Test
	public void testSimpleClassCanConvertToFromJson() throws Throwable
	{
		Class<?> clazz = (Class<?>)TestUtil.getTestClass(TestClassConstants.SerializableClass);

		ObjectMapper mapper = new ObjectMapper();

		Object o = createInstanceUsingFactory(clazz);

		byte[] values = mapper.writeValueAsBytes(o);

		Object oBack = mapper.readValue(values, clazz);

		Assert.assertTrue(compareInstanceFields(o,oBack));
	}

	/***
	 * Test both that object can be converted to/from json with jackson.
	 *
	 * @throws Throwable
	 */
	@Test
	public void testComplexClassCanConvertToFromJson() throws Throwable
	{
		Class<?> clazz = (Class<?>)TestUtil.getTestClass(TestClassConstants.SerializableWithBaseClass);

		ObjectMapper mapper = new ObjectMapper();

		Object o = createInstanceUsingFactory(clazz);

		byte[] values = mapper.writeValueAsBytes(o);

		Object oBack = mapper.readValue(values, clazz);

		Assert.assertTrue(compareInstanceFields(o,oBack));
	}
}
