package com.fortyoneconcepts.valjogen.model;

import org.junit.*;

import static org.mockito.Mockito.*;

/**
 * Tests for abstract Type.
 *
 * @author mmc
 */
public class TypeTest
{
	private BasicClazz clazz;

	@Before
	public void init() {
		clazz = mock(BasicClazz.class);
	}

	@Test
	public void testIsOfType() {
		Type type = new TestType("java.util.Locale");

		Assert.assertTrue(type.isOfType("java.util.Locale"));
		Assert.assertTrue(type.isOfType("java.lang.Cloneable"));
		Assert.assertTrue(type.isOfType("java.io.Serializable"));
		Assert.assertTrue(type.isOfType("java.lang.Object"));
	}

	@Test
	public void testNotOfType() {
		Type type = new TestType("java.util.Locale");

		Assert.assertFalse(type.isOfType("java.util.Collection"));
	}

	@Test
	public void testHasInstanceMethod() {
		Type type = new TestType("java.lang.Comparable");

		Assert.assertTrue(type.hasInstanceMethod("compareTo(Object)"));
	}

	@Test
	public void testHasInstanceMethodWithWildcards()
	{
		Type type = new TestType("java.lang.Comparable");

		Assert.assertTrue(type.hasInstanceMethod("compareTo(*)"));
	}

	@Test
	public void testNotHasInstanceMethod() {
		Type type = new TestType("java.lang.Comparable");

		Assert.assertFalse(type.hasInstanceMethod("compareTo(int)"));
	}

	@Test
	public void testHasStaticMember()
	{
		Type type = new TestType("java.util.Locale");

		Assert.assertTrue(type.hasStaticMember("ENGLISH"));
	}

	@Test
	public void testHasInstanceMember()
	{
		Type type = new TestType(ClassWithPublicMember.class.getName());

		Assert.assertTrue(type.hasInstanceMember("test"));
	}

	@Test
	public void testHasStaticMethod()
	{
		Type type = new TestType("java.util.Locale");

		Assert.assertTrue(type.hasStaticMethod("getDefault()"));
	}

	private final class ClassWithPublicMember
	{
		@SuppressWarnings("unused")
		public int test;
	}

    // Helper non-abstract replacement for Type that we can test on (mockito stubs won't call our ctr so they do not work well).
	private final class TestType extends Type
	{
		public TestType(String name)
		{
			super(clazz, name);
		}

		@Override
		public String toString(int level) {
			return this.getQualifiedName();
		}

		@Override
		public Type copy(BasicClazz optClazzUsingType) {
			throw new java.lang.UnsupportedOperationException();
		}

		@Override
		public boolean isInImportScope() {
			return true;
		}

		@Override
		public TypeCategory getTypeCategory() {
			return TypeCategory.OBJECT;
		}
	}
}
