package com.fortyoneconcepts.valjogen.test;

import static org.junit.Assert.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.lang.model.util.*;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.fortyoneconcepts.valjogen.annotations.*;
import com.fortyoneconcepts.valjogen.model.*;
import com.fortyoneconcepts.valjogen.processor.STCodeWriter;
import com.fortyoneconcepts.valjogen.test.util.AnnotationDefaultsProxy;
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
	private Configuration configuration;
	private ClazzFactory clazzFactory;

	@Before
	public void init() throws URISyntaxException
	{
		types = Objects.requireNonNull(compilationRule.getTypes());
		elements = Objects.requireNonNull(compilationRule.getElements());
		configuration = new Configuration(AnnotationDefaultsProxy.defaultsOf(GenerateVALJO.class), Locale.ENGLISH);
		clazzFactory = Objects.requireNonNull(ClazzFactory.getInstance());
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
		public String getStr();
		public int[] getAry();

		public int doSomething(int y);
	};

	// @Test
	public void sampleTestWithDefaultAnnotation() throws Exception
	{
		Clazz clazz = clazzFactory.createWithProperties(types, elements, elements.getTypeElement(Sample.class.getCanonicalName()), configuration, e -> { throw new RuntimeException(e); });

		STCodeWriter codeWriter = new STCodeWriter(failureMsg -> Assert.fail(failureMsg));

		String output = codeWriter.outputClass(clazz, configuration);


		//PackageElement pElement = elements.getPackageElement(Sample.class.getPackage().getName());
	    //TypeElement tElement = elements.getTypeElement(Sample.class.getCanonicalName());

	    // System.err.println(output);

	}

}
