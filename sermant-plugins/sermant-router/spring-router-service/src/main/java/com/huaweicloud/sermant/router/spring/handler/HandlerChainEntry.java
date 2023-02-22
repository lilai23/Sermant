package com.huaweicloud.sermant.router.spring.handler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * 类描述
 *
 * @author lilai
 * @since 2023-02-21
 */
public enum HandlerChainEntry {

    INSTANCE;

    private static final int HANDLER_SIZE = 4;

    private static final HandlerChain HANDLER_CHAIN = new HandlerChain();

    static {
        final List<AbstractRouteHandler> handlers = new ArrayList<>(HANDLER_SIZE);
        for (AbstractRouteHandler handler : ServiceLoader.load(AbstractRouteHandler.class,
                HandlerChainEntry.class.getClassLoader())) {
            handlers.add(handler);
        }
        Collections.sort(handlers);
        handlers.forEach(HANDLER_CHAIN::addLastHandler);
    }

    public List<Object> process(String targetName, List<Object> instances, String path,
                                Map<String, List<String>> header) {
        return HANDLER_CHAIN.handle(targetName, instances, path, header);
    }
}
