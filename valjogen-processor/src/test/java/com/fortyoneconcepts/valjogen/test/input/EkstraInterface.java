package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate(name="ExtraImpl")
// TODO: Re-enable when generated class is no longer abstract.
// @VALJOConfigure(extraInterfaceNames={ "java.io.Serializable", "java.lang.Comparable<EkstraInterface>", "com.fortyoneconcepts.valjogen.test.input.InterfaceWithoutAnnotation" })
public interface EkstraInterface
{
	public int getIntValue();
	public EkstraInterface setIntValue(int intValue);
}
