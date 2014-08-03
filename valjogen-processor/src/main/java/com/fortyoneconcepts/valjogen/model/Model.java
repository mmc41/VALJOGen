/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Common interface for all model elements.
 *
 * @author mmc
 */
public interface Model
{
	/**
	 * The configuration object for this model.
	 *
	 * @return
	 */
	public Configuration getConfiguration();

	/**
	 * The package that the output belongs to.
	 *
	 * @return
	 */
	public String getPackageName();

	public Clazz getClazz();

	/***
	 * True if the output has no package.
	 * @return
	 */
	public default boolean isInDefaultPackage()
	{
		return getPackageName().isEmpty();
	}

	public default String getVariablesPrefix()
	{
		 return getConfiguration().getSuggestedVariablesPrefix();
	}
}
