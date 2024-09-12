package io.sermant.common.otel.attributes;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.instrumentation.api.instrumenter.AttributesExtractor;

public interface OtelAttributesExtractor<REQUEST, RESPONSE> extends AttributesExtractor<REQUEST, RESPONSE> {
}
