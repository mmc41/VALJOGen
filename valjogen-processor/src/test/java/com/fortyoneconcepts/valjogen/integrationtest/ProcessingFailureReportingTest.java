/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.processor.ProcessorMessages;
import com.fortyoneconcepts.valjogen.test.util.CompilationTestBase;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration tests that checks that processing errors in dynamically created test classes are reported or ignored as configured.
 *
 * @author mmc
 */
public class ProcessingFailureReportingTest extends CompilationTestBase
{
	private String buildAnnotatedInterfaceSource(String interfaceName, String body, String configureString)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("import "+VALJOGenerate.class.getPackage().getName()+".*; "+System.lineSeparator());
		sb.append("@"+VALJOGenerate.class.getSimpleName()+System.lineSeparator());
		sb.append("@"+VALJOConfigure.class.getSimpleName()+"("+configureString+")"+System.lineSeparator());
		sb.append("public interface "+interfaceName+" {"+System.lineSeparator());
		sb.append(body+System.lineSeparator());
		sb.append("}"+System.lineSeparator());

		return sb.toString();
	}

	@Test
	public void testUnknownBaseClass() throws URISyntaxException, IOException
	{
		String unknownBaseClass = "unknownBaseClass";
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue();", ConfigurationOptionKeys.baseClazzName+"=\""+unknownBaseClass+"\"");
		Map<String,String> options = getOptions("ITest");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.ClassNotFound, unknownBaseClass), options);
	}

	@Test
	public void testUnknownImportClass() throws URISyntaxException, IOException
	{
		String unknownImportClass = "unknownImportClass";
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue();", ConfigurationOptionKeys.importClasses+"={\""+unknownImportClass+"\"}");
		Map<String,String> options = getOptions("ITest");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.ImportTypeNotFound, unknownImportClass), options);
	}

	@Test
	public void testMalformedSetterDueToExtraArgumentNotIgnored() throws URISyntaxException, IOException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public void setIntValue(int value, int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=false");
		Map<String,String> options = getOptions("ITest");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.MalFormedSetter, "setIntValue(int,int)"), options);
	}

	@Test
	public void testMalformedSetterDueToReturnValueNotIgnored() throws URISyntaxException, IOException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public Object setIntValue(int value);", ConfigurationOptionKeys.ignoreMalformedProperties+"=false");
		Map<String,String> options = getOptions("ITest");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.MalFormedSetter, "setIntValue(int)"), options);
	}

	@Test
	public void testMalformedSetterDueToWrongTypeNotIgnored() throws URISyntaxException, IOException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue(); public void setIntValue(double value);", ConfigurationOptionKeys.ignoreMalformedProperties+"=false");
		Map<String,String> options = getOptions("ITest");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.InconsistentProperty, "getIntValue, intValue"), options);
	}

	@Test
	public void testMalformedGetterNotIgnored() throws URISyntaxException, IOException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue(int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=false");
		Map<String,String> options = getOptions("ITest");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.MalFormedGetter, "getIntValue(int)"), options);
	}

	@Test
	public void testMalformedSetterIgnored() throws URISyntaxException, IOException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public void setIntValue(int value, int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=true");
		Map<String,String> options = getOptions("ITest");
		assertCompileSuccess("ITest", source, options);
	}

	@Test
	public void testMalformedGetterIgnored() throws URISyntaxException, IOException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue(int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=true");
		Map<String,String> options = getOptions("ITest");
		assertCompileSuccess("ITest", source, options);
	}
}
