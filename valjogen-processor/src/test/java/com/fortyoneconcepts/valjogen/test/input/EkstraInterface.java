package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate(name="ExtraImpl")
@VALJOConfigure(extraInterfaceNames={ "java.io.Serializable", "java.lang.Comparable<$(This)>", "com.fortyoneconcepts.valjogen.test.input.InterfaceWithoutAnnotation" })
public interface EkstraInterface
{
	public int getIntValue();
	public EkstraInterface setIntValue(int intValue);
}
