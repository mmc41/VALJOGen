/*
* Copyright (C) 2014 41concepts Aps
*/
package com.fortyoneconcepts.valjogen.model.util;

/**
 * Similar to java.util.Function but with apply that allows for Exceptions
 *
 * @author mmc
 *
 * @param <T> Argument
 * @param <R> Result.
 */
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
