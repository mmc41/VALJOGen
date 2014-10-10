package com.fortyoneconcepts.valjogen.processor;

import java.net.URI;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

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

	private final STGroup group;
	private final Set<String> templateMethodNames;

	/*
	private Set<String> specialMethods = new HashSet<String>(Arrays.asList("method_valueOf", "method_this"));

	@SuppressWarnings("serial")
	private static final Map<String , String> METHOD_SORT_RANK = new HashMap<String , String>() {{
	    put("method_valueOf",                            "0010"); // Presently unused - special method so not inserted dynamically.
	    put("method_this",                               "0020"); // Presently unused - special method so not inserted dynamically.

	    put("method_readObjectNoData",                   "0800");
	    put("method_readObject_ObjectInputStream",       "0810");
	    put("method_readResolve",                        "0820");
	    put("method_validateObject",                     "0830");
	    put("method_writeObject_ObjectOutputStream",     "0840");
	    put("method_writeReplace",                       "0850");

	    put("method_readExternal_ObjectInput",           "0860");
	    put("method_writeExternal_ObjectOutput",         "0870");

	    put("method_hashCode",                           "1000");
	    put("method_equals_Object",                      "1010");
	    put("method_compareTo_T",                        "1020");

	    put("method_finalize",                           "1030");
	    put("method_clone",                              "1030");

	    put("method_toString",                           "2000");
	}};


	 * allback if not mentioned in METHOD_SORT_RANK.

	private static final String UNKNOWN_METHOD_SORT_RANK = "100";


	 * Comparator that makes sure methods have a persistent total ordering with respect to sort rank and lexical order.

	private static final Comparator<String> compareMethodNames = new Comparator<String>()
	{
		@Override
		public int compare(String o1, String o2) {
			int result = getSortRank(o1).compareTo(getSortRank(o2));
			if (result==0)
				result=o1.compareTo(o2);
			return result;
		}

		private String getSortRank(String methodName)
		{
			return METHOD_SORT_RANK.getOrDefault(methodName, UNKNOWN_METHOD_SORT_RANK);
		}
	};
*/
	public STTemplates(ResourceLoader resourceLoader, Configuration cfg) throws Exception
	{
		STGroup defaultGroup = new STGroupFile(mainTemplateFile, delimiterStartChar, delimiterStopChar);

		String customTemplateFileName = cfg.getCustomTemplateFileName();


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

		Set<String> templateNames = getAllTemplateNames(group);

		// Extract method names but make sure special methods (incl. overridden special methods) are excluded in all cases:
		templateMethodNames = Collections.unmodifiableSet(templateNames.stream().filter(n -> n.startsWith(method_prefix)).map(n -> templateNameToMethodName(n)).collect(Collectors.toSet()));

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
	 * Return all template method names (build-in and custom) supplied by template group files except for special methods that are handled internally.
	 *
	 * @return List of normal template method names.
	 */
	public Set<String> getTemplateMethodNames()
	{
		return templateMethodNames;
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
}
