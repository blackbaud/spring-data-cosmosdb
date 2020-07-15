/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.cosmosdb.core.mapping;

import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.context.AbstractMappingContext;
import org.springframework.data.mapping.model.Property;
import org.springframework.data.mapping.model.SimpleTypeHolder;
import org.springframework.data.util.TypeInformation;

import java.util.Set;


public class CosmosMappingContext
        extends AbstractMappingContext<BasicCosmosPersistentEntity<?>, CosmosPersistentProperty> {

    private ApplicationContext context;

    @Override
    protected <T> BasicCosmosPersistentEntity<T> createPersistentEntity(TypeInformation<T> typeInformation) {
        final BasicCosmosPersistentEntity<T> entity = new BasicCosmosPersistentEntity<>(typeInformation);

        if (context != null) {
            entity.setApplicationContext(context);
        }
        return entity;
    }

    @Override
    public CosmosPersistentProperty createPersistentProperty(Property property,
                                                             BasicCosmosPersistentEntity<?> owner,
                                                             SimpleTypeHolder simpleTypeHolder) {
        return new BasicCosmosPersistentProperty(property, owner, simpleTypeHolder);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
