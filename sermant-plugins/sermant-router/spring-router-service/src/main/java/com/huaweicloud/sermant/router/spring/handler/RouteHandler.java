package com.huaweicloud.sermant.router.spring.handler;

import java.util.List;
import java.util.Map;

public interface RouteHandler {

    List<Object> handle(String targetName, List<Object> instances, String path,
                        Map<String, List<String>> header);

    int getOrder();
}
