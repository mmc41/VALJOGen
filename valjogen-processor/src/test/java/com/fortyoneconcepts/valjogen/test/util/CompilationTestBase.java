package com.fortyoneconcepts.valjogen.test.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Rule;
import org.junit.rules.TestName;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;
import com.fortyoneconcepts.valjogen.model.Configuration;
import com.fortyoneconcepts.valjogen.model.ConfigurationDefaults;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/**
 * Common superclass for compilation-based tests.
 *
 * @author mmc
 */
public class CompilationTestBase
{
	@Rule
	public TestName nameRule = new TestName();

	 /**
	 * Get URL to source code for class.
     *
	 * @param qualifiedClassName The qualified class name of the class to find the source for.
	 * @return The URL of the class
	 * @throws URISyntaxException If error.
	 * @throws MalformedURLException If error.
	 * @throws FileNotFoundException If error.
	 *
	 */
	public URL getSourceUrl(String qualifiedClassName) throws URISyntaxException, MalformedURLException, FileNotFoundException
	{
		// Create a confogiration used for reading the source path - not used for anything else.
		VALJOGenerate generate = new AnnotationProxyBuilder<VALJOGenerate>(VALJOGenerate.class).build();
		VALJOConfigure configure = new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class).build();
		Configuration configuration = new Configuration(null, generate, configure, Locale.ENGLISH, new HashMap<String,String>());

		// Setup url to source dir.
		Path targetPath = TestSupport.getClassPath();
		Path path = targetPath.resolve(configuration.getSourcePath());
		path = path.resolve(qualifiedClassName.replace(".", "/")+".java");
		path = path.toAbsolutePath().normalize();
		URL url = path.toUri().toURL();
		if (url==null)
			throw new FileNotFoundException(path.toString());

		return url;
	}

	 /**
	 * Get standard options with individual log file for class.
     *
	 * @param qualifiedClassName The qualified class name of the class to setup options for.
	 * @return The initialized standard options
	 * @throws URISyntaxException If error.
	 * @throws IOException If error.
	 *
	 */
	public Map<String,String> getOptions(String qualifiedClassName) throws URISyntaxException, IOException
	{
		Map<String,String> configurationOptions = new HashMap<String,String>();

		// Setup a default of a different log file for each test using this base class.
		String testName = this.getClass().getSimpleName()+"-"+nameRule.getMethodName()+"-"+NamesUtil.getUnqualifiedName(qualifiedClassName);

		Path logDir = TestSupport.getTargetPath().resolve("logs");
		if (!Files.exists(logDir))
		  logDir=Files.createDirectory(logDir);

		String defaultLogFile = logDir.resolve("valjogen-"+testName+".log").toString();

		configurationOptions.put(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.LOGFILE, defaultLogFile);

		return configurationOptions;
	}
}
