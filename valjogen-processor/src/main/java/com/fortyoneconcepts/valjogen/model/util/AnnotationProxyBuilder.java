/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
* A fluent builder that creates a new instance of our annotation with defaults (works also with source-only annotations at runtime).
*
* Use add(attribute-method-name, value) to configure values on the attribute followed by the build method to generate an annotation instance.
*
* @author mmc
* @param <A> The type of the annotation that this builder should construct
*/
public final class AnnotationProxyBuilder<A extends Annotation> implements InvocationHandler
{
	private final Map<String,Object> valuesByMethodName;
	private final Class<A> annotation;

	/***
	 * Constructs a builder for the specified annotation.
	 *
	 * @param annotation The type of the annotation that this builder should construct.
	 */
	public AnnotationProxyBuilder(Class<A> annotation)
	{
		this.valuesByMethodName = Collections.emptyMap();
		this.annotation = annotation;
	}

	private AnnotationProxyBuilder(Class<A> annotation, Map<String,Object> valuesByMethodName)
	{
		this.valuesByMethodName = valuesByMethodName;
		this.annotation = annotation;
	}

	/***
	 * The annotation instance (a proxy).
	 *
	 * @return Annotation with filled-in configuration.
	 */
	@SuppressWarnings("unchecked")
	public A build()
	{
	  return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation }, this);
	}

	/***
	 * Configures the value that a given method on the annotation should return.
	 *
	 * @param methodName Name of the annotation method
	 * @param value The value that the annotation method should return
	 * @return A new builder instance.
	 * @throws IllegalArgumentException if methodName is not declared on the attribute
	 */
	public AnnotationProxyBuilder<A> add(String methodName, Object value)
	{
		if (!Arrays.stream(annotation.getMethods()).anyMatch(m -> m.getName().equals(methodName)))
			throw new IllegalArgumentException("No annotation method named '"+methodName+"' on "+annotation.getName());

		Map<String,Object> newValuesByMethodName = new HashMap<String,Object>(valuesByMethodName);
		newValuesByMethodName.put(methodName, value);

		return new AnnotationProxyBuilder<A>(annotation, newValuesByMethodName);
	}

	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
	   Object value = valuesByMethodName.get(method.getName());
	   if (value!=null)
		   return value;
	   else return method.getDefaultValue();
	}
}

