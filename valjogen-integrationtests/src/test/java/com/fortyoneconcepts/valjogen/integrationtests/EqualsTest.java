/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;
import com.fortyoneconcepts.valjogen.testsources.util.TestUtil;

import nl.jqno.equalsverifier.*;

public class EqualsTest
{
	@Test
	public void simpleClassEqualsAndHashContractTest() throws Throwable
	{
		Class<?> clazz = TestUtil.getTestClass(TestClassConstants.SimpleClass);
	    EqualsVerifier.forClass(clazz).verify();
	}

	@Test
	public void advancedlassEqualsAndHashContractTest() throws Throwable
	{
		Class<?> clazz = TestUtil.getTestClass(TestClassConstants.ComplexClass);
	    EqualsVerifier.forClass(clazz).verify();
	}

	@Test
	@SuppressWarnings("unchecked")
	public void withBaseClassEqualsAndHashContractTest() throws Throwable
	{
		@SuppressWarnings("rawtypes")
		Class clazz = TestUtil.getTestClass(TestClassConstants.SerializableWithBaseClass);
	    EqualsVerifier.forClass(clazz).withRedefinedSuperclass().verify();
	}
}
