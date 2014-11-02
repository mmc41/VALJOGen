package com.fortyoneconcepts.valjogen.model;

import java.util.*;
import org.junit.*;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;
import com.fortyoneconcepts.valjogen.model.util.AnnotationProxyBuilder;
import com.fortyoneconcepts.valjogen.processor.TemplateKind;

import static org.mockito.Mockito.*;

/**
 * Tests for abstract Type.
 *
 * @author mmc
 */
public class ClazzTest
{
	private static final String sourceElementName ="ITest";
	private Configuration cfg;


	@Before
	public void init() {
		cfg = new Configuration(sourceElementName, new AnnotationProxyBuilder<VALJOGenerate>(VALJOGenerate.class).build(), Locale.ENGLISH, Collections.emptyMap());
	}

	@Test
	public void testIsOfType() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertTrue(clazz.isOfType("java.lang.Comparable"));
	}

	@Test
	public void testIsNotOfType() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertFalse(clazz.isOfType("java.lang.Collection"));
	}

	@Test
	public void testHasInstanceMember() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertTrue(clazz.hasInstanceMember("testInstanceMember"));
	}

	@Test
	public void testHasNotInstanceMember() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertFalse(clazz.hasInstanceMember("noMember"));
		Assert.assertFalse(clazz.hasInstanceMember("testStaticMember"));
	}

	@Test
	public void testHasStaticMember() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertTrue(clazz.hasStaticMember("testStaticMember"));
	}

	@Test
	public void testHasNotStaticMember() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertFalse(clazz.hasStaticMember("noMember"));
		Assert.assertFalse(clazz.hasStaticMember("testInstanceMember"));
	}

	@Test
	public void testHasInstanceMethod() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertTrue(clazz.hasInstanceMethod("compareTo(Testclass)"));
	}

	@Test
	public void testHasNotInstanceMethod() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertFalse(clazz.hasInstanceMethod("noMethod"));
		Assert.assertFalse(clazz.hasInstanceMethod("staticCompareTo(Testclass)"));
	}

	@Test
	public void testHasStaticMethod() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertTrue(clazz.hasStaticMethod("staticCompareTo(Testclass)"));
	}

	@Test
	public void testHasNotStaticMethod() {
		ComparableTestClazz clazz = new ComparableTestClazz("testpackage.Testclass");

		Assert.assertFalse(clazz.hasStaticMethod("noMethod"));
		Assert.assertFalse(clazz.hasStaticMethod("compareTo(Testclass)"));
	}

    /*
     *  Test class that extends Comparable
     */
	private final class ComparableTestClazz extends Clazz
	{
		public ComparableTestClazz(String name)
		{
			super(cfg, name, sourceElementName, "", "", (c) -> mock (HelperTypes.class));

			ObjectType baseClazz = new ObjectType(this, "java.lang.Object");
			baseClazz.initType(baseClazz, Collections.emptyList(), Collections.emptySet(), Collections.emptyList());

			ObjectType comparableClazz = new ObjectType(this, "java.lang.Comparable");
			comparableClazz.initType(baseClazz, Collections.emptyList(), Collections.emptySet(), Collections.emptyList());

			List<Type> interfaceTypes = Arrays.asList(comparableClazz);
			Set<Type> ancestorTypes = new HashSet<Type>(interfaceTypes);
			ancestorTypes.add(baseClazz);

			initType(baseClazz, interfaceTypes, ancestorTypes, Collections.emptyList());

			Member instanceMember = new Member(this, new PrimitiveType(this, "int"), "testInstanceMember", EnumSet.of(Modifier.PUBLIC));
			Member staticMember = new Member(this, new PrimitiveType(this, "int"), "testStaticMember", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC));
			Method instanceMethod = new Method(this, comparableClazz, "compareTo", new PrimitiveType(this, "int"), Arrays.asList(new Parameter(this, this, "other", EnumSet.of(Modifier.PUBLIC))), Collections.emptyList(), "", EnumSet.of(Modifier.PUBLIC), ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT, TemplateKind.TYPED);
			Method staticMethod = new Method(this, comparableClazz, "staticCompareTo", new PrimitiveType(this, "int"), Arrays.asList(new Parameter(this, this, "other", EnumSet.of(Modifier.PUBLIC))), Collections.emptyList(), "", EnumSet.of(Modifier.PUBLIC, Modifier.STATIC), ImplementationInfo.IMPLEMENTATION_PROVIDED_BY_THIS_OBJECT, TemplateKind.TYPED);

			initContent(Arrays.asList(instanceMember, staticMember), Collections.emptyList(), Arrays.asList(instanceMethod, staticMethod), Collections.emptyList(), Collections.emptyList(), EnumSet.of(Modifier.PUBLIC));
		}
	}
}
