package com.fortyoneconcepts.valjogen.test.util;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;
import com.fortyoneconcepts.valjogen.model.Clazz;
import com.fortyoneconcepts.valjogen.model.ClazzFactory;
import com.fortyoneconcepts.valjogen.model.Configuration;
import com.fortyoneconcepts.valjogen.model.ConfigurationOptionKeys;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.processor.STCodeWriter;
import com.google.testing.compile.CompilationRule;

/**
 * Common superclass for template-based tests.Derived tests are not supposed to check for compilation errors or that names are qualified correctly. This should be covered by the seperate tests that
 * checks if source will compile.
 *
 * As we use javax.lang.model which we do not normally have access to at runtime in Java 1.8 we use javax.lang.model magic proxies supplied by com.google.testing.compile library.
 *
 * Known problems:
 * No tests for javaDocs as reading javaDocs from source in tests not working due to limitation in google com.google.testing library v0.5.
 *
 * @author mmc
 */
public abstract class TemplateTestBase
{
	@Rule
	public CompilationRule compilationRule = new CompilationRule();

	private Types types;
	private Elements elements;
	private ClazzFactory clazzFactory;

	protected AnnotationProxyBuilder<VALJOGenerate> generateAnnotationBuilder;
	protected AnnotationProxyBuilder<VALJOConfigure> configureAnnotationBuilder;
	protected Map<String,String> configurationOptions;
	protected static final String generatedPackageName = "testPackage";
	protected static final String generatedClassName = "TestImpl";

	public TemplateTestBase() {
		super();
	}

	@Before
	public void init() throws URISyntaxException {
		types = Objects.requireNonNull(compilationRule.getTypes());
		elements = Objects.requireNonNull(compilationRule.getElements());

		generateAnnotationBuilder = new AnnotationProxyBuilder<VALJOGenerate>(VALJOGenerate.class);
		configureAnnotationBuilder = new AnnotationProxyBuilder<VALJOConfigure>(VALJOConfigure.class);

		clazzFactory = Objects.requireNonNull(ClazzFactory.getInstance());

		configurationOptions = new HashMap<String,String>();
	}

	protected String produceOutput(Class<?> sourceClass) {
		return produceOutput(sourceClass, generateAnnotationBuilder.add(ConfigurationOptionKeys.name, generatedPackageName+"."+generatedClassName).build(), configureAnnotationBuilder.build());
	}

	protected String produceOutput(Class<?> sourceClass, VALJOGenerate generateAnnotation, VALJOConfigure configureAnnotation) {
		Configuration configuration = new Configuration(generateAnnotation, configureAnnotation, Locale.ENGLISH, configurationOptions);

		Clazz clazz = clazzFactory.createClazz(types, elements, elements.getTypeElement(sourceClass.getCanonicalName()), configuration, e -> { throw new RuntimeException(e); });

		STCodeWriter codeWriter = new STCodeWriter(failureMsg -> Assert.fail(failureMsg));

		String output = codeWriter.outputClass(clazz, configuration);

		Assert.assertNotNull("template output should not be null", output);

	    output=output.replace('\r', ' ').replace('\n', ' ');
	    output=output.replaceAll("\\s+", " ");
	    output=output.trim();

		return output;
	}
}