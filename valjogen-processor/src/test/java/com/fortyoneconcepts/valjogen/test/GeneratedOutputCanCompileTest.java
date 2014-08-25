package com.fortyoneconcepts.valjogen.test;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.fortyoneconcepts.valjogen.test.input.*;
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
	public void testInterface() throws FileNotFoundException,MalformedURLException, URISyntaxException
	{
		assertCompileSuccess(JavaFileObjects.forResource(getResourcePath(className)));
	}
}
