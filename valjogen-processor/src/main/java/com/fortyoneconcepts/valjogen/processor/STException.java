/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

/**
 * Exception throw if StringTemplate engine run into trouble.
 *
 * @author mmc
 */
public class STException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public STException(String message) {
		super(message);
	}

	public STException(String message, Throwable cause) {
		super(message, cause);
	}
}
