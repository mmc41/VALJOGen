package com.fortyoneconcepts.valjogen.model.util;

/**
 * Represents a predicate (boolean-valued function) of two arguments.
 *
 * @param <T1> the type of the first input to the predicate
 * @param <T2> the type of the second input to the predicate
 * @param <T3> the type of the third input to the predicate
 * @param <T4> the type of the forth input to the predicate
 *
 */
@FunctionalInterface
public interface Predicate4<T1,T2,T3,T4>
{
    /**
     * Evaluates this predicate on the given argument.
     *
     * @param t1 the first input argument
     * @param t2 the second input argument
     * @param t3 the third input argument
     * @param t4 the forth input argument
     * @return {@code true} if the input arguments matches the predicate,
     * otherwise {@code false}
     */
	 boolean test(T1 t1, T2 t2, T3 t3, T4 t4);
}
