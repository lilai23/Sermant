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

package io.sermant.registry.inject.grace;

import io.sermant.core.common.LoggerFactory;
import io.sermant.core.plugin.config.PluginConfigManager;
import io.sermant.core.plugin.service.PluginServiceManager;
import io.sermant.registry.config.ConfigConstants;
import io.sermant.registry.config.GraceConfig;
import io.sermant.registry.config.grace.GraceContext;
import io.sermant.registry.services.GraceService;
import io.sermant.registry.utils.CommonUtils;

import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Spring closes the time listener
 *
 * @author provenceee
 * @since 2022-05-25
 */
@Component
public class ContextClosedEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger();

    private GraceService graceService;

    private GraceConfig graceConfig;

    /**
     * Constructor
     */
    public ContextClosedEventListener() {
        try {
            graceService = PluginServiceManager.getPluginService(GraceService.class);
            graceConfig = PluginConfigManager.getPluginConfig(GraceConfig.class);
        } catch (IllegalArgumentException exception) {
            LOGGER.severe("graceService is not enabled");
            graceService = null;
        }
    }

    /**
     * ContextClosedEvent Event listeners
     */
    @EventListener(value = ContextClosedEvent.class)
    public void listener() {
        System.out.println("###");
        LOGGER.info("###");
        if (!isEnableGraceDown() || graceService == null) {
            return;
        }
        graceService.shutdown();

        CommonUtils.sleep(1000);
        if (graceConfig.isEnableSpring() && graceConfig.isEnableGraceShutdown()) {
            GraceContext.INSTANCE.getGraceShutDownManager().setShutDown(true);
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
        }
    }

    private boolean isEnableGraceDown() {
        GraceConfig graceConfig = PluginConfigManager.getPluginConfig(GraceConfig.class);
        return graceConfig.isEnableSpring() && graceConfig.isEnableGraceShutdown() && graceConfig
                .isEnableOfflineNotify();
    }
}
