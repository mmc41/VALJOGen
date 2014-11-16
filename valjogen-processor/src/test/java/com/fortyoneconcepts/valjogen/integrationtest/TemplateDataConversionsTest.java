/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtest;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.annotations.types.DataConversion;
import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TemplateTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration test of StringTemplate generation of code related conversion to/from JSON/XML etc. See {@link TemplateTestBase} for general
 * words about template tests.
 *
 * @author mmc
 */
public class TemplateDataConversionsTest extends TemplateTestBase
{
	@Test
	public void testJacksonAnnotationOnFactoryMethodOnly() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.dataConversion, DataConversion.JACKSON_DATABIND_ANNOTATIONS).add(ConfigurationOptionKeys.staticFactoryMethodEnabled, true).build());
		assertContainsWithWildcards("@JsonCreator public static "+generatedClassName+" valueOf(@JsonProperty(\"intValue\") * int intValue, @JsonProperty(\"objectValue\") * Object objectValue)", output.code);
		assertNotContains("@JsonCreator public "+generatedClassName+"(", output.code);
	}

	@Test
	public void testJacksonJDK8AnnotationOnFactoryMethodOnly() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.dataConversion, DataConversion.JACKSON_DATABIND_ANNOTATIONS_WITH_JDK8_PARAMETER_NAMES).add(ConfigurationOptionKeys.staticFactoryMethodEnabled, true).build());
		assertContainsWithWildcards("@JsonCreator public static "+generatedClassName+" valueOf(", output.code);
		assertNotContains("@JsonProperty", output.code);
		assertNotContains("@JsonCreator public "+generatedClassName+"(", output.code);
	}

	@Test
	public void testJacksonAnnoationsOnConstructor() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.dataConversion, DataConversion.JACKSON_DATABIND_ANNOTATIONS).add(ConfigurationOptionKeys.staticFactoryMethodEnabled, false).build());
		assertContainsWithWildcards("@JsonCreator public "+generatedClassName+"(@JsonProperty(\"intValue\") * int intValue, @JsonProperty(\"objectValue\") * Object objectValue)", output.code);
	}

	@Test
	public void testJacksonJDK8AnnoationsOnConstructor() throws Exception
	{
		Output output = produceOutput(ImmutableInterface.class, configureAnnotationBuilder.add(ConfigurationOptionKeys.dataConversion, DataConversion.JACKSON_DATABIND_ANNOTATIONS_WITH_JDK8_PARAMETER_NAMES).add(ConfigurationOptionKeys.staticFactoryMethodEnabled, false).build());
		assertContainsWithWildcards("@JsonCreator public "+generatedClassName+"(", output.code);
		assertNotContains("@JsonProperty", output.code);
	}
}
