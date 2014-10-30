/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.test.input;

import java.util.Collection;
import java.util.List;

import com.fortyoneconcepts.valjogen.annotations.*;

@VALJOGenerate
public interface ContainsSpecialTypesInterface extends InterfaceWithoutAnnotation
{
	public Comparable<ContainsSpecialTypesInterface> getComparable();
	public void setComparable(Comparable<ContainsSpecialTypesInterface> comparable);

	public String getString();
	public void setString(String string);

	public String[] getStringArray();
	public void setStringArray(String[] stringArray);

	public Collection<String> getStringCollection();
	public void setStringCollection(Collection<String> stringList);

	public List<String> getStringList();
	public void setStringList(List<String> stringList);
}


