/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtest;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related to Serialization and Externalization. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateSerializationTest extends TemplateTestBase
{
	@Test
	public void testDirectSerializableHasUID() throws Exception
	{
		Output output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.io.Serializable"}).add(ConfigurationOptionKeys.serialVersionUID, 42L).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, Serializable", output.code);
		assertContainsWithWildcards("private static final long serialVersionUID = 42;", output.code);
	}

	@Test
	public void testIndirectSerializableHasUID() throws Exception
	{
		Output output = produceOutput(SerializableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.serialVersionUID, 43L).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements SerializableInterface {", output.code);
		assertContainsWithWildcards("private static final long serialVersionUID = 43;", output.code);
	}

	@Test
	public void testSerializableWithNoUIDHasNoUID() throws Exception
	{
		Output output = produceOutput(EkstraInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.extraInterfaceNames, new String[] {"java.io.Serializable"}).add(ConfigurationOptionKeys.serialVersionUID, ConfigurationDefaults.SerialVersionUID_NotSet).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements EkstraInterface, Serializable", output.code);
		assertNotContains("serialVersionUID", output.code);
	}

	@Test
	public void testExternalizableWithoutBaseClass() throws Exception
	{
		Output output = produceOutput(ExternalizableMutableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.serialVersionUID, 42L).build());
		assertContainsWithWildcards("class "+generatedClassName+" implements Externalizable", output.code);
		assertContainsWithWildcards("void writeExternal(*{*.write", output.code);
		assertContainsWithWildcards("void readExternal(*{*.read", output.code);
		assertNotContainsWithWildcards("super.writeExternal(", output.code);
		assertNotContainsWithWildcards("super.readExternal(", output.code);
	}

	@Test
	public void testExternalizableWithBaseClass() throws Exception
	{
		Output output = produceOutput(ExternalizableMutableInterfaceWithBaseClass.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.serialVersionUID, 42L)
																												   .add(ConfigurationOptionKeys.baseClazzName, ExternalizableBaseClass.class.getName()).build());
		assertContainsWithWildcards("class "+generatedClassName, output.code);
		assertContainsWithWildcards("void writeExternal(*{ super.writeExternal(*.write", output.code);
		assertContainsWithWildcards("void readExternal(*{ super.readExternal(*.read", output.code);
	}
}
