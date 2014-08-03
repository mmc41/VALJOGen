/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Consumer;

import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.*;

import com.fortyoneconcepts.valjogen.model.*;

public final class STCodeWriter
{
	private static final char delimiterStartChar = '<';
	private static final char delimiterStopChar = '>';

	private Consumer<String> errorConsumer;

	public STCodeWriter(Consumer<String> errorConsumer) {
		this.errorConsumer = Objects.requireNonNull(errorConsumer);
	}

	public String outputClass(Clazz clazz, Configuration cfg)
	{
		String result = null;

		try {
			STGroup group = new STGroupFile("templates/main.stg", delimiterStartChar, delimiterStopChar);

			group.registerModelAdaptor(Model.class, new CustomSTModelAdaptor());

			group.setListener(new ErrorListener());

			ST st = group.getInstanceOf("class");

			st.add("clazz", clazz);

			result = st.render(cfg.getLocale(), cfg.getLineWidth());

			System.out.println("Rendered "+result);

		} catch (Throwable e) {
			errorConsumer.accept(e.getMessage());
		}

		return result;
	}

	private final class ErrorListener implements STErrorListener  {
		private String reportSTMsg(STMessage msg) {
			return msg.toString();
			//return String.format(msg.error.message, msg.arg, msg.arg2, msg.arg3);
		}

		@Override
		public void runTimeError(STMessage msg) {
			errorConsumer.accept(reportSTMsg(msg));
		}

		@Override
		public void compileTimeError(STMessage msg) {
			errorConsumer.accept(reportSTMsg(msg));
		}

		@Override
		public void IOError(STMessage msg) {
			errorConsumer.accept(reportSTMsg(msg));
		}

		@Override
		public void internalError(STMessage msg) {
			errorConsumer.accept(reportSTMsg(msg));
		}
	}
}
