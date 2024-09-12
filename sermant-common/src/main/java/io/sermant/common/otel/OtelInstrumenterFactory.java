package io.sermant.common.otel;

import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-29
 */
public interface OtelInstrumenterFactory<REQUEST, RESPONSE> {
    Instrumenter<REQUEST, RESPONSE> create();
}
