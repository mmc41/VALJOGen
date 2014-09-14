/*
* Copyright (C) 2014 41concepts Aps
*/
/**
 * This package contains ValjoGen examples.
 *
 * Note how a @VALJOConfigure annotation can be applied here as a default for all examples. The annotation
 * will be overridden it its entirety if also apllied on an interface of an example.
 *
 * @author mmc
 */
@VALJOConfigure(getterPrefixes={"is", "get", "has"}, ensureNotNullEnabled=false)
package com.fortyoneconcepts.valjogen.examples;
import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
