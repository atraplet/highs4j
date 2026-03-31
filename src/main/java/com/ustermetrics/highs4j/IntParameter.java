package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * An integer <a href="https://highs.dev">HiGHS</a> solver option parameter.
 *
 * @param name  the option name
 * @param value the option value
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public record IntParameter(@NonNull String name, int value) implements Parameter {

    public IntParameter {
        checkArgument(!name.isBlank());
    }

}
