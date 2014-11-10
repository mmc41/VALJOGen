/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests.util;

import java.io.*;

public final class SerializationUtil
{
	public static Serializable read(byte[] serializedData) throws IOException, ClassNotFoundException
	{
		ByteArrayInputStream is = new ByteArrayInputStream(serializedData);
		ObjectInputStream oi = null;
		Serializable value;

		try {
			oi = new ObjectInputStream(is);
			value = (Serializable) oi.readObject();
		} finally {
			if (oi != null)
				oi.close();
		}

		return value;
	}

	public static byte[] write(Serializable value) throws IOException
	{
		ObjectOutput oo = null;
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(os);
			oo.writeObject(value);
			return os.toByteArray();
		} finally {
			if (oo != null)
				oo.close();
		}
	}

}
