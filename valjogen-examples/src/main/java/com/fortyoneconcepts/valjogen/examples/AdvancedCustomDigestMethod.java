/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.examples;

import java.util.List;

import com.fortyoneconcepts.valjogen.annotations.VALJOConfigure;
import com.fortyoneconcepts.valjogen.annotations.VALJOGenerate;

/**
* Example that shows a non-trivial custom template implementing an advanced custom method that calculate a digest of object state.
* The generated class is set to inherit from a base class that provide both contract and helper methods.
* The example also shows how to make the generated mutable class thread safe.
*/
@VALJOGenerate(comment="Example 23")
@VALJOConfigure(customJavaTemplateFileName="advanced_custom_method.stg", baseClazzName="DigestBaseClass", synchronizedAccessEnabled=true)
public interface AdvancedCustomDigestMethod
{
	public String getName();
	public void setName(String name);

	public int getAge();
	public void setAge(int age);

	public String[] getAddress();
	public void setAddress(String[] address);

	public List<String> getAltAddress();
	public void setAltAddress(List<String> altAddress);

	public boolean isVerified();
	public void setVerified(boolean verified);

	// Custom method calculateDigest declared in base class.
}
