/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javax.tools.JavaFileObject;

import org.junit.Assert;

import com.fortyoneconcepts.valjogen.processor.AnnotationProcessor;
import com.fortyoneconcepts.valjogen.test.GeneratedOutputCanCompileTest;
import com.google.testing.compile.JavaFileObjects;

/**
 * This class provides various helpers for test.
 *
 * Unfortunately, we are currenly forced to use a google's Truth framework below for assertCompile methods
 * instead of a JUnit assert like all the other tests (do not use this framework for other purposes):
 * @author mmc
 */
public final class TestSupport
{
	// TODO: Move thesethis build tool cfg:

	private static String classPathSourceCopyRelPath = "source"; // Source files copied to this dir in test output.

	private static String getSrcPath(String relpath)
	{
		return classPathSourceCopyRelPath+"/"+relpath;
	}

	public static Path getTargetPath() throws URISyntaxException
	{
		URL url = ClassLoader.getSystemResource(".");
		if (!"file".equalsIgnoreCase(url.getProtocol()))
			throw new IllegalStateException("Could not find file location of this class used as reference for finding all other files");
		return Paths.get(url.toURI()).getParent();
	}

	public static Path getProjectRootPath() throws URISyntaxException
	{
		URL url = ClassLoader.getSystemResource(".");
		if (!"file".equalsIgnoreCase(url.getProtocol()))
			throw new IllegalStateException("Could not find file location of this class used as reference for finding all other files");
		return Paths.get(url.toURI()).getParent().getParent();
	}

	public static String getFileContent(URL url) throws IOException
	{
		JavaFileObject fileObject = JavaFileObjects.forResource(url);
		return fileObject.getCharContent(false).toString();
	}

	public static URL getTestSourceFileResourcePath(String relPath) throws FileNotFoundException
	{
		String absPath = getSrcPath(relPath);
		URL url = TestSupport.class.getClassLoader().getResource(absPath);
		if (url==null)
			throw new FileNotFoundException(absPath);
		return url;
	}

	public static URL getJavaSourceResourcePath(String className) throws FileNotFoundException
	{
		String absPath = getSrcPath(className.replace(".", "/").concat(".java"));
		URL url = GeneratedOutputCanCompileTest.class.getClassLoader().getResource(absPath);
		if (url==null)
			throw new FileNotFoundException(absPath);
		return url;
	}

	public static Stream<String> getClassNames(ClassLoader classLoader, String packageName) throws Throwable
	{
		String packageNamePath = packageName.replace('.', '/');

		URL packageUrl;
		try {
		  packageUrl = classLoader.getResources(packageNamePath).nextElement();
		} catch (Exception e)
		{
		  throw new Exception("Could not find package "+packageName);
		}

		Path packagepath = FileSystems.getDefault().getPath(packageUrl.getFile());
		return Files.walk(packagepath).filter(path -> path.getFileName().toString().endsWith(".class") && !path.getFileName().toString().contains("package-info")).map(path -> {
			return packageName+"."+path.getFileName().toString().replace(".class", "");
		});
	}

	private static String createWildCardRegEx(String str)
	{
		return str.replace("?", ".?").replace("*", ".*?").replace("{","\\{").replace("}","\\}").replace("(","\\(").replace(")","\\)");
	}

	public static void assertContainsWithWildcards(String expectedSubstringWithWildcards, String string)
	{
		String escapedString = createWildCardRegEx(expectedSubstringWithWildcards);
		Pattern p = Pattern.compile(escapedString, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = p.matcher(string);
	    Assert.assertTrue("String '"+string+"' does not contain the expected wildcard substring '"+expectedSubstringWithWildcards+"'", matcher.find());
	}

	public static void assertNotContainsWithWildcards(String expectedSubstringWithWildcards, String string)
	{
		String escapedString = createWildCardRegEx(expectedSubstringWithWildcards);
		Pattern p = Pattern.compile(escapedString, Pattern.MULTILINE | Pattern.DOTALL);
		Matcher matcher = p.matcher(string);
	    Assert.assertFalse("String '"+string+"' does contain the unexpected wildcard substring '"+expectedSubstringWithWildcards+"'", matcher.find());
	}

	public static void assertContains(String expectedSubstring, String string)
	{
		Assert.assertTrue("String '"+string+"' does not contain the expected substring '"+expectedSubstring+"'", string.contains(expectedSubstring));
	}

	public static void assertNotContains(String expectedSubstring, String string)
	{
		Assert.assertFalse("String '"+string+"' does contain the unexpected substring '"+expectedSubstring+"'", string.contains(expectedSubstring));
	}

	public static void assertCompileFailure(String qualifiedClassName, String source, String errorMsg)
	{
		JavaFileObject javaObject = JavaFileObjects.forSourceString(qualifiedClassName, source);

		org.truth0.Truth.ASSERT.about(com.google.testing.compile.JavaSourceSubjectFactory.javaSource())
		 .that(javaObject)
		 .processedWith(new AnnotationProcessor(TestClassConstants.relSourcePath))
		 .failsToCompile()
		 .withErrorContaining(errorMsg).in(javaObject);
	}

	public static void assertCompileSuccess(String qualifiedClassName, String source)
	{
		JavaFileObject javaObject = JavaFileObjects.forSourceString(qualifiedClassName, source);

		org.truth0.Truth.ASSERT.about(com.google.testing.compile.JavaSourceSubjectFactory.javaSource())
		 .that(javaObject)
		 .processedWith(new AnnotationProcessor(TestClassConstants.relSourcePath))
		 .compilesWithoutError();
	}

	public static void assertCompileSuccess(JavaFileObject forResource) throws FileNotFoundException
	{
		org.truth0.Truth.ASSERT.about(com.google.testing.compile.JavaSourceSubjectFactory.javaSource())
		 .that(forResource)
		 .processedWith(new AnnotationProcessor(TestClassConstants.relSourcePath))
		 .compilesWithoutError();
	}
}
