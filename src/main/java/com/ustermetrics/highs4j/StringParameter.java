package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A string <a href="https://highs.dev">HiGHS</a> solver option parameter.
 *
 * @param name  the option name
 * @param value the option value
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public record StringParameter(@NonNull String name, @NonNull String value) implements Parameter {

    public StringParameter {
        checkArgument(!name.isBlank());
    }

}
