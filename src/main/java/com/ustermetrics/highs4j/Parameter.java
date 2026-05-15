package com.ustermetrics.highs4j;

/**
 * Sealed interface for parameter objects for <a href="https://highs.dev">HiGHS</a> solver options.
 */
public sealed interface Parameter permits BooleanParameter, IntParameter, DoubleParameter, StringParameter {

    /**
     * @return the option name
     */
    String name();

}
