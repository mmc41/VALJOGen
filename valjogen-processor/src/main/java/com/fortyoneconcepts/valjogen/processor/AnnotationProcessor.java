/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.element.*;
import javax.lang.model.util.*;
import javax.lang.model.*;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.model.util.NamesUtil;

/**
 * Main class for our annotation processor using the javax annotation processor api. Instantiated and used from javac compiler.
 *
 * @author mmc
 */
public class AnnotationProcessor extends AbstractProcessor
{
	private final Class<VALJOGenerate> annotationGenerateClass = VALJOGenerate.class;
	private final Class<VALJOConfigure> annotationConfigurationClass = VALJOConfigure.class;

	/**
	 * Constructor called automatically by javac compiler.
	 */
	public AnnotationProcessor()
	{
		super();
	}

	/**
	 * Entry point for javac-compiler when calling into our processor
	 */
	@Override
	public boolean process(Set<? extends TypeElement> annotationElements, RoundEnvironment roundEnv)
	{
		Messager messager = processingEnv.getMessager();

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
					messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.ExceptionFailure, e.toString(), ex.toString()), e);
				  }
			  } else { // A class:
				  messager.printMessage(Diagnostic.Kind.ERROR, String.format(ProcessorMessages.AnnotationOnInterfacesOnly, annotationGenerateClass.getSimpleName()), e);
			  }
			}
		}

		return true;
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

		Messager messager = processingEnv.getMessager();
		Filer filer = processingEnv.getFiler();
		Types types = processingEnv.getTypeUtils();
		Elements elements = processingEnv.getElementUtils();

		ClazzFactory clazzFactory = ClazzFactory.getInstance();

		Clazz clazz = clazzFactory.createClazz(types, elements, element, configuration, (msgElement, kind, err) -> messager.printMessage(kind, err, msgElement));
		if (clazz==null)
			return;

		String fileName=NamesUtil.stripGenericQualifier(clazz.getName());

		JavaFileObject target = filer.createSourceFile(fileName, element);

		STCodeWriter writer = new STCodeWriter();

		try (PrintWriter targetWriter = new PrintWriter(target.openWriter()))
		{
			String output = writer.outputClass(clazz, configuration);
			if (output!=null)
			{
			  targetWriter.write(output);
			  if (configuration.isDebugInfoEnabled())
				System.out.println("VALJOGen ANNOTATION PROCESSOR GENERATED FILE "+fileName+" WITH CONTENT: "+System.lineSeparator()+output);
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
}
