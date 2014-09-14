package com.fortyoneconcepts.valjogen.model;

/**
 * Specifies the access scope for a model.
 *
 * @author mmc
 */
public enum AccessLevel
{
 PUBLIC("public"),
 PRIVATE("private"),
 PROTECTED("protected"),
 PACKAGE("");

 private final String value;

 private AccessLevel(String value)
 {
	 this.value=value;
 }

 public String toString()
 {
	 return value;
 }
}
