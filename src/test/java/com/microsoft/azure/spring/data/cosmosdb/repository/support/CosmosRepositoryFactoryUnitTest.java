/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.support;

import com.microsoft.azure.spring.data.cosmosdb.core.CosmosTemplate;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.MappingCosmosConverter;
import com.microsoft.azure.spring.data.cosmosdb.domain.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.core.EntityInformation;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CosmosRepositoryFactoryUnitTest {

    @Mock
    CosmosTemplate dbTemplate;
    @Mock
    MappingCosmosConverter converter;
    @Mock
    MappingContext mappingContext;

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void useMappingCosmosDBEntityInfoIfMappingContextSet() {
        when(dbTemplate.getConverter()).thenReturn(converter);
        when(converter.getMappingContext()).thenReturn(mappingContext);
        final CosmosRepositoryFactory factory = new CosmosRepositoryFactory(dbTemplate, applicationContext);
        final EntityInformation<Person, String> entityInfo = factory.getEntityInformation(Person.class);
        assertTrue(entityInfo instanceof CosmosEntityInformation);
        verify(mappingContext, times(1)).getRequiredPersistentEntity(Person.class);
    }
}
