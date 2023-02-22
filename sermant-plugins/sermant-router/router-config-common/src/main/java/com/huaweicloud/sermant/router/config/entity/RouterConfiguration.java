/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package com.huaweicloud.sermant.router.config.entity;

import com.huaweicloud.sermant.router.common.utils.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 路由标签
 *
 * @author provenceee
 * @since 2021-10-27
 */
public class RouterConfiguration {
    /**
     * 服务的标签规则,外层key为服务名，内层key为标签规则类型kind，内层value为该类型标签路由的具体规则
     */
    private final Map<String, Map<String, List<Rule>>> rules = new ConcurrentHashMap<>();

    /**
     * 全局标签规则,key为标签规则类型kind，value为该类型标签路由的具体规则
     */
    private Map<String, List<Rule>> globalRules = new ConcurrentHashMap<>();

    public Map<String, Map<String, List<Rule>>> getRouteRule() {
        return rules;
    }

    public void updateServiceRule(String serviceName, List<EntireRule> entireRules) {
        Map<String, List<Rule>> serviceRules = rules.get(serviceName);
        if (serviceRules == null) {
            serviceRules = new ConcurrentHashMap<>();
        }
        serviceRules.clear();
        for (EntireRule entireRule : entireRules) {
            serviceRules.put(entireRule.getKind(), entireRule.getRules());
        }
    }

    public void removeServiceRule(String serviceName) {
        rules.remove(serviceName);
    }

    /**
     * 重置路由规则
     *
     * @param map 路由规则
     */
    public void resetRouteRule(Map<String, List<EntireRule>> map) {
        rules.clear();
        for (String serviceName : map.keySet()) {
            Map<String, List<Rule>> serviceRules = new ConcurrentHashMap<>();
            for (EntireRule entireRule : map.get(serviceName)) {
                serviceRules.put(entireRule.getKind(), entireRule.getRules());
            }
            rules.put(serviceName, serviceRules);
        }
    }

    public void resetGlobalRule(List<EntireRule> list) {
        globalRules.clear();
        for (EntireRule entireRule : list) {
            globalRules.put(entireRule.getKind(), entireRule.getRules());
        }
    }

    /**
     * 路由规则是否无效
     *
     * @param configuration 路由规则
     * @return 是否无效
     */
    public static boolean isInValid(RouterConfiguration configuration) {
        return configuration == null || CollectionUtils.isEmpty(configuration.getRouteRule());
    }
}