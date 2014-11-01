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

	public static String templateNameToMethodName(String templateName)
	{
		StringBuilder sb = new StringBuilder();

		boolean first_underscore=true;
		for (int i=0; i<templateName.length(); ++i)
		{
			char ch = templateName.charAt(i);
			if (ch=='_')
			{
				if (first_underscore) {
					sb.append('(');
					first_underscore=false;
				} else sb.append(',');
			} else sb.append(ch);
		}

		if (first_underscore)
			sb.append('(');
		sb.append(')');

		return sb.toString();
	}
}
