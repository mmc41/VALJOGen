/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Defaults and values with special meaning used for configuration and annotations.
 *
 * @author mmc
 */
public interface ConfigurationDefaults
{
  public static final String TOP_PACKAGE_NAME = "com.fortyoneconcepts.valjogen";

  public static final String SETTINGS_CONFIG_FILE = "valjogen.properties";

  /**
  * Common qualifier for all options.
  */
  public static final String OPTION_QUALIFIER = TOP_PACKAGE_NAME+".";

  public static final String RootObject = "java.lang.Object";

  public static final long SerialVersionUID_NotSet = 0;

  public static final String factoryMethodName = "valueOf";
}
