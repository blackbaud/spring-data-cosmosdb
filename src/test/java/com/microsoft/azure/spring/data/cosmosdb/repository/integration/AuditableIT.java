/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */
package com.microsoft.azure.spring.data.cosmosdb.repository.integration;

import com.microsoft.azure.spring.data.cosmosdb.domain.AuditableEntity;
import com.microsoft.azure.spring.data.cosmosdb.repository.StubAuditorProvider;
import com.microsoft.azure.spring.data.cosmosdb.repository.StubDateTimeProvider;
import com.microsoft.azure.spring.data.cosmosdb.repository.TestRepositoryConfig;
import com.microsoft.azure.spring.data.cosmosdb.repository.repository.AuditableRepository;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestRepositoryConfig.class)
public class AuditableIT {

    @Autowired
    private AuditableRepository auditableRepository;
    @Autowired
    private StubDateTimeProvider stubDateTimeProvider;
    @Autowired
    private StubAuditorProvider stubAuditorProvider;

    @After
    public void cleanup() {
        this.auditableRepository.deleteAll();
    }

    @Test
    public void testInsertShouldSetAuditableEntries() {
        final AuditableEntity entity = AuditableEntity.builder().build();
        final OffsetDateTime now = OffsetDateTime.now(ZoneId.of("UTC"));

        stubDateTimeProvider.setNow(now);
        stubAuditorProvider.setCurrentAuditor("created-by");
        final AuditableEntity savedEntity = auditableRepository.save(entity);

        assertThat(savedEntity.getCreatedBy()).isEqualTo("created-by");
        assertThat(savedEntity.getCreatedDate()).isEqualTo(now);
        assertThat(savedEntity.getLastModifiedBy()).isEqualTo("created-by");
        assertThat(savedEntity.getLastModifiedByDate()).isEqualTo(now);
    }
    
    @Test
    public void testUpdateShouldNotOverwriteCreatedEntries() {
        final AuditableEntity entity = AuditableEntity.builder().build();
        final OffsetDateTime createdOn = OffsetDateTime.now(ZoneId.of("UTC"));

        stubDateTimeProvider.setNow(createdOn);
        stubAuditorProvider.setCurrentAuditor("created-by");
        final AuditableEntity savedEntity = auditableRepository.save(entity);

        final OffsetDateTime modifiedOn = createdOn.plusMinutes(1);
        stubDateTimeProvider.setNow(modifiedOn);
        stubAuditorProvider.setCurrentAuditor("modified-by");
        final AuditableEntity modifiedEntity = auditableRepository.save(savedEntity);

        assertThat(modifiedEntity.getCreatedBy()).isEqualTo("created-by");
        assertThat(modifiedEntity.getCreatedDate()).isEqualTo(createdOn);
        assertThat(modifiedEntity.getLastModifiedBy()).isEqualTo("modified-by");
        assertThat(modifiedEntity.getLastModifiedByDate()).isEqualTo(modifiedOn);
    }

}
