/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests.util;

import java.util.function.Supplier;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Add dynamicly created message header to JUnit output in case of failure.
 *
 * @author mmc
 */
public final class WarningOnJUnitErrorRule implements TestRule
{
	private Supplier<String> errorMsgHeaderSupplier;

	public WarningOnJUnitErrorRule() {}

	public void setErrorMsgHeader(Supplier<String> errorMsgHeaderSupplier)
	{
		this.errorMsgHeaderSupplier=errorMsgHeaderSupplier;
	}

	@Override
	public Statement apply(final Statement base, Description description)
	{
		return new Statement()
		{
			@Override
			public void evaluate() throws Throwable
			{
				try {
					base.evaluate();
				} catch (Throwable e) {
				    if (errorMsgHeaderSupplier!=null)
				    	System.err.println(errorMsgHeaderSupplier.get());

					throw e;
				}
			}
		};
	}
}