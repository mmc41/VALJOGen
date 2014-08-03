/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests;

import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.testsources.util.TestClassConstants;

import nl.jqno.equalsverifier.*;

public class EqualsTest
{
	private static Class<?> getTestClass(String name) throws ClassNotFoundException
	{
		return EqualsTest.class.getClassLoader().loadClass(name);
	}

	@Test
	public void simpleClassEqualsAndHashContractTest() throws Throwable
	{
		Class<?> clazz = getTestClass(TestClassConstants.SimpleClass);
	    EqualsVerifier.forClass(clazz).verify();
	}

	@Test
	@Ignore("Ignored due to bug in Equalsverifier")
	public void advancedlassEqualsAndHashContractTest() throws Throwable
	{
		Class<?> clazz = getTestClass(TestClassConstants.ComplexClass);
	    EqualsVerifier.forClass(clazz).verify();
	}
}
