/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to make generated class implement comparable in a specific way. As we can't refer directly to the generated class directly from our interface
* we need to specify the implmentation with it's generic reference as an extra interface that the generated class should implement (in addition to this
* main interface). Extra interfaces are resolved only when the implementation is generated. Notice the use of the <code>This</code> macro so we do not have to
* hardcode the implementation class name. Notice also how a certain ordering for the compareTo implementation is specified (instead of the default
* which is in order of declaration of the properties in the interface).
*/
@VALJOGenerate
@VALJOConfigure(extraInterfaceNames={ "java.lang.Comparable<$(This)>" }, comparableMembers= {"lastName", "firstName" }, comment="Example 6")
public interface ComparableName
{
	public String getFirstName();
	public String getLastName();
}
