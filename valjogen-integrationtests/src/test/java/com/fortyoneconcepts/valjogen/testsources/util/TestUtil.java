/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.testsources.util;

public class TestUtil
{
	public static Class<?> getTestClass(String name) throws ClassNotFoundException
	{
		return TestUtil.class.getClassLoader().loadClass(name);
	}
}
