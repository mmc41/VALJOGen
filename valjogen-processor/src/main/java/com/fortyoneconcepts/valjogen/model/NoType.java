/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

import java.util.Objects;

import com.fortyoneconcepts.valjogen.model.util.IndentedPrintWriter;

/**
 * Specifies a non-exisisting type. Type of nothing.
 *
 * @author mmc
 *
 */
public class NoType extends ObjectType
{
	public NoType()
	{
		super(null, "");
	}

	/**
     * Nb. Post-constructor for this type. This method must be called for the type to be fully initialized.
     *
	 * @param clazzUsingType The class using this type.
     */
	public void init(BasicClazz clazzUsingType)
	{
		this.clazzUsingType=Objects.requireNonNull(clazzUsingType);
		this.initializedType=true;
	}

	@Override
	public TypeCategory getTypeCategory()
	{
		return TypeCategory.NONE;
	}

	@Override
	public boolean isInImportScope()
	{
		return true;
	}

	@Override
	public void print(IndentedPrintWriter writer, int detailLevel)
	{
		writer.print("NoType ");
	}
}
