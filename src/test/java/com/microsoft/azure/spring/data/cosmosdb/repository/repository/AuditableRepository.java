/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.repository;

import com.microsoft.azure.spring.data.cosmosdb.domain.AuditableEntity;
import com.microsoft.azure.spring.data.cosmosdb.repository.CosmosRepository;

public interface AuditableRepository extends CosmosRepository<AuditableEntity, String> {
}
