/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
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
	private final static Logger LOGGER = Logger.getLogger(AnnotationProcessor.class.getName());

	private final Class<VALJOGenerate> annotationGenerateClass = VALJOGenerate.class;
	private final Class<VALJOConfigure> annotationConfigurationClass = VALJOConfigure.class;

	private String processingEnvClassName;
	private String optCtrDefaultSourcePath;

	private final Logger parentLogger;

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
		Locale optLocale = processingEnv.getLocale();

		for (TypeElement te: annotationElements)
		{
			for (Element e : roundEnv.getElementsAnnotatedWith(te)) {
			  if (e.getKind()==ElementKind.INTERFACE) {
				  try {
					VALJOGenerate annotationGenerate = e.getAnnotation(VALJOGenerate.class);
					VALJOConfigure optConfigureConfiguration = getClosestConfiguration(e);

					Map<String,String> options = processingEnv.getOptions();
					if (options==null)
						options=Collections.emptyMap();

					Configuration configuration = optConfigureConfiguration!=null
							                      ? new Configuration(annotationGenerate, optConfigureConfiguration, optLocale, options)
					                              :  new Configuration(annotationGenerate, optLocale, options);

			        // Know that we know what proper log level to set, do set it correctly.
				    parentLogger.setLevel(configuration.getLogLevel());

					String path = processingEnv.getOptions().getOrDefault(ConfigurationDefaults.OPTION_QUALIFIER+ConfigurationOptionKeys.SOURCEPATH, optCtrDefaultSourcePath);
					LOGGER.fine(() -> "GOT SOURCEPATH: "+path);

					PackageElement packageElement = (PackageElement)(e.getEnclosingElement());
					String sourcePackageElementPath = packageElement.toString().replace('.', '/');
					ResourceLoader resourceLoader = new ResourceLoader(path, sourcePackageElementPath);

				    generate((TypeElement)e, configuration, resourceLoader);
				  }
				  catch(STException ex)
				  {
					 messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.StringTemplateExceptionFailure, e.toString(), ex.toString()), e);
				  }
				  catch(Exception ex)
				  {
					 messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.ExceptionFailure, e.toString(), LOGGER.isLoggable(Level.INFO) ? trace(ex) : ex), e);
				  }
			  } else { // A class:
				  messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.AnnotationOnInterfacesOnly, annotationGenerateClass.getSimpleName()), e);
			  }
			}
		}

		return true;
	}

	private void generate(TypeElement element, Configuration configuration, ResourceLoader resourceLoader) throws Exception
	{
		LOGGER.fine(() -> "Using Annotation processing environment : "+processingEnvClassName);

		Messager messager = processingEnv.getMessager();
		Filer filer = processingEnv.getFiler();
		Types types = processingEnv.getTypeUtils();
		Elements elements = processingEnv.getElementUtils();

		if (!resourceLoader.hasSourcePaths())
			messager.printMessage(Kind.WARNING, "VALJOGen annotion processor option "+ConfigurationOptionKeys.SOURCEPATH+" not specified. Code generation may fail in some cases.");
		else {
			LOGGER.fine(() -> "Using resourceloader: "+resourceLoader);
		}

		ClazzFactory clazzFactory = ClazzFactory.getInstance();

		Clazz clazz = clazzFactory.createClazz(types, elements, element, configuration, (msgElement, kind, err) -> messager.printMessage(kind, err, msgElement), resourceLoader);
		if (clazz==null)
			return;

		LOGGER.info(() -> "VALJOGen ANNOTATION PROCESSOR GENERATED CLAZZ MODEL INSTANCE "+System.lineSeparator()+clazz.toString());

		String fileName=stripGenericQualifier(clazz.getName());

		JavaFileObject target = filer.createSourceFile(fileName, element);

		STCodeWriter writer = new STCodeWriter(resourceLoader);

		try (PrintWriter targetWriter = new PrintWriter(target.openWriter()))
		{
			String output = writer.outputClass(clazz, configuration);
			if (output!=null)
			{
			  targetWriter.write(output);
  			  LOGGER.info(() -> "VALJOGen ANNOTATION PROCESSOR GENERATED TARGET FILE "+fileName+" WITH CONTENT: "+System.lineSeparator()+output);
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