/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.annotations.types;

/**
 * Used by annotations to specify if generated output should be mutable, immutable or left to the tool to decide.
 *
 * @author mmc
 */
public enum Mutability
{
  Undefined,
  Mutable,
  Immutable;

  public boolean isMutable()
  {
	  return this==Mutable;
  }

  public boolean isImmutable()
  {
	  return this==Immutable;
  }
}
