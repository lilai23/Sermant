/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.sermant.common.otel.attributes;

import io.opentelemetry.instrumentation.api.semconv.http.HttpCommonAttributesGetter;
import io.opentelemetry.instrumentation.api.semconv.network.ClientAttributesGetter;
import io.opentelemetry.instrumentation.api.semconv.network.NetworkAttributesGetter;
import io.opentelemetry.instrumentation.api.semconv.url.UrlAttributesGetter;

import javax.annotation.Nullable;

/**
 * An interface for getting HTTP server attributes.
 *
 * <p>Instrumentation authors will create implementations of this interface for their specific
 * library/framework. It will be used by the {@link HttpServerAttributesExtractor} to obtain the various HTTP server
 * attributes in a type-generic way.
 *
 * @since 2.0.0
 */
public interface OtelServerAttributesGetter<REQUEST, RESPONSE> extends NetworkAttributesGetter<REQUEST, RESPONSE>,
        ClientAttributesGetter<REQUEST> {
    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    default String getClientAddress(REQUEST request) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Nullable
    @Override
    default Integer getClientPort(REQUEST request) {
        return null;
    }
}
