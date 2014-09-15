/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.Map;
import java.util.Set;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface InterfaceWithGenericMembers
{
	public Map<String,Object> getMap();
	public void setMap(Map<String,Object> arg0);// Mutable setter.

	public Set<String> getSet();
	public InterfaceWithGenericMembers setSet(Set<String> arg0);// Immutable setter.
}
