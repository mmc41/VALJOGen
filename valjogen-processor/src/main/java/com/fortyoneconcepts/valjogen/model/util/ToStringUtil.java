package com.fortyoneconcepts.valjogen.model.util;

import java.util.Collection;
import java.util.stream.Collectors;

public final class ToStringUtil
{
 public static String toRefsString(Collection<?> c)
 {
	 if (c==null)
		 return "null";
	 else return "["+c.stream().map(p -> "@"+Integer.toHexString(System.identityHashCode(p))).collect(Collectors.joining(", "))+"]";
 }
}
