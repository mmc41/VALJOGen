/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

/**
 * Stubbed integration test of StringTemplate generated models and if special types are recognized correct.
 *
 * @author mmc
 */
public class ModelSpecialTypesTest extends TemplateTestBase
{

	@Test
	public void testModelForStringList() throws Exception
	{
		Output output = produceOutput(ContainsSpecialTypesInterface.class);

		Member stringListMember = output.clazz.tryGetMember("stringList");
		Assert.assertNotNull(stringListMember);
		Assert.assertTrue(stringListMember.getType().isObject());

		ObjectType stringListType = (ObjectType)stringListMember.getType();
		Assert.assertEquals("java.util.List<String>", stringListType.getPrototypicalName()); // hmm. should be shortened

		Assert.assertTrue(stringListType.isIterable());
		Assert.assertTrue(stringListType.isCollection());

		List<Type> stringListTypeTypeArguments = stringListType.getGenericTypeArguments();
		Assert.assertEquals(1, stringListTypeTypeArguments.size());
		ObjectType stringArgType = (ObjectType)stringListTypeTypeArguments.get(0);

		Assert.assertEquals("String", stringArgType.getName());

		System.out.println(stringListType);
	}

	@Test
	public void testModelForArrayList() throws Exception
	{
		Output output = produceOutput(ContainsSpecialTypesInterface.class);

		Member stringArrayMember = output.clazz.tryGetMember("stringArray");
		Assert.assertNotNull(stringArrayMember);
		Assert.assertTrue(stringArrayMember.getType().isArray());

		ArrayType stringArrayType = (ArrayType)stringArrayMember.getType();
		Assert.assertEquals("String[]", stringArrayType.getName());

		Assert.assertTrue(!stringArrayType.isIterable());
		Assert.assertTrue(!stringArrayType.isCollection());

		ObjectType stringComponentType = (ObjectType)stringArrayType.getArrayComponentType();

		Assert.assertEquals("String", stringComponentType.getName());
	}

	@Test
	public void testModelForComparable() throws Exception
	{
		Output output = produceOutput(ContainsSpecialTypesInterface.class);

		Member comparableMember = output.clazz.tryGetMember("comparable");
		Assert.assertNotNull(comparableMember);
		Assert.assertTrue(comparableMember.getType().isObject());

		ObjectType comparableType = (ObjectType)comparableMember.getType();
		Assert.assertEquals("Comparable<ContainsSpecialTypesInterface>", comparableType.getPrototypicalName());

		Assert.assertTrue(comparableType.isComparable());
		Assert.assertTrue(!comparableType.isIterable());
		Assert.assertTrue(!comparableType.isCollection());

		List<Type> comparableTypeArguments = comparableType.getGenericTypeArguments();
		Assert.assertEquals(1, comparableTypeArguments.size());
		ObjectType thisType = (ObjectType)comparableTypeArguments.get(0);

		Assert.assertEquals("ContainsSpecialTypesInterface", thisType.getName());
	}
}
