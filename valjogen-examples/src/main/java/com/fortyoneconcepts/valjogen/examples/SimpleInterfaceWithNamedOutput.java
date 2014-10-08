/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

@VALJOGenerate(name="SimpleInterfaceImpl")
public interface SimpleInterfaceWithNamedOutput
{
    public Object getObject();
    public String getString();
}
