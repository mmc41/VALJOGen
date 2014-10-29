/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
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
import com.fortyoneconcepts.valjogen.processor.builders.ModelBuilder;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.*;

/**
 * Main class for our annotation processor using the javax annotation processor api. Instantiated and used from javac compiler.
 *
 * @author mmc
 */
public class AnnotationProcessor extends AbstractProcessor
{
	private final static Logger LOGGER = Logger.getLogger(AnnotationProcessor.class.getName());

	private final Class<VALJOGenerate> annotationGenerateClass = VALJOGenerate.class;
	private final Class<VALJOConfigure> annotationConfigurationClass = VALJOConfigure.class;

	private String processingEnvClassName;
	private final Logger parentLogger;
	private final Map<String,String> ctrOptions;

	// Our own FileHandler class so we can recognize it from other FileHandlers.
	private final class InternalFileHandler extends FileHandler
	{
		private final String pattern;

		public InternalFileHandler(String pattern, boolean append) throws IOException, SecurityException
		{
			super(pattern, append);
			this.pattern=pattern;
		}

		public String getPattern()
		{
			return pattern;
		}
	}

	/**
	 * Constructor called automatically by javac compiler.
	 */
	public AnnotationProcessor()
	{
		this(Collections.emptyMap());
	}

	public AnnotationProcessor(Map<String,String> ctrOptions)
	{
		super();

		this.ctrOptions=ctrOptions;

		parentLogger = Logger.getLogger(ConfigurationDefaults.TOP_PACKAGE_NAME);

		// Set a tempoary default log level until configuration has been read.
		parentLogger.setLevel(Level.INFO);
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

		Set<String> stdOptions = Arrays.stream(fields).map(f -> {
			try {
				String value=(String)f.get(null);
				return ConfigurationDefaults.OPTION_QUALIFIER+value;
			} catch(Exception e)
			{
				throw new RuntimeException("Could not access field "+optionClass.getName()+"."+f.getName());
			}
		}).collect(Collectors.toSet());

		return stdOptions;
	}

