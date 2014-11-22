/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fortyoneconcepts.valjogen.model.ConfigurationDefaults;

/**
 * Utilities for finding folders with generated and expected source files.
 *
 * @author mmc
 */
public final class TestSupport
{
	public static final String unavailableFile = "Unavailable file";

	public static List<Object[]> getData(Path expectedSourcePath, Path actualOutputPath) throws IOException, FileNotFoundException {
		Path[] expectedFiles = Files.walk(expectedSourcePath).filter(p -> p.toString().endsWith(".java")).sorted().toArray(size -> new Path[size]);
		Path[] actualFiles = Files.walk(actualOutputPath).filter(p -> p.toString().endsWith(".java")).sorted().toArray(size -> new Path[size]);

		// Check - do we get any output:
		if (expectedFiles.length>0 && actualFiles.length==0)
			throw new FileNotFoundException("Could not detect any generated source files at '"+actualOutputPath+"'. Is annotation processor configured correctly in build configuration file?");
		else if (actualFiles.length>0 && expectedFiles.length==0)
			throw new FileNotFoundException("Could not detect any expected source files at '"+expectedSourcePath+"'. Have previously accepted golden master files been copied?");

		List<Object[]> result = new ArrayList<Object[]>();
		for(int i=0; i<Math.max(expectedFiles.length, actualFiles.length); ++i)
		{
			Path expectedFile = i<expectedFiles.length ? expectedFiles[i] : null;
			Path actualFile = i<actualFiles.length ? actualFiles[i] : null;

			result.add(new Object[] { getFileName(expectedFile), getFileName(actualFile), expectedFile, actualFile });
		}
		return result;
	}

	public static final String getFileName(Path optPath)
	{
		return optPath!=null ? optPath.getFileName().toString() : unavailableFile;
	}

	// TODO: Configure externally in maven
	public static Path getRootPathExpectedOutput() throws URISyntaxException, FileNotFoundException
	{
		Path root = getProjectRootPath();
		Path result = root.resolve("src").resolve("test").resolve("resources").resolve("expectedoutput");
		return validatePath(result);
	}

	// TODO: Configure externally in maven
	public static Path getRootPathGeneratedOutput() throws URISyntaxException, FileNotFoundException
	{
		Path root = getProjectRootPath();
		Path result = root.resolve("target").resolve("generated-test-sources").resolve("test-annotations");
		return validatePath(result);
	}

	private static Path validatePath(Path result) throws FileNotFoundException {
		if (!Files.exists(result) || !Files.isDirectory(result))
			throw new FileNotFoundException("Could not locate directory "+result.toString());

		return result;
	}

	public static Path getProjectRootPath() throws URISyntaxException
	{
		URL url = ClassLoader.getSystemResource(".");
		if (!"file".equalsIgnoreCase(url.getProtocol()))
			throw new IllegalStateException("Could not find file location of this class used as reference for finding all other files");
		return Paths.get(url.toURI()).getParent().getParent();
	}


	@SuppressWarnings("unchecked")
	public static <T> T createInstanceUsingFactory(Class<T> clazz) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Method mostCompleteFactoryMethod = Arrays.stream(clazz.getMethods()).filter(m->m.getName().equals(ConfigurationDefaults.factoryMethodName) && Modifier.isStatic(m.getModifiers())).max((a,b) -> Integer.compare(a.getParameterCount(), b.getParameterCount())).get();

		Parameter[] parameters = mostCompleteFactoryMethod.getParameters();

		Object[] args = new Object[parameters.length];

		for (int i=0; i<parameters.length; ++i)
		{
			args[i]=getTestValue(parameters[i].getType());
		}

		return (T)mostCompleteFactoryMethod.invoke(null, args);
	}

	public static Object getTestValue(Class<?> clazz)
	{
		if (clazz.equals(String.class))
			return "testString";
		else if (clazz.equals(Object.class))
			return "testObject";
		else if (clazz.equals(Integer.TYPE))
			return (byte)1;
		else if (clazz.equals(Integer.class))
			return 2;
		else if (clazz.equals(Byte.TYPE))
			return (byte)3;
		else if (clazz.equals(Byte.class))
			return Byte.valueOf((byte)4);
		else if (clazz.equals(Long.TYPE))
			return 3L;
		if (clazz.equals(Long.class))
			return Long.valueOf(3L);
		else if (clazz.equals(Boolean.TYPE))
			return true;
		else if (clazz.equals(Boolean.class))
			return Boolean.valueOf(true);
		else if (clazz.equals(Float.TYPE))
			return 4.4f;
		else if (clazz.equals(Float.class))
			return Float.valueOf(4.4f);
		else if (clazz.equals(Double.TYPE))
			return 5.5d;
		else if (clazz.equals(Double.class))
			return Double.valueOf(5.5d);
		else if (clazz.equals(Character.TYPE))
			return 'B';
		else if (clazz.equals(Character.class))
			return Character.valueOf('C');

		throw new IllegalArgumentException("Presently unsupported argument type '"+clazz+"'");
	}

	public static boolean compareInstanceFields(Object a, Object b) throws IllegalArgumentException, IllegalAccessException
	{
		Class<?> aClass = a.getClass();
		Class<?> bClass = b.getClass();

		if (!aClass.equals(bClass))
			return false;

		return compareInstanceFields(a, b, aClass);
	}

	private static boolean compareInstanceFields(Object a, Object b, Class<?> clazz) throws IllegalArgumentException, IllegalAccessException
	{
		Field[] fields = clazz.getDeclaredFields();

		for (int i=0; i<fields.length; ++i)
		{
			fields[i].setAccessible(true);

			Object afv = fields[i].get(a);
			Object bfv = fields[i].get(b);
			if (!afv.equals(bfv))
				return false;
		}

		Class<?> base = clazz.getSuperclass();
		if (base!=null && !base.getSimpleName().equals("Object"))
			return compareInstanceFields(a, b, base);
		else return true;
	}
}
