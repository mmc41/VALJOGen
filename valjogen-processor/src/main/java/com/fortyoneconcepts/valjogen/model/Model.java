/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;

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
	 * @return The configuration object used for codegeneration with this model.
	 */
	public Configuration getConfiguration();

	/**
	 * The package that the output belongs to.
	 *
	 * @return The package name that should be used when generating code for this model.
	 */
	public String getPackageName();

	/**
	 * True if the output has no package.
	 *
	 * @return true if the package that shoulde be used when generating code for this model is a default pacakge.
	 */
	public default boolean isInDefaultPackage()
	{
		return getPackageName().isEmpty();
	}

	/**
	 * Get the (basic) class that this model is used in. Can be the generated class or another class the generated class depend on.
	 *
	 * @return The class that this model is a part of.
	 */
	public BasicClazz getClazz();

	/**
	 * Get the class that is being generated - the 'root' class of all basic clazzes in the dependency graph.
	 *
	 * @return The generated class.
	 */
	public default Clazz getGeneratedClazz()
	{
		return getClazz().getGeneratedClazz();
	}

	/**
	 * Prefix for (auto-generated) local variables.
	 *
	 * @return The prefix that should be used for local variables in methods.
	 */
	public default String getVariablesPrefix()
	{
		return getConfiguration().getSuggestedVariablesPrefix();
	}

	/**
	 * Helper method for printing out the model nicely without getting into problems with its circular references.
	 *
	 * @param writer The writer to print to
	 * @param level The curren recursion level. Call with 0 initially.
	 */
	public void print(IndentedPrintWriter writer, int level);
}
