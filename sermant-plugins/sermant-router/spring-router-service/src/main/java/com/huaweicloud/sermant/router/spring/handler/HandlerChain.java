package com.huaweicloud.sermant.router.spring.handler;

/**
 * 类描述
 *
 * @author lilai
 * @since 2023-02-21
 */
public class HandlerChain extends AbstractRouteHandler {
    private AbstractRouteHandler tail;

    public void addLastHandler(AbstractRouteHandler handler) {
        if (tail == null) {
            tail = handler;
            setNext(handler);
            return;
        }
        tail.setNext(handler);
        tail = handler;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
