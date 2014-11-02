/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.io.StringWriter;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;

/**
 * Common base class for models
 *
 * @author mmc
 */
public abstract class ModelBase implements Model
{
	protected final static int MAX_RECURSIVE_LEVEL = 5;
	protected final static int NO_DETAILS_LEVEL = MAX_RECURSIVE_LEVEL+1;

	/**
	 * A special toString implementation that forward class to our recursive-safe toString.
	 * @return Safe string representation of the object (possibly cut-of).
	 */
	public final String toString()
	{
	   StringWriter result = new StringWriter();

	   IndentedPrintWriter p = new IndentedPrintWriter(result);
	   this.print(p, 0);
       p.flush();

	   return result.toString(); // toString(0);
	}


	public abstract void print(IndentedPrintWriter writer, int level);

	protected void printExtraTop(IndentedPrintWriter writer, int detailLevel) {}
	protected void printExtraBottom(IndentedPrintWriter writer, int detailLevel) {}
}
