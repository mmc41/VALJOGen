/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.integrationtests.util;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Add message header to JUnit output in case of failure.
 *
 * @author mmc
 */
public final class WarningOnJUnitErrorRule implements TestRule
{
	private String errorMsgHeader;

	public WarningOnJUnitErrorRule() {}

	public void setErrorMsgHeader(String errorMsgHeader)
	{
		this.errorMsgHeader=errorMsgHeader;
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
				    if (errorMsgHeader!=null)
				    	System.err.println(errorMsgHeader);

					throw e;
				}
			}
		};
	}
}