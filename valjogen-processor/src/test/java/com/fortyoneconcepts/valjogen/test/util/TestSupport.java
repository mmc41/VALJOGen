package com.fortyoneconcepts.valjogen.test.util;

import java.io.FileNotFoundException;
import java.net.URL;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import com.fortyoneconcepts.valjogen.test.GeneratedOutputCanCompileTest;

public class TestSupport
{
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
}
