package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record DoubleParameter(@NonNull String name, double value) implements Parameter {

    public DoubleParameter {
        checkArgument(!name.isBlank());
    }

}
