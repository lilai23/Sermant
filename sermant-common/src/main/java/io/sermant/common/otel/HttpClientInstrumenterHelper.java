package io.sermant.common.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.context.propagation.TextMapSetter;
import io.opentelemetry.instrumentation.api.incubator.builder.internal.DefaultHttpClientInstrumenterBuilder;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.instrumentation.api.semconv.http.HttpClientAttributesGetter;
import io.opentelemetry.javaagent.bootstrap.internal.AgentCommonConfig;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-28
 */
public class HttpClientInstrumenterHelper extends InstrumenterHelper {
    public static <REQUEST, RESPONSE> Instrumenter<REQUEST, RESPONSE> createInstrumenter(String instrumentationName,
            HttpClientAttributesGetter<REQUEST, RESPONSE> httpAttributesGetter,
            TextMapSetter<REQUEST> headerSetter) {
        //        return JavaagentHttpClientInstrumenters.create(
        //                instrumentationName, httpAttributesGetter, headerSetter);
        HttpInstrumenterFactory<REQUEST, RESPONSE> defaultHttpClientTelemetryBuilder =
                new HttpInstrumenterFactory<>(
                        instrumentationName, GlobalOpenTelemetry.get(), httpAttributesGetter);
        if (headerSetter != null) {
            defaultHttpClientTelemetryBuilder.setHeaderSetter(headerSetter);
        }

        return defaultHttpClientTelemetryBuilder
                .configure(AgentCommonConfig.get())
                .setBuilderCustomizer(b -> {
                }).create();
    }
}
