/**
 * Copyright (c) Microsoft Corporation. All rights reserved.
 * Licensed under the MIT License. See LICENSE in the project root for
 * license information.
 */

package com.microsoft.azure.spring.data.cosmosdb.core;

import javax.annotation.Nullable;

public interface ResponseDiagnosticsProcessor {

    /**
     * Gets called after receiving response from CosmosDb.
     * Response Diagnostics are collected from API responses and
     * then set in {@link ResponseDiagnostics} object.
     * <p>
     * In case of missing diagnostics from CosmosDb, responseDiagnostics will be null.
     *
     * @param responseDiagnostics responseDiagnostics object containing CosmosDb response
     *                            diagnostics information
     */
    void processResponseDiagnostics(@Nullable ResponseDiagnostics responseDiagnostics);
}
