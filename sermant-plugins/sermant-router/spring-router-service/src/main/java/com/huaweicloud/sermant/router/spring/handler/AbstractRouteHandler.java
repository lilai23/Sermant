package com.huaweicloud.sermant.router.spring.handler;

import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author lilai
 * @since 2023-02-21
 */
public abstract class AbstractRouteHandler implements RouteHandler, Comparable<AbstractRouteHandler> {
    private AbstractRouteHandler next;

    @Override
    public List<Object> handle(String targetName, List<Object> instances, String path,
                               Map<String, List<String>> header) {
        if (next != null) {
            return next.handle(targetName, instances, path, header);
        }
        return instances;
    }

    @Override
    public int compareTo(AbstractRouteHandler handler) {
        return getOrder() - handler.getOrder();
    }

    public void setNext(AbstractRouteHandler handler) {
        this.next = handler;
    }

    boolean shouldHandle(List<Object> instances) {
        // 实例数大于1才能路由
        return instances != null && instances.size() > 1;
    }
}
