package com.fortyoneconcepts.valjogen.processor;

public interface ProcessorMessages
{
  public static final String ClassNotFound="Could not find class/interface %s";
  public static final String InterfaceNotFound="Could not find interface %s";
  public static final String MemberNotFound="Could not find member %s";

  public static final String MemberNotComparable="Member %s is not comparable";

  public static final String ImportTypeNotFound="Unknown import type %s";

  public static final String MalFormedSetter="Malformed setter %s";
  public static final String MalFormedGetter="Malformed getter %s";
  public static final String InconsistentProperty="Type inconsistency between property methods %s";

  public static final String AnnotationOnInterfacesOnly = "Annotation %s may only be used with interfaces.";

  public static final String StringTemplateExceptionFailure = "Fatal error - processing of %s failed due to template error %s";
  public static final String ExceptionFailure = "Fatal error - processing of %s failed due to internal exception %s";
  public static final String ConfigurationFailure = "Fatal error - configuration error %s";

  public static final String ParameterNamesUnavailable="Could not retrive correct parameter names to use for %s (-parameters option missing from javac?)";

  public static final String NotAllMembersAreComparable="Implementation for %s is comparable but not all members are comparable and which members to use are not specifed. Non comparable-members will be omitted in compareTo method";

  public static final String SucessMsg="Sucessfully generated file %s";

  public static final String UNKNOWN_METHOD = "Unknown/undeclared method %s";

  public static final String NOT_A_CLASS = "%s is not a class type";
  public static final String NOT_AN_INTERFACE = "%s is not an interface type";
}
