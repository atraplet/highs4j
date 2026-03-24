package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record IntParameter(@NonNull String name, int value) implements Parameter {

    public IntParameter {
        checkArgument(!name.isBlank());
    }

}
