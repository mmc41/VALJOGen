/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

	public static Path getRootPathExpectedOutput() throws URISyntaxException, FileNotFoundException
	{
		Path root = getProjectRootPath();
		Path result = root.resolve("src").resolve("test").resolve("resources").resolve("expectedoutput");
		return validatePath(result);
	}

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
}
