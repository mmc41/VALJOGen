package com.fortyoneconcepts.valjogen.test.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
* Create a new instance of our annotation with defaults (works also with source-only annotations at runtime).
* 
* @author mmc
*/
public class AnnotationDefaultsProxy implements InvocationHandler 
{
  @SuppressWarnings("unchecked")
public static <A extends Annotation> A defaultsOf(Class<A> annotation) 
  {
    return (A) Proxy.newProxyInstance(annotation.getClassLoader(), new Class[] { annotation }, new AnnotationDefaultsProxy());
  }
  
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    return method.getDefaultValue();
  }
}

