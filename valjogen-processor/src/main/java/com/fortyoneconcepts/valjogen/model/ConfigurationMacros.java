package com.fortyoneconcepts.valjogen.model;

/**
 * Macro definitions.
 *
 * @author mmc
 */
public interface ConfigurationMacros
{
  /**
  * The value that specified that no value is set. Any string that contains this will be converted to null.
  */
  public static final String NotApplicableMacro = "$(N/A)";

  /**
   * Resolves to the name of the generated class.
   */
  public static final String GeneratedClassNameMacro = "$(This)";

  /**
   * Resolves to the name of interface that is the main source for the generated class.
   */
  public static final String MasterInterfaceMacro = "$(IThis)";

  /**
   * Resolves to the time of execution.
   */
  public static final String ExecutionDateMacro = "$(ExecutionDate)";
}
