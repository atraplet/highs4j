package com.ustermetrics.highs4j;

public sealed interface Parameter permits BooleanParameter, IntParameter, DoubleParameter, StringParameter {

    String name();

}
