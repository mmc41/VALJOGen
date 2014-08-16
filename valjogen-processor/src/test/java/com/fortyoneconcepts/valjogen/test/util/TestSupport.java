package com.fortyoneconcepts.valjogen.test.util;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.junit.Assert;

import com.fortyoneconcepts.valjogen.test.GeneratedOutputCanCompileTest;

/**
 * This class provides various helpers for test.
 *
 * @author mmc
 */
public final class TestSupport
{
	public static Path getProjectRootPath() throws URISyntaxException
	{
		URL url = ClassLoader.getSystemResource(".");
		if (!"file".equalsIgnoreCase(url.getProtocol()))
			throw new IllegalStateException("Could not find file location of this class used as reference for finding all other files");
		return Paths.get(url.toURI()).getParent().getParent();
	}

	public static URL getResourcePath(String className) throws FileNotFoundException
	{
		String clazzSourceFile = className.replace(".", "/").concat(".java");
		URL url = GeneratedOutputCanCompileTest.class.getClassLoader().getResource("source/"+className.replace(".", "/").concat(".java"));
		if (url==null)
			throw new FileNotFoundException(clazzSourceFile.toString());
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
}