	/**
	 * Entry point for javac-compiler when calling into our processor
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotationElements, RoundEnvironment roundEnv)
	{
		Messager messager = processingEnv.getMessager();
		Locale optLocale = processingEnv.getLocale();

		boolean claimed = true;

		for (TypeElement te: annotationElements)
		{
			for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
			  if (e.getKind()==ElementKind.INTERFACE) {
				  try {
					VALJOGenerate annotationGenerate = e.getAnnotation(VALJOGenerate.class);
					VALJOConfigure optConfigureConfiguration = getClosestConfiguration(e);

					Map<String,String> processorEnvOptions = processingEnv.getOptions();
					if (processorEnvOptions==null)
						processorEnvOptions=new HashMap<String,String>();

					// Merge processor options with any options provided directly to constructor.
					Map<String,String> options = new HashMap<String,String>(processorEnvOptions);
					for(Entry<String, String> ctrOption : ctrOptions.entrySet()) {
						   options.putIfAbsent(ctrOption.getKey(), ctrOption.getValue());
					}

					String masterInterfaceName = e.asType().toString();
					Configuration configuration = optConfigureConfiguration!=null
							                      ? new Configuration(masterInterfaceName, annotationGenerate, optConfigureConfiguration, optLocale, options)
					                              :  new Configuration(masterInterfaceName, annotationGenerate, optLocale, options);

					setUpLogging(configuration);

					String srcPath = configuration.getSourcePathOrDefault();
					LOGGER.fine(() -> "GOT SOURCEPATH: "+srcPath);

					LOGGER.info(() -> "VALJOGen ANNOTATION PROCESSOR CONFIGURATION "+System.lineSeparator()+configuration);

					PackageElement packageElement = (PackageElement)(e.getEnclosingElement());
					String sourcePackageElementPath = packageElement.getQualifiedName().toString().replace(".", File.separator);
					ResourceLoader resourceLoader = new ResourceLoader(srcPath, sourcePackageElementPath);

				    generate((TypeElement)e, configuration, resourceLoader);

				    claimed=true;
				  } catch (ConfigurationException ex)  {
					  if (LOGGER.isLoggable(Level.INFO))
						  messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage(), e);
					  else messager.printMessage(Diagnostic.Kind.ERROR, ex.getMessage());
				  } catch(STException ex)
				  {
					 messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.StringTemplateExceptionFailure, e.toString(), ex.toString()), e);
				  } catch(Exception ex)
				  {
					 messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.ExceptionFailure, e.toString(), LOGGER.isLoggable(Level.INFO) ? trace(ex) : ex), e);
				  }
			  } else { // A class:
				  messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.AnnotationOnInterfacesOnly, annotationGenerateClass.getSimpleName()), e);
			  }
			}
		}

		return claimed;
	}

	private void generate(TypeElement element, Configuration configuration, ResourceLoader resourceLoader) throws Exception
	{
		LOGGER.fine(() -> "Using Annotation processing environment : "+processingEnvClassName);

		Messager messager = processingEnv.getMessager();
		Filer filer = processingEnv.getFiler();
		Types types = processingEnv.getTypeUtils();
		Elements elements = processingEnv.getElementUtils();

		if (!resourceLoader.hasSourcePaths())
			messager.printMessage(Kind.MANDATORY_WARNING, ProcessorMessages.SourcePathNotSet);
		else {
			LOGGER.fine(() -> "Using resourceloader: "+resourceLoader);
		}

		STTemplates templates = new STTemplates(resourceLoader, configuration);

		ModelBuilder clazzFactory = new ModelBuilder(types, elements, (msgElement, kind, err) -> {
			if (msgElement!=null)
				messager.printMessage(kind, err, msgElement);
			else messager.printMessage(kind, err);
		}, element, configuration, resourceLoader, templates);

		Clazz clazz = clazzFactory.buildNewCLazz();
		if (clazz==null)
			return;

		LOGGER.info(() -> "VALJOGen ANNOTATION PROCESSOR GENERATED CLAZZ MODEL INSTANCE "+System.lineSeparator()+clazz.toString());

		STCodeWriter writer = new STCodeWriter(clazz, configuration, templates);
		String output = writer.outputClass();
		if (output!=null)
		{
			String fileName=stripGenericQualifier(clazz.getQualifiedName());

			JavaFileObject target = filer.createSourceFile(fileName, element);

			try (PrintWriter targetWriter = new PrintWriter(target.openWriter()))
			{
			  targetWriter.write(output);
  			  LOGGER.info(() -> "VALJOGen ANNOTATION PROCESSOR GENERATED TARGET FILE "+fileName+" WITH CONTENT: "+System.lineSeparator()+output);
			}

		    messager.printMessage(Kind.NOTE, "VALJOGen Annotation Processor successfully generated file "+target.getName());
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

	private void setUpLogging(Configuration configuration)	throws ConfigurationException
	{
		String logFileString = configuration.getLogFileOrDefault();

		try {
			// Only add a filehandler if it is not there already.
			Handler[] handlers = parentLogger.getHandlers();
			boolean alreadyAddedLogger = false;
		    for (Handler handler : handlers) {
			    if (handler instanceof InternalFileHandler) {
			    		alreadyAddedLogger=true;
			    }
		    }

			if (!alreadyAddedLogger && logFileString!=null) {
				FileHandler logFile = new InternalFileHandler(logFileString, true);
				logFile.setFormatter(new SimpleFormatter());
				logFile.setLevel(Level.FINEST);
				parentLogger.addHandler(logFile);
			}
		} catch(Throwable ex)
		{
			throw new ConfigurationException("Could not setup log file at "+logFileString, ex);
		}

        // Know that we know what proper log level to set, do set it correctly.
	    parentLogger.setLevel(configuration.getLogLevel());
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