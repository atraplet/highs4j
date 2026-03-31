package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A boolean <a href="https://highs.dev">HiGHS</a> solver option parameter.
 *
 * @param name  the option name
 * @param value the option value
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public record BooleanParameter(@NonNull String name, boolean value) implements Parameter {

    public BooleanParameter {
        checkArgument(!name.isBlank());
    }

}
