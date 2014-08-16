/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.Objects;
import java.util.function.Consumer;

import org.stringtemplate.v4.*;
import org.stringtemplate.v4.misc.*;

import com.fortyoneconcepts.valjogen.model.*;

/**
 * Controller for StringTemplate 4 templates groups. Calls into the main.stg
 * template.
 *
 * @author mmc
 */
public final class STCodeWriter
{
	private static final String mainTemplateFile = "templates/main.stg";
	private static final String mainTemplate = "class";
	private static final String mainTemplateArg = "clazz";

	private static final char delimiterStartChar = '<';
	private static final char delimiterStopChar = '>';

	private final Consumer<String> errorConsumer;

	public STCodeWriter(Consumer<String> errorConsumer) {
		this.errorConsumer = Objects.requireNonNull(errorConsumer);
	}

	public String outputClass(Clazz clazz, Configuration cfg)
	{
		String result = null;

		try {
			STGroup group = new STGroupFile(mainTemplateFile, delimiterStartChar, delimiterStopChar);

			group.registerModelAdaptor(Model.class, new CustomSTModelAdaptor());

			group.setListener(new ErrorListener());

			ST st = group.getInstanceOf(mainTemplate);

			st.add(mainTemplateArg, Objects.requireNonNull(clazz));

			result = st.render(Objects.requireNonNull(cfg).getLocale(), cfg.getLineWidth());

			// System.out.println("Rendered "+result);

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
