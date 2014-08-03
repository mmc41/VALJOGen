package com.fortyoneconcepts.valjogen.test;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.lang.model.element.*;
import javax.lang.model.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.processor.STCodeWriter;
import com.fortyoneconcepts.valjogen.test.input.InterfaceAllTypesWithReservedWords;
import com.fortyoneconcepts.valjogen.test.input.SimpleAnnotatedInterface;
import com.google.testing.compile.CompilationRule;

/**
 * Unit test of StringTemplate generation of code for Sample classes. As we use javax.lang.model which we do not normally have access to at runtime in Java 1.8
 * we use javax.lang.model magic proxies supplied by com.google.testing.compile library.
 *
 * Known problems:
 *
 * Reading javaDoc from source in tests not working due to limitation in google com.google.testing library v0.5.
 *
 * @author mmc
 */
public class TemplateTest
{
	@Rule public CompilationRule compilationRule = new CompilationRule();

	private Types types;
	private Elements elements;
	private ClazzFactory clazzFactory;
	private AnnotationProxyBuilder<VALJOGenerate> generateAnnotationBuilder;
	private AnnotationProxyBuilder<VALJOConfigure> configureAnnotationBuilder;

	@Before
	public void init() throws URISyntaxException
	{
		types = Objects.requireNonNull(compilationRule.getTypes());
		elements = Objects.requireNonNull(compilationRule.getElements());

		generateAnnotationBuilder = new AnnotationProxyBuilder<VALJOGenerate>(VALJOGenerate.class);
		configureAnnotationBuilder = new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class);

		clazzFactory = Objects.requireNonNull(ClazzFactory.getInstance());
	}

	public abstract class SampleBase
	{
		public SampleBase() {}
	}

	/**
	 * This is a sample class.
	 *
	 * @author mmc
	 */
	public interface Sample
	{
		/**
		 * This is a sample getter for X.
		 *
		 * @author mmc
		 */
		public int getX();
		public void setX(int p);
		public double getY();
		public String[] getStr();
		public int[] getAry();

		public Sample setY(double y);

		//public int doSomething(int y);
	};

	@Test
	public void sampleTestWithDefaultAnnotation() throws Exception
	{
		Configuration configuration = new Configuration(generateAnnotationBuilder.build(), configureAnnotationBuilder.build(), Locale.ENGLISH, Collections.emptyMap());

//		Configuration configuration = new Configuration(generateAnnotationBuilder.build(), configureAnnotationBuilder.add(ConfigurationOptionKeys.baseClazzName,  SampleBase.class.getCanonicalName()).build(), Locale.ENGLISH, Collections.emptyMap());

		Clazz clazz = clazzFactory.createClazz(types, elements, elements.getTypeElement(Sample.class.getCanonicalName()), configuration, e -> { throw new RuntimeException(e); });


	//	Object o = (clazz.getPropertyMethods().toArray(Property::new)[0]);

		// Name name = elements.getBinaryName(elements.getTypeElement(Sample.class.getCanonicalName()));

		//System.out.println("name = "+name.toString());


		STCodeWriter codeWriter = new STCodeWriter(failureMsg -> Assert.fail(failureMsg));

		String output = codeWriter.outputClass(clazz, configuration);


		//PackageElement pElement = elements.getPackageElement(Sample.class.getPackage().getName());
	    //TypeElement tElement = elements.getTypeElement(Sample.class.getCanonicalName());

	    // System.err.println(output);

	}

}
