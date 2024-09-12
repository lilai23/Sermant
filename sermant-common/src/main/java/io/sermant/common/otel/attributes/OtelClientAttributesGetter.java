/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.sermant.common.otel.attributes;

import io.opentelemetry.instrumentation.api.semconv.network.NetworkAttributesGetter;
import io.opentelemetry.instrumentation.api.semconv.network.ServerAttributesGetter;

import javax.annotation.Nullable;

/**
 * An interface for getting HTTP client attributes.
 *
 * <p>Instrumentation authors will create implementations of this interface for their specific
 * library/framework. It will be used by the {@link HttpClientAttributesExtractor} to obtain the various HTTP client
 * attributes in a type-generic way.
 *
 * @since 2.0.0
 */
public interface OtelClientAttributesGetter<REQUEST, RESPONSE> extends NetworkAttributesGetter<REQUEST, RESPONSE>,
        ServerAttributesGetter<REQUEST> {
    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    String getServerAddress(REQUEST request);

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    Integer getServerPort(REQUEST request);
}
