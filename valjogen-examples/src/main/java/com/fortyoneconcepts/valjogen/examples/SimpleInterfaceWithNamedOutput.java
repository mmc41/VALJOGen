/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows how to control the name of the generated implementation class.
*/
@VALJOGenerate(name="SimpleInterfaceImpl", comment="Example 2")
public interface SimpleInterfaceWithNamedOutput
{
    public Object getObject();
    public String getString();
}
