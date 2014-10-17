/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.Date;
import java.util.Locale;

import org.stringtemplate.v4.AttributeRenderer;

/**
 * ST render that knows how to output dates.
 *
 * @author mmc
 */
public class STISODateRender implements AttributeRenderer
{
    public STISODateRender()
    {
    }

    @Override
	public String toString(Object date, String formatString, Locale locale)
	{
		if (!(date instanceof Date))
			throw new IllegalArgumentException("Object argument should be a date");

		return String.format("%tFT%<tRZ", (Date)date);
	}
}