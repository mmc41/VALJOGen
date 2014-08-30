/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.lang.model.*;
import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.*;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Main class for our annotation processor using the javax annotation processor api. Instantiated and used from javac compiler.
 *
 * @author mmc
 */
public class AnnotationProcessor extends AbstractProcessor
{
	private final Class<VALJOGenerate> annotationGenerateClass = VALJOGenerate.class;
	private final Class<VALJOConfigure> annotationConfigurationClass = VALJOConfigure.class;

	private String processingEnvClassName;
	private String optCtrDefaultSourcePath;
	private Path[] configuredSourcePaths;

	/**
	 * Constructor called automatically by javac compiler.
	 */
	public AnnotationProcessor()
	{
		this(null);
	}

	public AnnotationProcessor(String defaultSourcePath)
	{
		super();
		this.optCtrDefaultSourcePath=defaultSourcePath;
		this.configuredSourcePaths = new Path[0];
	}

	@Override
	public void init(ProcessingEnvironment pe) {
	    super.init(pe);

	    processingEnvClassName = pe.getClass().getName();

	    // NOTE: For the future, we could also inspect source using Tree api by saving instance her : E.g. this.trees = Trees.instance(pe);
	}

	@Override
    public Set<String> getSupportedOptions()
    {
		Class<?> optionClass = ConfigurationOptionKeys.class;

		Field[] fields = optionClass.getFields();

		return Arrays.stream(fields).map(f -> {
			try {
				String value=(String)f.get(null);
				return ConfigurationDefaults.OPTION_QUALIFIER+value;
			} catch(Exception e)
			{
				throw new RuntimeException("Could not access field "+optionClass.getName()+"."+f.getName());
			}
		}).collect(Collectors.toSet());
	}

