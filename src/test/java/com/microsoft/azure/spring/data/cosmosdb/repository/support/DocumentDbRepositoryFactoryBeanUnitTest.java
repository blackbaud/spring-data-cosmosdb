/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.support;

import com.microsoft.azure.spring.data.cosmosdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.MappingDocumentDbConverter;
import com.microsoft.azure.spring.data.cosmosdb.repository.repository.PersonRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DocumentDbRepositoryFactoryBeanUnitTest {
    @Mock
    DocumentDbTemplate dbTemplate;
    @Mock
    MappingDocumentDbConverter converter;

    @Test
    public void testCreateRepositoryFactory() {
        when(dbTemplate.getConverter()).thenReturn(converter);
        final DocumentDbRepositoryFactoryBean factoryBean =
                new DocumentDbRepositoryFactoryBean(PersonRepository.class);
        factoryBean.setDocumentDbOperations(dbTemplate);
        final RepositoryFactorySupport factory = factoryBean.createRepositoryFactory();
        assertThat(factory).isNotNull();
    }
}
