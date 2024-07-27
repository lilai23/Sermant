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
 * Based on org/apache/dubbo/rpc/cluster/loadbalance/ShortestResponseLoadBalance.java
 * from the Apache Dubbo project.
 */

package io.sermant.registry.grace.interceptors;

import com.netflix.loadbalancer.Server;

import io.sermant.core.common.LoggerFactory;
import io.sermant.core.plugin.agent.entity.ExecuteContext;
import io.sermant.core.utils.StringUtils;
import io.sermant.registry.config.grace.GraceContext;
import io.sermant.registry.config.grace.GraceHelper;
import io.sermant.registry.config.grace.GraceShutDownManager;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.zookeeper.discovery.ZookeeperServer;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Spring selects an instance based on the warm-up parameters
 *
 * @author zhouss
 * @since 2022-05-17
 */
public class SpringRibbonWarmUpInterceptor extends GraceSwitchInterceptor {
    private static final String ZOOKEEPER_SERVER_NAME = "org.springframework.cloud.zookeeper.discovery.ZookeeperServer";
    private static final Logger LOGGER = LoggerFactory.getLogger();
    @Override
    protected ExecuteContext doAfter(ExecuteContext context) {
        final Object result = context.getResult();
        if (!(result instanceof List)) {
            return context;
        }
        List<Server> serverList = (List<Server>) result;
        if (serverList.size() <= 1) {
            return context;
        }
        List<Server> filterServerList = filterOfflineInstance(serverList);
        if (filterServerList.size() == 1 || !graceConfig.isEnableWarmUp()) {
            context.changeResult(filterServerList);
            return context;
        }
        boolean isAllWarmed = true;
        int[] weights = new int[filterServerList.size()];
        int totalWeight = 0;
        int index = 0;
        for (Server server : filterServerList) {
            final Map<String, String> metadata = getMetadata(server);
            final boolean isWarmed = calculate(metadata, weights, index);
            isAllWarmed &= isWarmed;
            totalWeight += weights[index++];
            if (!isWarmed) {
                warmMessage(server.getHost(), server.getPort());
            }
        }
        if (!isAllWarmed) {
            final Optional<Object> chooseResult = chooseServer(totalWeight, weights, filterServerList);
            chooseResult.ifPresent(server -> context.changeResult(Collections.singletonList(server)));
        }
        return context;
    }

    /**
     * Instances that have been notified to go offline are directly removed
     *
     * @param serverList Original serverList
     * @return Filtered services
     */
    private List<Server> filterOfflineInstance(List<Server> serverList) {
        LOGGER.info("process filter" + graceConfig.isEnableGraceShutdown());
        if (graceConfig.isEnableGraceShutdown()) {
            final GraceShutDownManager graceShutDownManager = GraceContext.INSTANCE.getGraceShutDownManager();
            List<Server> collect = serverList.stream()
                    .filter(server -> {
                        LOGGER.info("before" + server.getPort());
                        return !graceShutDownManager.isMarkedOffline(
                                buildEndpoint(server.getHost(), server.getPort()));
                    })
                    .collect(Collectors.toList());
            collect.forEach(server -> LOGGER.info("after" +server.getPort()));
            return collect;
        }
        return serverList;
    }

    private Map<String, String> getMetadata(Server server) {
        if (StringUtils.equals(ZOOKEEPER_SERVER_NAME, server.getClass().getName())
                && server instanceof ZookeeperServer) {
            ZookeeperServer zookeeperServer = (ZookeeperServer) server;
            return zookeeperServer.getInstance().getPayload().getMetadata();
        }
        return GraceHelper.getMetadata(server);
    }
}
