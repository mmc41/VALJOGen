package com.fortyoneconcepts.valjogen.model.util;

@FunctionalInterface
public interface ThrowingFunction<T, R>
{
    /**
     * Applies this function to the given argument.
     *
     * @param t the function argument
     * @return the function result
     * @throws Exception If error.
     */
    R apply(T t) throws Exception;
}
