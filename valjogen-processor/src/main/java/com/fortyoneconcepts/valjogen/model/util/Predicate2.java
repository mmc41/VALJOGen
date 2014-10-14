package com.fortyoneconcepts.valjogen.model.util;

/**
 * Represents a predicate (boolean-valued function) of two arguments.
 *
 * @param <T1> the type of the first input to the predicate
 * @param <T2> the type of the first input to the predicate
 *
 */
@FunctionalInterface
public interface Predicate2<T1,T2>
{
    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t1 the first input argument
     * @param t2 the second input argument
     * @return {@code true} if the input arguments matches the predicate,
     * otherwise {@code false}
     */
	 boolean test(T1 t1, T2 t2);
}
