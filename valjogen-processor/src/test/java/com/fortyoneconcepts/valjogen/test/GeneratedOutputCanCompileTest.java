/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;
import com.fortyoneconcepts.valjogen.model.Configuration;
import com.fortyoneconcepts.valjogen.model.ConfigurationDefaults;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.test.input.*;
import com.fortyoneconcepts.valjogen.test.util.TestSupport;
import com.google.testing.compile.*;

import static com.fortyoneconcepts.valjogen.test.util.TestSupport.*;

/**
 * Stubbed integration tests that checks that all test input classes and their
 * generated classes can compile with an annotation processor.
 *
 * This is done by searching the class path for all classes in our test source
 * package and then using letting Junit instantiate a new instance of this test
 * class and run the test for all test source class files.
 *
 * Unfortunately, we are currenly forced to use a google's Truth framework below
 * instead of a JUnit assert like all the other tests:
 *
 * Nb. Unlike other runtime tests, this test requires source interfaces to be
 * annotated!
 *
 * @author mmc
 */
@RunWith(Parameterized.class)
public class GeneratedOutputCanCompileTest
{
	private static final String packageNameForTests = SimpleAnnotatedInterface.class.getPackage().getName();

	@Parameters(name = "{index} - {0}")
	public static Collection<Object[]> data() throws Throwable {
		Stream<String> classNames = getClassNames(GeneratedOutputCanCompileTest.class.getClassLoader(), packageNameForTests);
		return classNames.map(className -> new Object[] { className }).collect(Collectors.toList());
	}

	private final String className;

	public GeneratedOutputCanCompileTest(String className) {
		this.className = className;
	}

	@Test // // Nb. Must be executed from test class - can not be run individually.
	public void testInterface() throws URISyntaxException, IOException
	{
		// Create a confogiration used for reading the source path - not used for anything else.
		VALJOGenerate generate = new AnnotationProxyBuilder<VALJOGenerate>(VALJOGenerate.class).build();
		VALJOConfigure configure = new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class).build();
		Configuration configuration = new Configuration(null, generate, configure, Locale.ENGLISH, new HashMap<String,String>());

		// Setup url to source dir.
		Path targetPath = TestSupport.getClassPath();
		Path path = targetPath.resolve(configuration.getSourcePathOrDefault());
		path = path.resolve(className.replace(".", "/")+".java");
		path = path.toAbsolutePath().normalize();
		URL url = path.toUri().toURL();
		if (url==null)
			throw new FileNotFoundException(path.toString());

		Map<String,String> configurationOptions = new HashMap<String,String>();

		// Setup a default of a different log file for each test using this base class.
		String testName = this.getClass().getSimpleName()+"-testInterface-"+className;

		Path logDir = TestSupport.getTargetPath().resolve("logs");
		if (!Files.exists(logDir))
		  logDir=Files.createDirectory(logDir);

		String defaultLogFile = logDir.resolve("valjogen-"+testName+".log").toString();

		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.LOGFILE, defaultLogFile);

		// Compile source file corresponding to className using annotation processor.
		assertCompileSuccess(JavaFileObjects.forResource(url), configurationOptions);
	}
}
