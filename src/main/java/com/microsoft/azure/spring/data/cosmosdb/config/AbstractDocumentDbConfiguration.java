/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.cosmosdb.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.azure.documentdb.DocumentClient;
import com.microsoft.azure.spring.data.cosmosdb.Constants;
import com.microsoft.azure.spring.data.cosmosdb.DocumentDbFactory;
import com.microsoft.azure.spring.data.cosmosdb.core.DocumentDbTemplate;
import com.microsoft.azure.spring.data.cosmosdb.core.convert.MappingDocumentDbConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;

@Configuration
public abstract class AbstractDocumentDbConfiguration extends DocumentDbConfigurationSupport {
    public abstract DocumentDBConfig getConfig();

    @Bean
    public DocumentClient documentClient() {
        return this.documentDbFactory().getDocumentClient();
    }

    @Qualifier(Constants.OBJECTMAPPER_BEAN_NAME)
    @Autowired(required = false)
    private ObjectMapper objectMapper;

    @Qualifier(Constants.AUDITING_HANDLER_BEAN_NAME)
    @Autowired(required = false)
    private IsNewAwareAuditingHandler cosmosAuditingHandler;

    @Bean
    public DocumentDbFactory documentDbFactory() {
        return new DocumentDbFactory(this.getConfig());
    }

    @Bean
    public DocumentDbTemplate documentDbTemplate() throws ClassNotFoundException {
        final DocumentDBConfig config = getConfig();
        return new DocumentDbTemplate(this.documentDbFactory(), this.mappingDocumentDbConverter(),
                cosmosAuditingHandler, config.getDatabase());
    }

    @Bean
    public MappingDocumentDbConverter mappingDocumentDbConverter() throws ClassNotFoundException {
        return new MappingDocumentDbConverter(this.documentDbMappingContext(), objectMapper);
    }
}
