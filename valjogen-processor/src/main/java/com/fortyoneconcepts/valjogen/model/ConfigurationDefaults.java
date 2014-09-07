package com.fortyoneconcepts.valjogen.model;

import com.fortyoneconcepts.valjogen.annotations.*;

/**
 * Defaults and values with special meaning used for configuration and annotations.
 *
 * @author mmc
 */
public interface ConfigurationDefaults
{
  public static final String TOP_PACKAGE_NAME = "com.fortyoneconcepts.valjogen";

  /**
  * Common qualifier for all options.
  */
  public static final String OPTION_QUALIFIER = TOP_PACKAGE_NAME+".";

  /**
  * The value that specified that no value is set.
  *
  * Default for names and packages etc.
  *
  * @see VALJOConfigure#outputPackage
  * @see VALJOGenerate#name
  */
  public static final String NotApplicable = "N/A";

  public static final String GeneratedClassNameReference = "$(This)";

  public static final String RootObject = "java.lang.Object";

  public static final long SerialVersionUID_NotSet = 0;
}
