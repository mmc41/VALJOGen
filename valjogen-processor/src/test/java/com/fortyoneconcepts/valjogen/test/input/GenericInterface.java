/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.Map;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface GenericInterface<GT,ST,OT> // Wildcards like <GT extends java.io.Serializable, ST extends CharSequence, OT> not yet supported.
{
	public GT getGt();
	public void setGt(GT arg0); // Mutable setter.

	public ST getSt();
	public GenericInterface<GT,ST,OT> setSt(ST arg0); // Immutable setter.

	public OT getOt();
	public GenericInterface<GT,ST,OT> setOt(OT arg0); // Immutable setter;
}
