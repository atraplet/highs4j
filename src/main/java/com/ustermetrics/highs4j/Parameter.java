package com.ustermetrics.highs4j;

/**
 * Sealed interface for <a href="https://highs.dev">HiGHS</a> solver option parameters.
 *
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public sealed interface Parameter permits BooleanParameter, IntParameter, DoubleParameter, StringParameter {

    /**
     * @return the option name
     */
    String name();

}
