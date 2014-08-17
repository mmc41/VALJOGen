package com.fortyoneconcepts.valjogen.processor;

public interface ProcessorMessages
{
  public static final String BaseClassNotFound="Could not find base class %s";
  public static final String ImportTypeNotFound="Unknown import type %s";

  public static final String MalFormedSetter="Malformed setter %s";
  public static final String MalFormedGetter="Malformed getter %s";

  public static final String AnnotationOnInterfacesOnly = "Annotation %s may only be used with interfaces.";
  public static final String ExceptionFailure = "Fatal error - processing of %s failed due to exception %s";

  public static final String ParameterNamesUnavailable="Could not retrive correct parameter names to use for %s (-parameters option missing from javac?)";

  public static final String SucessMsg="Sucessfully generated file %s";
}
