package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record BooleanParameter(@NonNull String name, boolean value) implements Parameter {

    public BooleanParameter {
        checkArgument(!name.isBlank());
    }

}
