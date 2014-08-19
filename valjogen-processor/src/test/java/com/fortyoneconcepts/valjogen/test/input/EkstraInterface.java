package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
@VALJOConfigure(extraInterfaceNames={ "java.io.Serializable", "com.fortyoneconcepts.valjogen.test.input.InterfaceWithoutAnnotation" })
public interface EkstraInterface
{
	public int getIntValue();
	public EkstraInterface setIntValue(int intValue);
}
