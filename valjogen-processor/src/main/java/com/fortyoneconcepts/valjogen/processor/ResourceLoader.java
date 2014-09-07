/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;

/**
 *  Unfortunately, the Filer api does not allow us to read sources
 *  so this class let ous do our own lookup using an explicitly specified source path:
 *
 * @author mmc
 */
public final class ResourceLoader
{
	private List<Path> configuredSourcePaths;
	private String defaultRelPackagePath;

	public ResourceLoader(String sourcePathOption, String defaultRelPackagePath) throws ConfigurationException
	{
		this.configuredSourcePaths=getConfiguredSourcePaths(Objects.requireNonNull(sourcePathOption), Objects.requireNonNull(defaultRelPackagePath));
		this.defaultRelPackagePath=Objects.requireNonNull(defaultRelPackagePath);
	}

	private static List<Path> getConfiguredSourcePaths(String sourcePathOption, String defaultRelPackagePath) throws ConfigurationException
	{
		final String pathSep = System.getProperty("path.separator");

		LinkedHashSet<Path> result = new LinkedHashSet<Path>();
		if (sourcePathOption!=null && sourcePathOption.length()>0) {
		  String[] sourcePathStrings = sourcePathOption.split("\\"+pathSep);
		  FileSystem fileSystem = FileSystems.getDefault();

		  for (int i=0; i<sourcePathStrings.length; ++i)
		  {
			 String sourcePathString = sourcePathStrings[i].trim();
			 Path sourcePath = fileSystem.getPath(sourcePathString);

			 if (!sourcePath.isAbsolute())
			 {
				 String classesPath = System.getProperty("java.class.path");
				 String[] classPathStrings = classesPath.split("\\"+pathSep);

				 boolean hasClassPathFolder=false;
				 for (int j=0; j<classPathStrings.length; ++j)
				 {
					 String pathName = classPathStrings[j];

					 if (!pathName.contains(".jar") && !pathName.contains(".zip") && !pathName.contains(".war") && !pathName.contains(".ear")) {
						 hasClassPathFolder=true;

						 Path singleClassPath = fileSystem.getPath(pathName);
						 Path resolvedSourcePath=singleClassPath.resolve(sourcePath);

						 addIfExist(result, resolvedSourcePath.resolve(defaultRelPackagePath));
						 addIfExist(result, resolvedSourcePath);
					 }
				 }

				 if (!hasClassPathFolder)
				    throw new ConfigurationException("Sourcepath item "+sourcePathString+" is relative to classpath folders but classpath contains no valid folders to base the source path on.");
			 } else {
				 addIfExist(result, sourcePath.resolve(defaultRelPackagePath));
				 addIfExist(result, sourcePath);
			 }
		  }


		  if (result.isEmpty())
			  throw new ConfigurationException("Sourcepath "+sourcePathOption+" with optional default package "+defaultRelPackagePath+" does not resolve to any valid folders.");
		}

		return new ArrayList<Path>(result);
	}

	private static void addIfExist(Collection<Path> result, Path path)
	{
		if (Files.exists(path) && Files.isDirectory(path)) {
			 path=path.normalize().toAbsolutePath();
			 result.add(path);
		}
	}

	public boolean hasSourcePaths()
	{
		return configuredSourcePaths.size()>0;
	}

	public String getResourceAsText(String fileName) throws Exception
	{
		URI uri = getFileResourceAsURL(fileName);

		URL url = uri.toURL();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF8"))) {
		    StringBuilder out = new StringBuilder();
		    String newLine = System.getProperty("line.separator");
		    String line;
		    while ((line = reader.readLine()) != null) {
		        out.append(line);
		        out.append(newLine);
		    }
		    return out.toString();
		}
	}

	public URI getFileResourceAsURL(String fileName) throws Exception
	{
		if (defaultRelPackagePath==null)
			throw new IllegalArgumentException("defaultRelPackagePath may not be null");

		if (fileName==null)
			throw new IllegalArgumentException("filename may not be null");

		if (!hasSourcePaths())
			throw new Exception(ConfigurationOptionKeys.SOURCEPATH+" not configured (forgot to specify in annotation processor options?).");

		List<Path> targetPaths = configuredSourcePaths.stream().map(p -> p.resolve(fileName)).collect(Collectors.toList());

				//concat(configuredSourcePaths.stream().map(p -> p.resolve(defaultRelPackagePath).resolve(fileName)), configuredSourcePaths.stream().map(p -> p.resolve(fileName))).collect(Collectors.toList());

		for (Path targetPath: targetPaths)
		{
			if (Files.exists(targetPath) && Files.isRegularFile(targetPath) && Files.isReadable(targetPath))
				return targetPath.toUri();

			if (Files.exists(targetPath) && Files.isRegularFile(targetPath) && Files.isReadable(targetPath))
				return targetPath.toUri();
		}

		String allTargetPaths = targetPaths.stream().map(p -> "\""+p.toString()+"\"").collect(Collectors.joining(", "));
		throw new FileNotFoundException("Could not find file \""+fileName+"\" in any specified source path(s): "+allTargetPaths);
	}

	@Override
	public String toString()
	{
		return "ResourceLoader {defaultRelPackagePath="+defaultRelPackagePath+", sourcePaths="+configuredSourcePaths.stream().map(p -> "\""+p.toString()+"\"").collect(Collectors.joining(", "))+"}";
	}

}
