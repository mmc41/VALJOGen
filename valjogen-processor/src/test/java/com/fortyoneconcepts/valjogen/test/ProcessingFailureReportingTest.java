package com.fortyoneconcepts.valjogen.test;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.processor.ProcessorMessages;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration tests that checks that processing errors in dynamically created test classes are reported or ignored as configured.
 *
 * @author mmc
 */
public class ProcessingFailureReportingTest
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
	public void testUnknownBaseClass() throws FileNotFoundException, MalformedURLException, URISyntaxException
	{
		String unknownBaseClass = "unknownBaseClass";
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue();", ConfigurationOptionKeys.baseClazzName+"=\""+unknownBaseClass+"\"");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.BaseClassNotFound, unknownBaseClass));
	}

	@Test
	public void testUnknownImportClass() throws FileNotFoundException, MalformedURLException, URISyntaxException
	{
		String unknownImportClass = "unknownImportClass";
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue();", ConfigurationOptionKeys.importClasses+"={\""+unknownImportClass+"\"}");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.ImportTypeNotFound, unknownImportClass));
	}

	@Test
	public void testMalformedSetterDueToExtraArgumentNotIgnored() throws FileNotFoundException, MalformedURLException, URISyntaxException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public void setIntValue(int value, int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=false");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.MalFormedSetter, "setIntValue(int,int)"));
	}

	@Test
	public void testMalformedSetterDueToReturnValueNotIgnored() throws FileNotFoundException, MalformedURLException, URISyntaxException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public Object setIntValue(int value);", ConfigurationOptionKeys.ignoreMalformedProperties+"=false");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.MalFormedSetter, "setIntValue(int)"));
	}

	@Test
	public void testMalformedGetterNotIgnored() throws FileNotFoundException, MalformedURLException, URISyntaxException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue(int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=false");
		assertCompileFailure("ITest", source, String.format(ProcessorMessages.MalFormedGetter, "getIntValue(int)"));
	}

	@Test
	public void testMalformedSetterIgnored() throws FileNotFoundException, MalformedURLException, URISyntaxException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public void setIntValue(int value, int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=true");
		assertCompileSuccess("ITest", source);
	}

	@Test
	public void testMalformedGetterIgnored() throws FileNotFoundException, MalformedURLException, URISyntaxException
	{
		String source = buildAnnotatedInterfaceSource("ITest", "public int getIntValue(int other);", ConfigurationOptionKeys.ignoreMalformedProperties+"=true");
		assertCompileSuccess("ITest", source);
	}
}
