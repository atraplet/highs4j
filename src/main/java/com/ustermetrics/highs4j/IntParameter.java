package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A parameter object for <a href="https://highs.dev">HiGHS</a> solver integer options.
 *
 * @param name  the option name
 * @param value the option value
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public record IntParameter(@NonNull String name, int value) implements Parameter {

    public IntParameter {
        checkArgument(!name.isBlank(), "name cannot be blank");
    }

}