	/**
	 * Entry point for javac-compiler when calling into our processor
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotationElements, RoundEnvironment roundEnv)
	{
		Messager messager = processingEnv.getMessager();

		try {
			configure(processingEnv.getOptions());
		}
		catch(Exception e)
		{
			messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.ConfigurationFailure, e.toString()));
			return false;
		}

		for (TypeElement te: annotationElements)
		{
			for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
			  if (e.getKind()==ElementKind.INTERFACE) {
				  try {
					VALJOGenerate annotationGenerate = e.getAnnotation(VALJOGenerate.class);
					VALJOConfigure optConfigureConfiguration = getClosestConfiguration(e);

				    generate(annotationGenerate, optConfigureConfiguration, (TypeElement)e);
				  }
				  catch(STException ex)
				  {
					messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.StringTemplateExceptionFailure, e.toString(), ex.toString()), e);
				  }
				  catch(Exception ex)
				  {
					messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.ExceptionFailure, e.toString(), trace(ex)), e);
				  }
			  } else { // A class:
				  messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.AnnotationOnInterfacesOnly, annotationGenerateClass.getSimpleName()), e);
			  }
			}
		}

		return true;
	}

	private void configure(Map<String,String> options) throws Exception
	{
		String sourcePathOption=options.getOrDefault(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.SOURCEPATH, optCtrDefaultSourcePath);

		if (sourcePathOption!=null && sourcePathOption.length()>0) {
		  String[] sourcePathStrings = sourcePathOption.split("\\"+System.getProperty("path.separator"));
		  FileSystem fileSystem = FileSystems.getDefault();

		  configuredSourcePaths = new Path[sourcePathStrings.length];
		  for (int i=0; i<sourcePathStrings.length; ++i)
		  {
			 String sourcePathString = sourcePathStrings[i].trim();
			 Path sourcePath = fileSystem.getPath(sourcePathString);

			 if (!sourcePath.isAbsolute())
			 {
				 try {
					 URL url = this.getClass().getClassLoader().getResource(".");
					 URI uri = url.toURI();
					 Path classPath = Paths.get(uri);
					 sourcePath=classPath.resolve(sourcePath);
				 }
				 catch (Exception e)
				 {
					 throw new ConfigurationException("Could not locate processor path used to resolve relative source path (consider using absolute source path)", e);
				 }
			 }

			 sourcePath=sourcePath.normalize().toAbsolutePath();

			 if (!Files.exists(sourcePath))
				 throw new ConfigurationException("Configured source path directory \""+sourcePathString+"\" does not exist");

			 if (!Files.isDirectory(sourcePath))
				 throw new ConfigurationException("Configured source path \""+sourcePathString+"\" is not a directory");

			 configuredSourcePaths[i]=sourcePath;
		  }
		}
	}

	private String readFileResource(Path path) throws IOException
	{
		return new String(Files.readAllBytes(path));
	}

	private String readFileResource(PackageElement defaultRelPackage, String fileName) throws Exception
	{
		if (configuredSourcePaths.length==0)
			throw new Exception(ConfigurationOptionKeys.SOURCEPATH+" not configured (forgot to specify in annotation processor options?).");

		String defaultRelPackagePath = defaultRelPackage.toString().replace('.', '/');

		// Unfortunately, the Filer api does not allow us to read sources so we do our own lookup using our own source path:
		for (Path sourcePath: configuredSourcePaths)
		{
			Path targetPath = sourcePath.resolve(defaultRelPackagePath).resolve(fileName);
			if (Files.exists(targetPath) && Files.isRegularFile(targetPath) && Files.isReadable(targetPath))
				return readFileResource(targetPath);

			targetPath = sourcePath.resolve(fileName);
			if (Files.exists(targetPath) && Files.isRegularFile(targetPath) && Files.isReadable(targetPath))
				return readFileResource(targetPath);
		}

		throw new FileNotFoundException("Could not find fileName in any specified source path(s)");
	}

	private void generate(VALJOGenerate annotation, VALJOConfigure optConfigureConfiguration, TypeElement element) throws Exception
	{
		Locale optLocale = processingEnv.getLocale();

		Map<String,String> options = processingEnv.getOptions();
		if (options==null)
			options=Collections.emptyMap();

		Configuration configuration = optConfigureConfiguration!=null
				                      ? new Configuration(annotation, optConfigureConfiguration, optLocale, options)
		                              :  new Configuration(annotation, optLocale, options);


		if (configuration.isDebugInfoEnabled())
			System.out.println("Using Annotation processing environment : "+processingEnvClassName);

		Messager messager = processingEnv.getMessager();
		Filer filer = processingEnv.getFiler();
		Types types = processingEnv.getTypeUtils();
		Elements elements = processingEnv.getElementUtils();

		if (configuredSourcePaths.length==0)
			messager.printMessage(Kind.WARNING, "VALJOGen annotion processor option "+ConfigurationOptionKeys.SOURCEPATH+" not specified. Code generation may fail in some cases.");
		else if (configuration.isDebugInfoEnabled()) {
			String resolvedSourcePath = Arrays.stream(configuredSourcePaths).map(p -> "\""+p.toString()+"\"").collect(Collectors.joining(", "));
			System.out.println("Resolved sourcePath : "+resolvedSourcePath);
		}

		ClazzFactory clazzFactory = ClazzFactory.getInstance();

		PackageElement packageElement = (PackageElement)element.getEnclosingElement();

		Clazz clazz = clazzFactory.createClazz(types, elements, element, configuration, (msgElement, kind, err) -> messager.printMessage(kind, err, msgElement), (fileName) -> readFileResource(packageElement, fileName));
		if (clazz==null)
			return;

		if (configuration.isDebugInfoEnabled())
			System.out.println("VALJOGen ANNOTATION PROCESSOR GENERATED CLAZZ MODEL INSTANCE "+System.lineSeparator()+clazz.toString());

		String fileName=stripGenericQualifier(clazz.getName());

		JavaFileObject target = filer.createSourceFile(fileName, element);

		STCodeWriter writer = new STCodeWriter();

		try (PrintWriter targetWriter = new PrintWriter(target.openWriter()))
		{
			String output = writer.outputClass(clazz, configuration);
			if (output!=null)
			{
			  targetWriter.write(output);
			  if (configuration.isVerboseInfoEnabled())
				System.out.println("VALJOGen ANNOTATION PROCESSOR GENERATED TARGET FILE "+fileName+" WITH CONTENT: "+System.lineSeparator()+output);
			}
		}
	}

	/**
	 * Looks for VALJOConfigure in interface and package of interface.
	 *
	 * @param annotatedInterfaceElement The interface that uses the VALJOGen annotation.
	 * @return null if none exist, otherwise closest annotation
	 */
	private VALJOConfigure getClosestConfiguration(Element annotatedInterfaceElement)
	{
		VALJOConfigure configuration = annotatedInterfaceElement.getAnnotation(annotationConfigurationClass);
		if (configuration!=null)
			return configuration;

		Element enlosingElement = annotatedInterfaceElement.getEnclosingElement();
		return (enlosingElement!=null) ? getClosestConfiguration(enlosingElement) : null;
	}

	@Override
	public Set<String> getSupportedAnnotationTypes()
	{
		return java.util.Collections.singleton(annotationGenerateClass.getName());
	}

	@Override
	public SourceVersion getSupportedSourceVersion()
	{
		return SourceVersion.latestSupported();
	}

	private static String trace(Throwable t) {
		StringWriter w = new StringWriter();
		t.printStackTrace(new PrintWriter(w, true));
		return w.toString();
	}
}