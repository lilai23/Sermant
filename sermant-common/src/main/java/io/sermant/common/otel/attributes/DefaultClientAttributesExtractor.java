package io.sermant.common.otel.attributes;

import static io.opentelemetry.instrumentation.api.internal.AttributesExtractorUtil.internalSet;

import io.opentelemetry.api.common.AttributesBuilder;
import io.opentelemetry.context.Context;
import io.opentelemetry.semconv.NetworkAttributes;
import io.opentelemetry.semconv.ServerAttributes;

import java.util.Locale;

import javax.annotation.Nullable;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-29
 */
public class DefaultClientAttributesExtractor<REQUEST, RESPONSE, GETTER extends OtelClientAttributesGetter<REQUEST, RESPONSE>> implements
        OtelAttributesExtractor<REQUEST, RESPONSE> {
    private GETTER getter;

    public DefaultClientAttributesExtractor(GETTER getter) {
        this.getter = getter;
    }

    @Override
    public void onStart(AttributesBuilder attributes, Context parentContext, REQUEST request) {
        attributes.put(ServerAttributes.SERVER_ADDRESS, getter.getServerAddress(request));
        attributes.put(ServerAttributes.SERVER_PORT, getter.getServerPort(request));
    }

    @Override
    public void onEnd(AttributesBuilder attributes, Context context, REQUEST request,
            @Nullable RESPONSE response, @Nullable Throwable error) {
        String protocolName = lowercase(getter.getNetworkProtocolName(request, response));
        String protocolVersion = lowercase(getter.getNetworkProtocolVersion(request, response));

        String transport = lowercase(getter.getNetworkTransport(request, response));
        internalSet(attributes, NetworkAttributes.NETWORK_TRANSPORT, transport);
        internalSet(
                attributes,
                NetworkAttributes.NETWORK_TYPE,
                lowercase(getter.getNetworkType(request, response)));
        internalSet(attributes, NetworkAttributes.NETWORK_PROTOCOL_NAME, protocolName);
        internalSet(attributes, NetworkAttributes.NETWORK_PROTOCOL_VERSION, protocolVersion);

        String localAddress = getter.getNetworkLocalAddress(request, response);
        if (localAddress != null) {
            internalSet(attributes, NetworkAttributes.NETWORK_LOCAL_ADDRESS, localAddress);

            Integer localPort = getter.getNetworkLocalPort(request, response);
            if (localPort != null && localPort > 0) {
                internalSet(attributes, NetworkAttributes.NETWORK_LOCAL_PORT, (long) localPort);
            }
        }

        String peerAddress = getter.getNetworkPeerAddress(request, response);
        if (peerAddress != null) {
            internalSet(attributes, NetworkAttributes.NETWORK_PEER_ADDRESS, peerAddress);

            Integer peerPort = getter.getNetworkPeerPort(request, response);
            if (peerPort != null && peerPort > 0) {
                internalSet(attributes, NetworkAttributes.NETWORK_PEER_PORT, (long) peerPort);
            }
        }
    }

    @Nullable
    private static String lowercase(@Nullable String str) {
        return str == null ? null : str.toLowerCase(Locale.ROOT);
    }
}
