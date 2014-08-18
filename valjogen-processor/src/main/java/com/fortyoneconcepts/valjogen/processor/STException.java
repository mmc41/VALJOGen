package com.fortyoneconcepts.valjogen.processor;

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
