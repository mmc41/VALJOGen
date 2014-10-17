/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model;

/**
 * Modifier enum identical to {@link javax.lang.model.element.Modifier} but in our own package to avoid having a dependency.
 *
 * @author mmc
 */
public enum Modifier {
   PUBLIC,
   PROTECTED,
   PRIVATE,
   ABSTRACT,
   DEFAULT,
   STATIC,
   FINAL,
   TRANSIENT,
   VOLATILE,
   SYNCHRONIZED,
   NATIVE,
   STRICTFP;

   /**
    * Returns this modifier's name in lowercase suitable for code generation.
    *
    * @return Name of modifiers in correct form for code generation,
    */
   public String toString() {
       return name().toLowerCase(java.util.Locale.US);
   }
}
