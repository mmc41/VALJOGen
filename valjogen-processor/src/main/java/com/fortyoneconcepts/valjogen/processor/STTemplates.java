package com.fortyoneconcepts.valjogen.processor;

import java.net.URI;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.stringtemplate.v4.STErrorListener;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.misc.ErrorManager;
import org.stringtemplate.v4.misc.STMessage;

import com.fortyoneconcepts.valjogen.model.Configuration;

/**
 * Loads and holds STGroup file(s) according to configuration and offer inspection methods about the content.
 * template.
 *
 * @author mmc
 */
public final class STTemplates
{
	private final static Logger LOGGER = Logger.getLogger(STTemplates.class.getName());

	private static final String mainTemplateFile = "templates/main.stg";

	private static final String templateFilesEncoding = "UTF8";

	private static final char delimiterStartChar = '<';
	private static final char delimiterStopChar = '>';

	private static final String method_prefix="/method_";

	private final Deque<STException> stExceptions;
	private final STGroup group;
	private final Set<String> templateMethodNames;

	public STTemplates(ResourceLoader resourceLoader, Configuration cfg) throws Exception
	{
		stExceptions = new ArrayDeque<STException>();

		STGroup.verbose = LOGGER.isLoggable(Level.FINE);
		STGroup.trackCreationEvents = cfg.isDebugStringTemplatesEnabled();

		STGroup defaultGroup = new STGroupFile(mainTemplateFile, delimiterStartChar, delimiterStopChar);

		String customTemplateFileName = cfg.getCustomJavaTemplateFileName();


		if (customTemplateFileName!=null)
		{
			URI uri = resourceLoader.getFileResourceAsURL(customTemplateFileName);
			URL url = uri.toURL();

			group = new STGroupFile(url, templateFilesEncoding, delimiterStartChar, delimiterStopChar);
			group.importTemplates(defaultGroup);

			LOGGER.info(() -> "Added custom sub-template: "+customTemplateFileName+" from "+url.toString());
		} else {
			group = defaultGroup;
		}

		group.setListener(myErrorListener);

		Set<String> templateNames = getAllTemplateNames(group);

		templateMethodNames = Collections.unmodifiableSet(Collections.unmodifiableSet(templateNames.stream().filter(n -> n.startsWith(method_prefix)).map(n -> templateNameToMethodName(n)).collect(Collectors.toSet())));

        if (LOGGER.isLoggable(Level.FINE))
          for (String templateName : templateMethodNames)
		   LOGGER.fine("Found ST template implemented method name '"+templateName+"'" );
	}

	/**
	 * Return the loaded STGroup instance that can be used to generate output.
	 *
	 * @return STGroup instance
	 */
	public STGroup getSTGroup()
	{
		return group;
	}

	/**
	 * Return all template method names (build-in and custom) supplied by template group files
	 *
	 * @return List of normal template method names.
	 */
	public Set<String> getAllTemplateMethodNames()
	{
		return templateMethodNames;
	}

	/**
	 * Return list of exceptions that has occured during processing of string templates.
	 *
	 * @return List of normal template method names.
	 */
	public Deque<STException> exceptions()
	{
		return stExceptions;
	}

	private static String templateNameToMethodName(String templateName)
	{
		StringBuilder sb = new StringBuilder();

		boolean first_underscore=true;
		for (int i=method_prefix.length(); i<templateName.length(); ++i)
		{
			char ch = templateName.charAt(i);
			if (ch=='_')
			{
				if (first_underscore) {
					sb.append('(');
					first_underscore=false;
				} else sb.append(',');
			} else sb.append(ch);
		}

		if (first_underscore)
			sb.append('(');
		sb.append(')');

		return sb.toString();
	}

	private static Set<String> getAllTemplateNames(STGroup group)
	{
		HashSet<String> names = new HashSet<>(group.getTemplateNames());
		for (STGroup importGroup : group.getImportedGroups())
			names.addAll(getAllTemplateNames(importGroup));
		return names;
	}

	/**
	 * Errror listener for string templates - registers and throws exceptions.
	 */
	private final STErrorListener myErrorListener = new STErrorListener()  {
		private String reportSTMsg(STMessage msg) {
			LOGGER.severe(() -> msg.toString());
			return msg.toString();
		}

		@Override
		public void runTimeError(STMessage msg) {
			STException exception = new STException(reportSTMsg(msg));
			stExceptions.add(exception);
			throw exception;
		}

		@Override
		public void compileTimeError(STMessage msg) {
			STException exception = new STException(reportSTMsg(msg));
			stExceptions.add(exception);
			throw exception;
		}

		@Override
		public void IOError(STMessage msg) {
			STException exception = new STException(reportSTMsg(msg));
			stExceptions.add(exception);
			throw exception;
		}

		@Override
		public void internalError(STMessage msg) {
			STException exception = new STException(reportSTMsg(msg));
			stExceptions.add(exception);
			throw exception;
		}
	};
}
