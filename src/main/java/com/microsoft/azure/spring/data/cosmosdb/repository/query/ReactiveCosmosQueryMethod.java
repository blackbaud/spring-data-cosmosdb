/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.query;

import com.microsoft.azure.spring.data.cosmosdb.repository.support.CosmosEntityInformation;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.EntityMetadata;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryMethod;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Method;

public class ReactiveCosmosQueryMethod extends QueryMethod {

    private ReactiveCosmosEntityMetadata<?> metadata;
    private Method method;

    public ReactiveCosmosQueryMethod(Method method, RepositoryMetadata metadata, ProjectionFactory factory) {
        super(method, metadata, factory);
        this.method = method;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EntityMetadata<?> getEntityInformation() {
        final Class<Object> domainType = (Class<Object>) getDomainClass();
        final CosmosEntityInformation entityInformation =
                new CosmosEntityInformation<Object, String>(domainType);

        this.metadata = new SimpleReactiveCosmosEntityMetadata<Object>(domainType, entityInformation);
        return this.metadata;
    }

    public Class<?> getReactiveWrapper() {
       return isReactiveWrapperClass(method.getReturnType()) ? method.getReturnType() : null;
    }

    private static boolean isReactiveWrapperClass(Class clazz) {
       return clazz.equals(Flux.class) || clazz.equals(Mono.class);
    }
}
