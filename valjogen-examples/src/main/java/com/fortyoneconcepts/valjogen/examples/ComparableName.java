/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate
@VALJOConfigure(extraInterfaceNames={ "java.lang.Comparable<$(This)>" }, comparableMembers= {"lastName", "firstName" })
public interface ComparableName
{
	public String getFirstName();
	public String getLastName();
}
