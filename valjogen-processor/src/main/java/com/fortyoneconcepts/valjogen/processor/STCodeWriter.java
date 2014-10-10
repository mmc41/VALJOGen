/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.Date;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.stringtemplate.v4.*;
import org.stringtemplate.v4.gui.STViz;
import org.stringtemplate.v4.misc.*;

import com.fortyoneconcepts.valjogen.model.*;

/**
 * Controller for StringTemplate 4 templates groups. Calls into the main.stg
 * template.
 *
 * Instancess class is NOT multi-thread safe across threads. Individual instances are needed for each thread.
 *
 * @author mmc
 */
public final class STCodeWriter
{
	private final static Logger LOGGER = Logger.getLogger(STCodeWriter.class.getName());

	private static final String mainTemplate = "class";
	private static final String mainTemplateArg = "clazz";

	private RuntimeException lastException;
	private final Clazz clazz;
	private final STTemplates stTemplates;
	private final Configuration cfg;

	public STCodeWriter(Clazz clazz, Configuration cfg, STTemplates stTemplates)
	{
		this.stTemplates=stTemplates;
		this.clazz=clazz;
		this.cfg=cfg;
		this.lastException=null;
	}

	public String outputClass() throws Exception
	{
		lastException = null;

		String result = null;

		STGroup.verbose = LOGGER.isLoggable(Level.FINE);
		STGroup.trackCreationEvents = cfg.isDebugStringTemplatesEnabled();

		STGroup group = stTemplates.getSTGroup();

		group.registerModelAdaptor(Model.class, new STCustomModelAdaptor());

		group.registerRenderer(Date.class, new STISODateRender());

		group.setListener(new ErrorListener());

		ST st = group.getInstanceOf(mainTemplate);

		st.add(mainTemplateArg, Objects.requireNonNull(clazz));

		result = st.render(Objects.requireNonNull(cfg).getLocale(), cfg.getLineWidth());
		if (result==null)
			throw new STException("Template rendering error : No output");

		// Unfortunately, ST does not propagate exceptions so we have to do that.
		Exception exception = lastException;
		if (exception!=null)
			throw exception;

		if (cfg.isDebugStringTemplatesEnabled())
		{
			LOGGER.warning(() -> "Showing STViz - Pausing code generation until STViz is closed...");

			STViz viz = st.inspect();
			viz.waitForClose();
		}

		return result;
	}

	private final class ErrorListener implements STErrorListener  {
		private String reportSTMsg(STMessage msg) {
			return msg.toString();
		}

		@Override
		public void runTimeError(STMessage msg) {
			throw (lastException = new STException(reportSTMsg(msg)));
		}

		@Override
		public void compileTimeError(STMessage msg) {
			throw (lastException = new STException(reportSTMsg(msg)));
		}

		@Override
		public void IOError(STMessage msg) {
			throw (lastException = new STException(reportSTMsg(msg)));
		}

		@Override
		public void internalError(STMessage msg) {
			throw (lastException = new STException(reportSTMsg(msg)));
		}
	}
}
