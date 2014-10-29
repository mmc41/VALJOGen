/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Macro definitions.
 *
 * @author mmc
 */
public interface ConfigurationMacros
{
  /**
  * What all macros start with
  */
  public static final String MacroPrefix="$(";

  /**
  * What all macros start with
  */
  public static final String MacroSuffix=")";

  /**
  * The value that specified that no value is set. Any string that contains this will be converted to null.
  */
  public static final String NotApplicableMacro = MacroPrefix+"N/A"+MacroSuffix;

  /**
   * Resolves to the name of the generated class.
   */
  public static final String GeneratedClassNameMacro = MacroPrefix+"This"+MacroSuffix;

  /**
   * Resolves to the name of interface that is the main source for the generated class.
   */
  public static final String MasterInterfaceMacro = MacroPrefix+"IThis"+MacroSuffix;

  /**
   * Resolves to the time of execution.
   */
  public static final String ExecutionDateMacro = MacroPrefix+"ExecutionDate"+MacroSuffix;
}
