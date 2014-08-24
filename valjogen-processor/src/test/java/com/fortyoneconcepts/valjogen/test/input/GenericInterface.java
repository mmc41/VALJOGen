package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface GenericInterface<GT,ST,OT> // Wildcards like <GT extends java.io.Serializable, ST extends CharSequence, OT> not yet supported.
{
	public GT getGt();

	public ST getSt();

	public OT getOt();
}
