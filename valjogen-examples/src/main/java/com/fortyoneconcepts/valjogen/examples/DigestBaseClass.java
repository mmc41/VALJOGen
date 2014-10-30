package com.fortyoneconcepts.valjogen.examples;

/**
* Example of a preexisting abstract base class that a generated class can extend from.
*
* In this case the bease class both specifies an abstract method that the
* generated class must implement (using custom templates) as well as
* helper methods useful for calculating digests. Helper methods using overloading
* like here can makes custom templates much simpler to write.
*
* Used by the "AdvancedCustomDigestMethod.java" example and its assoicated template "advanced_custom_method.stg".
*
* Note that the current set of getBytes methods only handle a subset of types such as bytes, integers
* and strings (arrays and collections of thoses are handled by the custom template). For a real implementation,
* one should overload with additional getBytes methods.
*/
public abstract class DigestBaseClass
{
    // Will be implemented by a custom template specified in the interface for the example.
	public abstract byte[] calculateDigest(String algorithm) throws java.security.NoSuchAlgorithmException;

	protected static final byte getBytes(boolean v) {
		return v ? (byte)1 : (byte)0;
	}

	protected static final byte[] getBytes(int v) {
	  return new byte[] {
		        (byte)((v >> 24) & 0xff),
		        (byte)((v >> 16) & 0xff),
		        (byte)((v >> 8) & 0xff),
		        (byte)((v >> 0) & 0xff),
	  };
    }

	protected static final byte[] getBytes(String v) {
	    return v.getBytes();
	}
}
