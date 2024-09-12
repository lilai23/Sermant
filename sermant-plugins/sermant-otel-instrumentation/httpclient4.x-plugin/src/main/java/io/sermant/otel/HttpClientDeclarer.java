package io.sermant.otel;

import io.sermant.core.plugin.agent.declarer.AbstractPluginDeclarer;
import io.sermant.core.plugin.agent.declarer.InterceptDeclarer;
import io.sermant.core.plugin.agent.matcher.ClassMatcher;
import io.sermant.core.plugin.agent.matcher.MethodMatcher;

/**
 * 模板字节码增强声明
 *
 * @author luanwenfei
 * @since 2023-01-15
 */
public class HttpClientDeclarer extends AbstractPluginDeclarer {
    private static final String[] ENHANCE_CLASSES = {
            "org.apache.http.impl.client.AbstractHttpClient",
            "org.apache.http.impl.client.DefaultRequestDirector",
            "org.apache.http.impl.client.InternalHttpClient",
            "org.apache.http.impl.client.MinimalHttpClient"
    };

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASSES);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameContains("doExecute", "execute")
                                .and(MethodMatcher.paramTypesEqual(
                                        "org.apache.http.HttpHost",
                                        "org.apache.http.HttpRequest",
                                        "org.apache.http.protocol.HttpContext")),
                        new io.sermant.otel.HttpClientInterceptor())
        };
    }
}
