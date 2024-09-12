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
public class MethodDeclarer extends AbstractPluginDeclarer {
    private static final String[] ENHANCE_CLASSES = {
            "io.demo.template.Test"
    };

    @Override
    public ClassMatcher getClassMatcher() {
        return ClassMatcher.nameContains(ENHANCE_CLASSES);
    }

    @Override
    public InterceptDeclarer[] getInterceptDeclarers(ClassLoader classLoader) {
        return new InterceptDeclarer[]{
                InterceptDeclarer.build(MethodMatcher.nameContains("sendRequest"),
                        new MethodInterceptor())
        };
    }
}
