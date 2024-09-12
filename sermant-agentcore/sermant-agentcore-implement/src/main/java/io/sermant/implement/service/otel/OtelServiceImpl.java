package io.sermant.implement.service.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.ContextPropagators;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.resources.ResourceBuilder;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.semconv.ResourceAttributes;
import io.sermant.core.service.otel.OtelService;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-27
 */
public class OtelServiceImpl implements OtelService {
    @Override
    public void start() {
        init();
    }

    public static void init() {
        initOpenTelemetry();
    }

    private static void initOpenTelemetry() {
        String serviceName = "test-name";
        OtlpGrpcSpanExporter spanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint("http://127.0.0.1:4317") // 替换为实际的 OTLP 接收器端点
                .build();

        ResourceBuilder resourceBuilder = new ResourceBuilder().putAll(Resource.getDefault())
                .put(ResourceAttributes.SERVICE_NAME, "otel-" + serviceName);

        // Set to process the spans by the Jaeger Exporter
        final SdkTracerProvider tracerProvider = SdkTracerProvider.builder()
                .addSpanProcessor(SimpleSpanProcessor.create(spanExporter)).setResource(resourceBuilder.build())
                .build();

        OpenTelemetrySdk.builder().setTracerProvider(tracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal();
    }
}
