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
public class MethodInterceptor implements Interceptor {
    @Override
    public ExecuteContext before(ExecuteContext executeContext) throws Exception {
        HttpRequest httpRequest = (HttpRequest) executeContext.getArguments()[0];

        ApacheHttpClientRequest otelRequest = new ApacheHttpClientRequest(null, httpRequest);
        Context parentContext = OtelContextHelper.currentContext();
        Context context = MethodInstrumenter.instrumenter().start(parentContext, otelRequest);
        Scope scope = context.makeCurrent();

        executeContext.setLocalFieldValue("context", context);
        executeContext.setLocalFieldValue("scope", scope);
        executeContext.setLocalFieldValue("otelRequest", otelRequest);

        return executeContext;
    }

    @Override
    public ExecuteContext after(ExecuteContext executeContext) throws Exception {

        Context context = (Context) executeContext.getLocalFieldValue("context");
        Scope scope = (Scope) executeContext.getLocalFieldValue("scope");
        ApacheHttpClientRequest otelRequest = (ApacheHttpClientRequest) executeContext.getLocalFieldValue(
                "otelRequest");
        scope.close();
        MethodInstrumenter.instrumenter().end(context, otelRequest, new Object(), null);

        return executeContext;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext executeContext) throws Exception {
        return executeContext;
    }
}
