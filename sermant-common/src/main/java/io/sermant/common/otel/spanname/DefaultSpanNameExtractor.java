/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package io.sermant.common.otel.spanname;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;
import io.opentelemetry.instrumentation.api.semconv.http.HttpSpanNameExtractorBuilder;
import io.sermant.common.otel.attributes.OtelClientAttributesGetter;
import io.sermant.common.otel.attributes.OtelServerAttributesGetter;

import java.util.Set;

/**
 * Extractor of the <a href="https://github.com/open-telemetry/semantic-conventions/blob/v1.23.0/docs/http/http-spans.md#name">HTTP
 * span name</a>.
 *
 * @since 2.0.0
 */
public final class DefaultSpanNameExtractor {
    /**
     * Returns an HTTP client {@link SpanNameExtractor} with default configuration.
     *
     * @see Instrumenter#builder(OpenTelemetry, String, SpanNameExtractor)
     */
    public static <REQUEST> SpanNameExtractor<REQUEST> create(
            OtelClientAttributesGetter<REQUEST, ?> getter) {
        return builder(getter).build();
    }

    /**
     * Returns an HTTP server {@link SpanNameExtractor} with default configuration.
     *
     * @see Instrumenter#builder(OpenTelemetry, String, SpanNameExtractor)
     */
    public static <REQUEST> SpanNameExtractor<REQUEST> create(
            OtelServerAttributesGetter<REQUEST, ?> getter) {
        return builder(getter).build();
    }

    /**
     * Returns a new {@link HttpSpanNameExtractorBuilder} that can be used to configure the HTTP client span name
     * extractor.
     */
    public static <REQUEST> DefaultSpanNameExtractorBuilder<REQUEST> builder(
            OtelClientAttributesGetter<REQUEST, ?> getter) {
        return new DefaultSpanNameExtractorBuilder<>(getter, null);
    }

    /**
     * Returns a new {@link HttpSpanNameExtractorBuilder} that can be used to configure the HTTP server span name
     * extractor.
     */
    public static <REQUEST> DefaultSpanNameExtractorBuilder<REQUEST> builder(
            OtelServerAttributesGetter<REQUEST, ?> getter) {
        return new DefaultSpanNameExtractorBuilder<>(null, getter);
    }

    static final class Client<REQUEST> implements SpanNameExtractor<REQUEST> {
        private final OtelClientAttributesGetter<REQUEST, ?> getter;

        private final Set<String> knownMethods;

        Client(OtelClientAttributesGetter<REQUEST, ?> getter, Set<String> knownMethods) {
            this.getter = getter;
            this.knownMethods = knownMethods;
        }

        @Override
        public String extract(REQUEST request) {
            //            String method = getter.getHttpRequestMethod(request);
            //            if (method == null || !knownMethods.contains(method)) {
            //                return "HTTP";
            //            }
            //            return method;
            return "clientside";
        }
    }

    static final class Server<REQUEST> implements SpanNameExtractor<REQUEST> {
        private final OtelServerAttributesGetter<REQUEST, ?> getter;

        private final Set<String> knownMethods;

        Server(OtelServerAttributesGetter<REQUEST, ?> getter, Set<String> knownMethods) {
            this.getter = getter;
            this.knownMethods = knownMethods;
        }

        @Override
        public String extract(REQUEST request) {
            //            String method = getter.getHttpRequestMethod(request);
            //            String route = getter.getHttpRoute(request);
            //            if (method == null) {
            //                return "HTTP";
            //            }
            //            if (!knownMethods.contains(method)) {
            //                method = "HTTP";
            //            }
            //            return route == null ? method : method + " " + route;
            return "serverside";
        }
    }

    private DefaultSpanNameExtractor() {
    }
}
