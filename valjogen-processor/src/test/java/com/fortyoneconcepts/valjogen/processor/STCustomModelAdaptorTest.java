package com.fortyoneconcepts.valjogen.processor;

import org.junit.*;
import org.stringtemplate.v4.*;

import com.fortyoneconcepts.valjogen.model.*;

import static org.mockito.Mockito.*;

/**
 * Test that the ST4 custom adapter transforms magic property methods references into corresponding helper methods on type object + normal behavior for non-magic methods still work.
 *
 * @author mmc
 */
public class STCustomModelAdaptorTest
{
	@Test
	public void testIsMagicStaticMethodCalled()
	{
		STCustomModelAdaptor adapter = new STCustomModelAdaptor();

		Type type = mock(Type.class);

		execute(adapter, type, "<type.staticMethod_BlaBla_String>");

		verify(type).hasStaticMethod("BlaBla(String)");

		type = mock(Type.class);

		execute(adapter, type, "<type.staticMethodBlaBla_String>");

		verify(type).hasStaticMethod("BlaBla(String)");
	}

	@Test
	public void testIsMagicInstanceMethodCalled()
	{
		STCustomModelAdaptor adapter = new STCustomModelAdaptor();

		Type type = mock(Type.class);

		execute(adapter, type, "<type.instanceMethod_BlaBla_int_double>");

		verify(type).hasInstanceMethod("BlaBla(int,double)");

		type = mock(Type.class);

		execute(adapter, type, "<type.instanceMethodBlaBla_int_double>");

		verify(type).hasInstanceMethod("BlaBla(int,double)");
	}

	@Test
	public void testIsMagicStaticMemberCalled()
	{
		STCustomModelAdaptor adapter = new STCustomModelAdaptor();

		Type type = mock(Type.class);

		execute(adapter, type, "<type.staticMember_BlaBla>");

		verify(type).hasStaticMember("BlaBla");

		type = mock(Type.class);

		execute(adapter, type, "<type.staticMemberBlaBla>");

		verify(type).hasStaticMember("BlaBla");
	}

	@Test
	public void testIsMagicInstanceMemberCalled()
	{
		STCustomModelAdaptor adapter = new STCustomModelAdaptor();

		Type type = mock(Type.class);

		execute(adapter, type, "<type.instanceMember_BlaBla>");

		verify(type).hasInstanceMember("BlaBla");

		type = mock(Type.class);

		execute(adapter, type, "<type.instanceMemberBlaBla>");

		verify(type).hasInstanceMember("BlaBla");
	}


	@Test
	public void testIsMagicPropertyWithSimpleNameCalled()
	{
		STCustomModelAdaptor adapter = new STCustomModelAdaptor();

		Type type = mock(Type.class);

		execute(adapter, type, "<type.ofType_Collection>");

		verify(type).isOfType("Collection");

		type = mock(Type.class);

		execute(adapter, type, "<type.ofTypeCollection>");

		verify(type).isOfType("Collection");
	}

	@Test
	public void testIsMagicPropertyWithQualifiedNameCalled()
	{
		STCustomModelAdaptor adapter = new STCustomModelAdaptor();

		Type type = mock(Type.class);

		execute(adapter, type, "<type.ofType_java_lang_Collection>");

		verify(type).isOfType("java.lang.Collection");

		type = mock(Type.class);

		execute(adapter, type, "<type.ofTypejava_lang_Collection>");

		verify(type).isOfType("java.lang.Collection");
	}

	@Test
	public void testIsNonMagicPropertyStillWorking()
	{
		STCustomModelAdaptor adapter = new STCustomModelAdaptor();

		Type type = mock(Type.class);

		execute(adapter, type, "<type.primitive>");

		verify(type).isPrimitive();
	}

	private final static void execute(STCustomModelAdaptor adapter, Type type, String templateMethodContent)
	{
		String templateStr = "test(type) ::= <%"+templateMethodContent+"%>\n";
		STGroup g = new STGroupString(templateStr);
		g.registerModelAdaptor(Type.class, adapter);
		ST template = g.getInstanceOf("test");
		template.add("type", type);
		template.render();
	}
}
