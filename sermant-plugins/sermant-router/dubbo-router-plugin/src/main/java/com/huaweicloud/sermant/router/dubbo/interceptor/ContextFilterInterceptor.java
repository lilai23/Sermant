/*
 * Copyright (C) 2022-2022 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.huaweicloud.sermant.router.dubbo.interceptor;

import com.huaweicloud.sermant.core.plugin.agent.entity.ExecuteContext;
import com.huaweicloud.sermant.core.plugin.agent.interceptor.AbstractInterceptor;
import com.huaweicloud.sermant.router.common.utils.ThreadLocalUtils;
import com.huaweicloud.sermant.router.dubbo.handler.AbstractContextFilterHandler;
import com.huaweicloud.sermant.router.dubbo.handler.LaneContextFilterHandler;
import com.huaweicloud.sermant.router.dubbo.handler.RouteContextFilterHandler;
import com.huaweicloud.sermant.router.dubbo.utils.DubboReflectUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 增强ContextFilter类的invoke方法
 *
 * @author provenceee
 * @since 2022-09-26
 */
public class ContextFilterInterceptor extends AbstractInterceptor {
    private final List<AbstractContextFilterHandler> handlers;

    /**
     * 构造方法
     */
    public ContextFilterInterceptor() {
        handlers = new ArrayList<>();
        handlers.add(new LaneContextFilterHandler());
        handlers.add(new RouteContextFilterHandler());
        handlers.sort(Comparator.comparingInt(AbstractContextFilterHandler::getOrder));
    }

    @Override
    public ExecuteContext before(ExecuteContext context) {
        Object[] arguments = context.getArguments();
        Object invocation = arguments[1];
        String interfaceName = DubboReflectUtils.getServiceKey(DubboReflectUtils.getUrl(arguments[0]));
        String methodName = DubboReflectUtils.getMethodName(invocation);
        Map<String, Object> attachments = DubboReflectUtils.getAttachments(invocation);
        Object[] args = DubboReflectUtils.getArguments(invocation);
        handlers.forEach(handler -> ThreadLocalUtils
            .addRequestTag(handler.getRequestTag(interfaceName, methodName, attachments, args)));
        return context;
    }

    @Override
    public ExecuteContext after(ExecuteContext context) {
        ThreadLocalUtils.removeRequestTag();
        return context;
    }

    @Override
    public ExecuteContext onThrow(ExecuteContext context) {
        ThreadLocalUtils.removeRequestTag();
        return context;
    }
}