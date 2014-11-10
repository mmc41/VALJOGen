/*
* Copyright (C) 2014 41concepts Aps
*/
/**
* This package contains test source classes that we run our annotation processor against during integration testing.
*
* This file also specifies @VALJOConfigure defaults that some test(s) may depend on.
*
* @author mmc
**/
@VALJOConfigure(serialVersionUID=42, staticFactoryMethodEnabled=true, dataConversion=DataConversion.JACKSON_DATABIND_ANNOTATIONS)
package com.fortyoneconcepts.valjogen.testsources;
import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.types.*;

