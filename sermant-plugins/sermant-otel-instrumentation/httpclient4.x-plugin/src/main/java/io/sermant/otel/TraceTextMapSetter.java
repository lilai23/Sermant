package io.sermant.otel;

import io.opentelemetry.context.propagation.TextMapSetter;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-20
 */
public enum TraceTextMapSetter implements TextMapSetter<ApacheHttpClientRequest> {
    INSTANCE;

    @Override
    public void set(ApacheHttpClientRequest carrier, String key, String value) {
        // Insert the context as Header
        if (carrier != null) {
            carrier.setHeader(key, value);
        }
    }
}
