/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.cosmosdb.repository.support;

import com.microsoft.azure.spring.data.cosmosdb.core.DocumentDbOperations;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.DocumentDbPersistentEntity;
import com.microsoft.azure.spring.data.cosmosdb.core.mapping.DocumentDbPersistentProperty;
import com.microsoft.azure.spring.data.cosmosdb.repository.query.DocumentDbQueryMethod;
import com.microsoft.azure.spring.data.cosmosdb.repository.query.PartTreeDocumentDbQuery;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Optional;


public class DocumentDbRepositoryFactory extends RepositoryFactorySupport {

    private final ApplicationContext applicationContext;
    private final DocumentDbOperations dbOperations;
    private final MappingContext<? extends DocumentDbPersistentEntity<?>, DocumentDbPersistentProperty> mappingContext;

    public DocumentDbRepositoryFactory(DocumentDbOperations dbOperations, ApplicationContext applicationContext) {
        this.dbOperations = dbOperations;
        this.applicationContext = applicationContext;
        this.mappingContext = dbOperations.getConverter().getMappingContext();
    }

    @Override
    protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
        return SimpleDocumentDbRepository.class;
    }

    @Override
    protected Object getTargetRepository(RepositoryInformation information) {
        final EntityInformation<?, Serializable> entityInformation = getEntityInformation(information.getDomainType());
        return getTargetRepositoryViaReflection(information, entityInformation, this.applicationContext);
    }

    @Override
    public <T, ID> EntityInformation<T, ID> getEntityInformation(Class<T> domainClass) {
        // TODO: initialization of the persistent entity is required to integrate with spring-data auditing support
        // there might be a better way to do this, or to better use DocumentDbPersistentEntity and remove logic from
        // DocumentDbEntityInformation.  should investigate when time permits.
        mappingContext.getRequiredPersistentEntity(domainClass);
        return new DocumentDbEntityInformation<>(domainClass);
    }

    @Override
    protected Optional<QueryLookupStrategy> getQueryLookupStrategy(
            QueryLookupStrategy.Key key, QueryMethodEvaluationContextProvider evaluationContextProvider) {
        return Optional.of(new DocumentDbQueryLookupStrategy(dbOperations, evaluationContextProvider));
    }

    private static class DocumentDbQueryLookupStrategy implements QueryLookupStrategy {
        private final DocumentDbOperations dbOperations;

        public DocumentDbQueryLookupStrategy(
                DocumentDbOperations operations, QueryMethodEvaluationContextProvider provider) {
            this.dbOperations = operations;
        }

        @Override
        public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata,
                                            ProjectionFactory factory, NamedQueries namedQueries) {
            final DocumentDbQueryMethod queryMethod = new DocumentDbQueryMethod(method, metadata, factory);

            Assert.notNull(queryMethod, "queryMethod must not be null!");
            Assert.notNull(dbOperations, "dbOperations must not be null!");
            return new PartTreeDocumentDbQuery(queryMethod, dbOperations);

        }
    }
}
