/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.Objects;

import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

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

	public STCodeWriter() {}

	public String outputClass(Clazz clazz, Configuration cfg) throws Exception
	{
		String result = null;

		STGroup group = new STGroupFile(mainTemplateFile, delimiterStartChar, delimiterStopChar);

		group.registerModelAdaptor(Model.class, new CustomSTModelAdaptor());

		group.setListener(new ErrorListener());

		ST st = group.getInstanceOf(mainTemplate);

		st.add(mainTemplateArg, Objects.requireNonNull(clazz));

		result = st.render(Objects.requireNonNull(cfg).getLocale(), cfg.getLineWidth());

		// System.out.println("Rendered "+result);

		return result;
	}

	private final class ErrorListener implements STErrorListener  {
		private String reportSTMsg(STMessage msg) {
			return msg.toString();
			//return String.format(msg.error.message, msg.arg, msg.arg2, msg.arg3);
		}

		@Override
		public void runTimeError(STMessage msg) {
			throw new STException(reportSTMsg(msg));
		}

		@Override
		public void compileTimeError(STMessage msg) {
			throw new STException(reportSTMsg(msg));
		}

		@Override
		public void IOError(STMessage msg) {
			throw new STException(reportSTMsg(msg));
		}

		@Override
		public void internalError(STMessage msg) {
			throw new STException(reportSTMsg(msg));
		}
	}
}
