/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import static com.fortyoneconcepts.valjogen.model.util.NamesUtil.getUnqualifiedName;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * StringTemplate utilities.
 *
 * @author mmc
 */
public final class STUtil
{
	public static String getConstructorTemplateName(String methodName)
	{
		return "method_this";
	}

	public static String getUnTypedTemplateName(String methodName)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("method_");
		sb.append(methodName);

		return sb.toString();
	}

	public static String getTypedTemplateName(String methodName, Stream<String> parameterTypeNames)
	{
		StringBuilder sb = new StringBuilder();

		sb.append("method_");
		sb.append(methodName);

		String parametersStr = (parameterTypeNames.map(p -> {
			  return getUnqualifiedName(p);
		}).collect(Collectors.joining("_")));

		if (!parametersStr.isEmpty()) {
			sb.append("_");
			sb.append(parametersStr);
		}

		return sb.toString();
	}

	public static String getPropertyTemplateName(String methodName)
	{
		return "property";
	}
}
