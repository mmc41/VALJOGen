/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.io.IOException;
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

public class AnnotationProcessor extends AbstractProcessor
{
	private Class<VALJOGenerate> annotationGenerateClass = VALJOGenerate.class;
	private Class<VALJOConfigure> annotationConfigurationClass = VALJOConfigure.class;

	public AnnotationProcessor()
	{
		super();
	}

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
				  } catch(Exception ex)
				  {
					messager.printMessage(Diagnostic.Kind.ERROR, "Failed processing "+e.toString()+" due to exception "+ex, e);
				  }
			  } else messager.printMessage(Diagnostic.Kind.ERROR, "Annotation "+annotationGenerateClass.getSimpleName()+ " may only be used with interfaces.", e);
			}
		}



		return true;
	}

	private void generate(VALJOGenerate annotation, VALJOConfigure optConfigureConfiguration, TypeElement element) throws IOException
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

		Clazz clazz = clazzFactory.createClazz(types, elements, element, configuration, err -> messager.printMessage(Diagnostic.Kind.ERROR, err, element));

		String fileName=NamesUtil.stripGenericQualifier(clazz.getName());

		JavaFileObject target = filer.createSourceFile(fileName, element);

		STCodeWriter writer = new STCodeWriter(err -> messager.printMessage(Diagnostic.Kind.ERROR, err));

		try (PrintWriter targetWriter = new PrintWriter(target.openWriter()))
		{
			messager.printMessage(Diagnostic.Kind.NOTE, "Writing file : "+fileName);

			String output = writer.outputClass(clazz, configuration);
			if (output!=null)
			  targetWriter.write(output); // Locale ???

			messager.printMessage(Diagnostic.Kind.NOTE, "done Writing file : "+fileName);
		}
	}

	/**
	 * Looks for VALJOConfigure in interface and package of interface.
	 * @return null if none exist, otherwise closest annotation
	 */
	public VALJOConfigure getClosestConfiguration(Element e)
	{
		VALJOConfigure configuration = e.getAnnotation(annotationConfigurationClass);
		if (configuration!=null)
			return configuration;

		Element enlosingElement = e.getEnclosingElement();
		return (enlosingElement!=null) ? getClosestConfiguration(enlosingElement) : null;

	}

	@SuppressWarnings("serial")
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
