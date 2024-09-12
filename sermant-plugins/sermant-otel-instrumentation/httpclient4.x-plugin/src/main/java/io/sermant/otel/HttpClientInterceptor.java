package io.sermant.otel;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.sermant.common.otel.OtelContextHelper;
import io.sermant.core.plugin.agent.entity.ExecuteContext;
import io.sermant.core.plugin.agent.interceptor.Interceptor;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;

import java.util.Collections;

/**
 * 类描述
 *
 * @author lilai
 * @since 2024-08-22
 */
public class HttpClientInterceptor implements Interceptor {
    @Override
    public ExecuteContext before(ExecuteContext executeContext) throws Exception {

        HttpHost httpHost = (HttpHost) executeContext.getArguments()[0];
        HttpRequest httpRequest = (HttpRequest) executeContext.getArguments()[1];

        ApacheHttpClientRequest otelRequest = new ApacheHttpClientRequest(httpHost, httpRequest);
        Context parentContext = OtelContextHelper.currentContext();
        Context context = HttpClientInstrumenter.instrumenter().start(parentContext, otelRequest);
        Scope scope = context.makeCurrent();

        executeContext.setLocalFieldValue("context", context);
        executeContext.setLocalFieldValue("scope", scope);
        executeContext.setLocalFieldValue("otelRequest", otelRequest);

        return executeContext;
    }

    @Override
    public ExecuteContext after(ExecuteContext executeContext) throws Exception {
        HttpUriRequest httpRequest = (HttpUriRequest) executeContext.getArguments()[1];
        HttpResponse response = (HttpResponse) executeContext.getResult();

        Context context = (Context) executeContext.getLocalFieldValue("context");
        Scope scope = (Scope) executeContext.getLocalFieldValue("scope");
        ApacheHttpClientRequest otelRequest = (ApacheHttpClientRequest) executeContext.getLocalFieldValue(
                "otelRequest");
        scope.close();
        HttpClientInstrumenter.instrumenter().end(context, otelRequest, response, null);

        return executeContext;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext executeContext) throws Exception {
        return executeContext;
    }

    private static final TextMapGetter<HttpUriRequest> getter = new TextMapGetter<HttpUriRequest>() {
        @Override
        public Iterable<String> keys(HttpUriRequest carrier) {
            return () -> {
                Header[] allHeaders = carrier.getAllHeaders();
                return allHeaders == null ? Collections.emptyIterator()
                        : Collections.singletonList("user-id").iterator();
            };
        }

        @Override
        public String get(HttpUriRequest carrier, String key) {

            if (carrier.getHeaders(key) == null || carrier.getHeaders(key).length == 0) {
                return "";
            }
            return carrier.getHeaders(key)[0].getValue();
        }
    };
}
