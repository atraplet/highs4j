package com.ustermetrics.highs4j;

import lombok.NonNull;

import static com.google.common.base.Preconditions.checkArgument;

public record StringParameter(@NonNull String name, @NonNull String value) implements Parameter {

    public StringParameter {
        checkArgument(!name.isBlank());
    }

}
