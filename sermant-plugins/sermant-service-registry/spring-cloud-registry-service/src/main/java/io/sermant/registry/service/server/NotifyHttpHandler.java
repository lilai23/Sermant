/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Based on org/apache/dubbo/remoting/exchange/support/header/HeaderExchangeServer.java
 * from the Apache Dubbo project.
 */

package io.sermant.registry.service.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import io.sermant.core.common.LoggerFactory;
import io.sermant.core.utils.StringUtils;
import io.sermant.registry.config.grace.GraceConstants;
import io.sermant.registry.config.grace.GraceContext;
import io.sermant.registry.utils.RefreshUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * HTTP processor
 *
 * @author provenceee
 * @since 2022-05-27
 */
public class NotifyHttpHandler implements HttpHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        LOGGER.info("NotifyHttpHandler start process");
        try {
            if (GraceConstants.GRACE_HTTP_METHOD_POST.equalsIgnoreCase(exchange.getRequestMethod())) {
                String serviceName = exchange.getRequestHeaders().getFirst(GraceConstants.MARK_SHUTDOWN_SERVICE_NAME);
                LOGGER.info("NotifyHttpHandler serviceName:" + serviceName);
                addMarkShutdownEndpoint(exchange.getRequestHeaders());
                if (StringUtils.isBlank(serviceName)) {
                    LOGGER.warning("ServiceName is empty.");
                    return;
                }
                LOGGER.warning(String.format(Locale.ROOT, "Service[%s] has been offline.", serviceName));
                RefreshUtils.refreshTargetServiceInstances(serviceName);
                OutputStream responseBody = exchange.getResponseBody();
                exchange.sendResponseHeaders(GraceConstants.GRACE_HTTP_SUCCESS_CODE,
                        GraceConstants.GRACE_OFFLINE_SUCCESS_MSG.length());
                responseBody.write(GraceConstants.GRACE_OFFLINE_SUCCESS_MSG.getBytes(StandardCharsets.UTF_8));
                responseBody.flush();
            }
        } finally {
            exchange.close();
        }
    }

    private void addMarkShutdownEndpoint(Headers headers) {
        GraceContext.INSTANCE.getGraceShutDownManager()
                .addShutdownEndpoints(headers.get(GraceConstants.MARK_SHUTDOWN_SERVICE_ENDPOINT));
    }
}
