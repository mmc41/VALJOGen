/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.Date;
import java.util.Objects;

import org.stringtemplate.v4.*;
import org.stringtemplate.v4.gui.STViz;
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

		STGroup.verbose = cfg.isDebugInfoEnabled() && cfg.isVerboseInfoEnabled();
		STGroup.trackCreationEvents = cfg.isDebugShowingSTVizGuiExplorerEnabled();

		group.registerModelAdaptor(Model.class, new STCustomModelAdaptor());

		group.registerRenderer(Date.class, new STISODateRender());

		group.setListener(new ErrorListener());

		ST st = group.getInstanceOf(mainTemplate);

		st.add(mainTemplateArg, Objects.requireNonNull(clazz));

		result = st.render(Objects.requireNonNull(cfg).getLocale(), cfg.getLineWidth());

		if (cfg.isDebugShowingSTVizGuiExplorerEnabled()) {
			System.out.println("Showing STViz - Pausing code generation until STViz is closed...");

			STViz viz = st.inspect();
			viz.waitForClose();
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
