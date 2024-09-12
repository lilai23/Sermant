package io.sermant.common.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.instrumenter.SpanNameExtractor;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-29
 */
public class DefaultInstrumenterFactory<REQUEST, RESPONSE> implements OtelInstrumenterFactory<REQUEST, RESPONSE> {
    private final String instrumentationName;

    private final OpenTelemetry openTelemetry;

    private SpanNameExtractor<? super REQUEST> spanNameExtractor;

    private AttributesExtractor<REQUEST, RESPONSE> attributesExtractor;

    public DefaultInstrumenterFactory(String instrumentationName, OpenTelemetry openTelemetry,
            SpanNameExtractor<? super REQUEST> spanNameExtractor,
            AttributesExtractor<REQUEST, RESPONSE> attributesExtractor) {
        this.instrumentationName = instrumentationName;
        this.openTelemetry = openTelemetry;
        //todo simple
        this.spanNameExtractor = spanNameExtractor;
        this.attributesExtractor = attributesExtractor;
    }

    public Instrumenter<REQUEST, RESPONSE> create() {
        return Instrumenter.<REQUEST, RESPONSE>builder(openTelemetry, instrumentationName, spanNameExtractor)
                .addAttributesExtractor(attributesExtractor)
                .buildInstrumenter();
    }
}
