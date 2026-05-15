package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A parameter object for <a href="https://highs.dev">HiGHS</a> solver string options.
 *
 * @param name  the option name
 * @param value the option value
 */
public record StringParameter(@NonNull String name, @NonNull String value) implements Parameter {

    public StringParameter {
        checkArgument(!name.isBlank(), "name cannot be blank");
        checkArgument(!value.isBlank(), "value cannot be blank");
    }

}
