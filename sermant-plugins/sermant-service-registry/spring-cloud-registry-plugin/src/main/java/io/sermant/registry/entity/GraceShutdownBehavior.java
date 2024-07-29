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
 * Based on org/apache/dubbo/config/DubboShutdownHook.java
 * from the Apache Dubbo project.
 */

package io.sermant.registry.entity;

import io.sermant.core.common.LoggerFactory;
import io.sermant.core.plugin.config.PluginConfigManager;
import io.sermant.registry.config.ConfigConstants;
import io.sermant.registry.config.GraceConfig;
import io.sermant.registry.config.grace.GraceContext;
import io.sermant.registry.utils.CommonUtils;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Graceful closing behavior
 *
 * @author zhouss
 * @since 2022-06-24
 */
public class GraceShutdownBehavior implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private final GraceConfig graceConfig;

    GraceShutdownBehavior() {
        graceConfig = PluginConfigManager.getPluginConfig(GraceConfig.class);
    }

    @Override
    public void run() {
        if (graceConfig.isEnableSpring() && graceConfig.isEnableGraceShutdown()) {
            GraceContext.INSTANCE.getGraceShutDownManager().setShutDown(true);
            graceShutDown();
        }
    }

    private void graceShutDown() {
        CommonUtils.sleep(30000);
        long shutdownWaitTime = graceConfig.getShutdownWaitTime() * ConfigConstants.SEC_DELTA;
        final long shutdownCheckTimeUnit = graceConfig.getShutdownCheckTimeUnit() * ConfigConstants.SEC_DELTA;
        while (GraceContext.INSTANCE.getGraceShutDownManager().getRequestCount() > 0 && shutdownWaitTime > 0) {
            LOGGER.info(String.format(Locale.ENGLISH, "Wait all request complete , remained count [%s]",
                    GraceContext.INSTANCE.getGraceShutDownManager().getRequestCount()));
            CommonUtils.sleep(shutdownCheckTimeUnit);
            shutdownWaitTime -= shutdownCheckTimeUnit;
        }
        final int requestCount = GraceContext.INSTANCE.getGraceShutDownManager().getRequestCount();
        if (requestCount > 0) {
            LOGGER.warning(String.format(Locale.ENGLISH, "Request num that does not completed is [%s] ", requestCount));
        } else {
            LOGGER.fine("Graceful shutdown completed!");
        }
        CommonUtils.sleep(30000);
    }
}
