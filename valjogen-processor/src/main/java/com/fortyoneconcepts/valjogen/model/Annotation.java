/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Objects;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;

/**
 * Information about an annotation. Currently without any details - just the string content.
 *
 * TODO: Implement as having a type and a value map.
 *
 * @author mmc
 */
public class Annotation extends ModelBase
{
	private BasicClazz clazz;
	private String annotationCodeString;

	public Annotation(BasicClazz clazz, String annotationCodeString)
	{
		this.clazz=Objects.requireNonNull(clazz);
		this.annotationCodeString=Objects.requireNonNull(annotationCodeString);
	}

	@Override
	public Configuration getConfiguration()
	{
		return clazz.getConfiguration();
	}

	@Override
	public String getPackageName()
	{
		return clazz.getPackageName();
	}

	@Override
	public BasicClazz getClazz()
	{
		return clazz;
	}

	public String getCode()
	{
		return annotationCodeString;
	}

	@Override
	public void print(IndentedPrintWriter writer, int detailLevel)
	{
		if (detailLevel>=MAX_RECURSIVE_LEVEL) {
			writer.print(annotationCodeString+" ");
			return;
		}

		if (detailLevel>0)
			writer.increaseIndent();

		writer.ensureNewLine();

		writer.print(this.getClass().getSimpleName()+"(annotationString="+annotationCodeString);

		printExtraTop(writer, detailLevel);

		printExtraBottom(writer, detailLevel);

		writer.println(")");

		if (detailLevel>0)
			writer.decreaseIndent();
	}
}
