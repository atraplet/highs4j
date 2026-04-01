package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A parameter object for <a href="https://highs.dev">HiGHS</a> solver double options.
 *
 * @param name  the option name
 * @param value the option value
 * @see <a href="https://highs.dev">HiGHS</a>
 */
public record DoubleParameter(@NonNull String name, double value) implements Parameter {

    public DoubleParameter {
        checkArgument(!name.isBlank(), "name cannot be blank");
    }

}
