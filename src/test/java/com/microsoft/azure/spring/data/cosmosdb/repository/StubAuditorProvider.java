/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository;

import lombok.Setter;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class StubAuditorProvider implements AuditorAware<String> {

    @Setter
    private String currentAuditor;

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.of(currentAuditor);
    }

}
