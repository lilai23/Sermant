package io.sermant.otel;

import io.sermant.common.otel.attributes.OtelClientAttributesGetter;

import javax.annotation.Nullable;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-29
 */
public class ClientAttributesGetter<REQUEST, RESPONSE> implements OtelClientAttributesGetter<REQUEST, RESPONSE> {
    public ClientAttributesGetter() {
    }

    @Nullable
    @Override
    public String getServerAddress(REQUEST request) {
        return "123";
    }

    @Nullable
    @Override
    public Integer getServerPort(REQUEST request) {
        return 111;
    }
}
