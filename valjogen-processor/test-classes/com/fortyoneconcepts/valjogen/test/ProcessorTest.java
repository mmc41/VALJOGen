package com.fortyoneconcepts.valjogen.test;

import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static org.truth0.Truth.ASSERT;

import java.io.FileNotFoundException;
import java.net.URL;

import org.junit.Test;

import com.fortyoneconcepts.valjogen.processor.AnnotationProcessor;
import com.fortyoneconcepts.valjogen.test.input.InterfaceAllTypesWithDefaultsAndReservedWords;
import com.google.testing.compile.*;

public class ProcessorTest
{
	private static String imports = "import com.fortyoneconcepts.valjogen.annotations.*;";
	public String onePropertyTestSource = imports+" @GenerateVALJO public interface OnePropertyTest { public int getX(); }";

	public String resourcePath = InterfaceAllTypesWithDefaultsAndReservedWords.class.getPackage().getName().replace(".", "/")+"/";

	private URL getResourcePath(Class<?> clazz) throws FileNotFoundException
	{
		String clazzSourceFile = clazz.getName().replace(".", "/").concat(".java");
		URL url = clazz.getClassLoader().getResource(clazz.getName().replace(".", "/").concat(".java"));
		if (url==null)
			throw new FileNotFoundException(clazzSourceFile.toString());
		return url;
	}


	@Test
	public void test()
	{
		 ASSERT.about(javaSource())
		 .that(JavaFileObjects.forSourceString("OnePropertyTest", onePropertyTestSource))
		 .processedWith(new AnnotationProcessor())
		 .compilesWithoutError();
	}

	@Test
	public void testJava() throws FileNotFoundException
	{
		 ASSERT.about(javaSource())
		 .that(JavaFileObjects.forResource(getResourcePath(InterfaceAllTypesWithDefaultsAndReservedWords.class)))
		 .processedWith(new AnnotationProcessor())
		 .compilesWithoutError();
	}

	/*
	 *
	//@Rule public CompilationRule compilationRule = new CompilationRule();
	 *
	@Test
	public void test2()
	{
		JavaSourceSubjectFactory sources = JavaSourceSubjectFactory.javaSource();

		JavaFileObject javaObject = JavaFileObjects.forSourceString("OnePropertyTest", onePropertyTestSource);

		javaObject.

		sources.

	}
	**/

}
