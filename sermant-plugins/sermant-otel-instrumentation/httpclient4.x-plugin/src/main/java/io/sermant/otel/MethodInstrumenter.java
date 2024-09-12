package io.sermant.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.api.instrumenter.Instrumenter;
import io.sermant.common.otel.DefaultInstrumenterFactory;
import io.sermant.common.otel.HttpClientInstrumenterHelper;
import io.sermant.common.otel.attributes.DefaultClientAttributesExtractor;
import io.sermant.common.otel.spanname.DefaultSpanNameExtractorBuilder;

import org.apache.http.HttpResponse;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-26
 */
public class MethodInstrumenter {
    private static final Instrumenter<ApacheHttpClientRequest, Object> INSTRUMENTER;

    static {
//        INSTRUMENTER =
//                HttpClientInstrumenterHelper.createInstrumenter(
//                        "INSTRUMENTATION_NAME",
//                        new ApacheHttpClientHttpAttributesGetter(),
//                        TraceTextMapSetter.INSTANCE);
        ClientAttributesGetter<ApacheHttpClientRequest, Object> getter = new ClientAttributesGetter<>();
        DefaultInstrumenterFactory<ApacheHttpClientRequest, Object> factory = new DefaultInstrumenterFactory<>(
                "sermant", GlobalOpenTelemetry.get(),
                new DefaultSpanNameExtractorBuilder<>(
                        getter, null).build(), new DefaultClientAttributesExtractor<>(getter));
        INSTRUMENTER = factory.create();
    }

    public static Instrumenter<ApacheHttpClientRequest, Object> instrumenter() {
        return INSTRUMENTER;
    }
}

