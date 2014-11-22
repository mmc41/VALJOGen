/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface ExternalizableSemiMutableInterface extends java.io.Externalizable
{
	public byte getByte();
	public void setByte(byte value);

	public int getInt();
}
