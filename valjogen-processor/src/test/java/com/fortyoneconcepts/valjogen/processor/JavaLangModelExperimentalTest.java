package com.fortyoneconcepts.valjogen.processor;

import java.net.URISyntaxException;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import com.fortyoneconcepts.valjogen.test.input.AbstractComparableBaseClass;
import com.google.testing.compile.CompilationRule;

/**
 * Used for experimentation with the javax.lang.model api. Not an actual test.
 *
 * @author mmc
 */
@SuppressWarnings("unused")
public class JavaLangModelExperimentalTest
{
	@Rule
	public CompilationRule compilationRule = new CompilationRule();

	private Types types;
	private Elements elements;

	@Before
	public void init() throws URISyntaxException {
		types = Objects.requireNonNull(compilationRule.getTypes());
		elements = Objects.requireNonNull(compilationRule.getElements());
	}
	@Test
	public void extractGenerics() throws Exception
	{
		String className = AbstractComparableBaseClass.class.getName();
		TypeElement element = elements.getTypeElement(className);

		TypeMirror elementType = element.asType();
		DeclaredType declaredType = (DeclaredType)elementType;

		Assert.assertEquals("wrong type", AbstractComparableBaseClass.class.getName(), declaredType.toString());

		ExecutableElement executableElement = declaredType.asElement().getEnclosedElements().stream().filter(m -> m.getKind()==ElementKind.METHOD).map(m -> (ExecutableElement)m).filter(m -> m.getSimpleName().toString().contains("getStrListField")).findFirst().get();

		TypeMirror returnType = executableElement.getReturnType();

		Assert.assertEquals("wrong type", "java.util.List<java.lang.String>", returnType.toString());
	}
}
