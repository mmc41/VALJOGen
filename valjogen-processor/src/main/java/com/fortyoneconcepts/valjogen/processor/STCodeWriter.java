/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.processor;

import java.util.Date;
import java.util.Objects;
import java.util.logging.Logger;

import org.stringtemplate.v4.*;
import org.stringtemplate.v4.gui.STViz;

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

	private final Clazz clazz;
	private final STTemplates stTemplates;
	private final Configuration cfg;

	public STCodeWriter(Clazz clazz, Configuration cfg, STTemplates stTemplates)
	{
		this.stTemplates=stTemplates;
		this.clazz=clazz;
		this.cfg=cfg;
	}

	public String outputClass() throws Exception
	{
		String result = null;

		STGroup group = stTemplates.getSTGroup();

		stTemplates.exceptions().clear();

		group.registerModelAdaptor(Model.class, new STCustomModelAdaptor());
		group.registerRenderer(Date.class, new STISODateRender());

		ST st = group.getInstanceOf(mainTemplate);

		try {
		  st.add(mainTemplateArg, Objects.requireNonNull(clazz));
		} catch (Exception e)
		{
	      throw new STException("Internal error loading templates : No output", e);
		}

		result = st.render(Objects.requireNonNull(cfg).getLocale(), cfg.getLineWidth());

		if (!stTemplates.exceptions().isEmpty())
			throw stTemplates.exceptions().getFirst();

		if (result==null)
			throw new STException("Template rendering error : No output");

		if (cfg.isDebugStringTemplatesEnabled())
		{
			LOGGER.warning(() -> "Showing STViz - Pausing code generation until STViz is closed...");

			STViz viz = st.inspect();
			viz.waitForClose();
		}

		return result;
	}
}
