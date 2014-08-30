package com.fortyoneconcepts.valjogen.processor;

@FunctionalInterface
public interface ResourceProducer
{
    public String getResourceAsText(String fileName) throws Exception;
}
