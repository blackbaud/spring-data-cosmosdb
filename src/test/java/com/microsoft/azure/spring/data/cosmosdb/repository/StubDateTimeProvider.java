/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository;

import lombok.Setter;
import org.springframework.data.auditing.DateTimeProvider;

import java.time.OffsetDateTime;
import java.time.temporal.TemporalAccessor;
import java.util.Optional;

public class StubDateTimeProvider implements DateTimeProvider {

    @Setter
    private OffsetDateTime now;

    @Override
    public Optional<TemporalAccessor> getNow() {
        return Optional.of(now);
    }

}
