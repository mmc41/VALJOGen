package com.fortyoneconcepts.valjogen.model.util;

import java.util.Collection;
import java.util.stream.Collectors;

import com.fortyoneconcepts.valjogen.model.ModelBase;

public final class ToStringUtil
{
 public static String toRefsString(Collection<?> c)
 {
	 if (c==null)
		 return "null";
	 else return "["+c.stream().map(p -> "@"+Integer.toHexString(System.identityHashCode(p))).collect(Collectors.joining(", "))+"]";
 }

 public static String toString(Collection<? extends ModelBase> models, String seperator, int level)
 {
	 if (models==null)
		 return "null";
	 else return "["+models.stream().map(t -> t.toString(level)).collect(Collectors.joining(seperator))+"]";
 }
}
