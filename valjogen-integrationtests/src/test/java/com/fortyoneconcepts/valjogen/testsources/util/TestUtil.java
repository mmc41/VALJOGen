package com.fortyoneconcepts.valjogen.testsources.util;

public class TestUtil
{
	public static Class<?> getTestClass(String name) throws ClassNotFoundException
	{
		return TestUtil.class.getClassLoader().loadClass(name);
	}
}
