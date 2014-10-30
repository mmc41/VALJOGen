package com.fortyoneconcepts.valjogen.processor;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.fortyoneconcepts.valjogen.model.Configuration;

/**
 * Our own FileHandler class so we can recognize it from other FileHandlers.
 *
 * At the same time this handler class provides help for adding/removing.
 *
 * @author mmc
 */
public final class KnownFileHandler extends FileHandler
{
	private final String pattern;

	public KnownFileHandler(String pattern, boolean append) throws IOException, SecurityException
	{
		super(pattern, append);
		this.pattern=pattern;
	}

	public String getPattern()
	{
		return pattern;
	}

	/**
	 * Configure log system (parentLogger) according to configuration.
	 *
	 * @param parentLogger The logger instance that should be configured.
	 * @param configuration Where to read how logging should be setup.
	 * @throws ConfigurationException Thrown if unable to setup logging.
	 */
	public static void setUpLogging(Logger parentLogger, Configuration configuration) throws ConfigurationException
	{
		String logFileString = configuration.getLogFile();

		try {
			// Only add a filehandler if it is not there already with the same log file name.
			// + Remove old handlers for other log files added previously by this processor (useful for running test suites with different log files)
			Handler[] handlers = parentLogger.getHandlers().clone();
			boolean alreadyAddedLogger = false;
		    for (Handler _handler : handlers) {
			    if (_handler instanceof KnownFileHandler) {
			    	KnownFileHandler handler = (KnownFileHandler)_handler;
			    	if (handler.getPattern().equals(logFileString)) {
			    		alreadyAddedLogger=true;
			    	} else {
			    		parentLogger.removeHandler(_handler);
			    		_handler.close();
			    	}
			    }
		    }

			if (!alreadyAddedLogger && logFileString!=null) {
				FileHandler logFile = new KnownFileHandler(logFileString, true);
				logFile.setFormatter(new SimpleFormatter());
				logFile.setLevel(Level.FINEST);
				parentLogger.addHandler(logFile);
			}
		} catch(Throwable ex)
		{
			throw new ConfigurationException("Could not setup log file at "+logFileString, ex);
		}

	    parentLogger.setLevel(configuration.getLogLevel());
	}
}
