package com.huaweicloud.sermant.router.spring.handler;

import com.huaweicloud.sermant.core.plugin.config.PluginConfigManager;
import com.huaweicloud.sermant.router.common.config.RouterConfig;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.cache.ConfigCache;
import com.huaweicloud.sermant.router.config.entity.Match;
import com.huaweicloud.sermant.router.config.entity.MatchRule;
import com.huaweicloud.sermant.router.config.entity.MatchStrategy;
import com.huaweicloud.sermant.router.config.entity.Route;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.entity.Rule;
import com.huaweicloud.sermant.router.config.entity.ValueMatch;
import com.huaweicloud.sermant.router.config.utils.RuleUtils;
import com.huaweicloud.sermant.router.config.utils.TagRuleUtils;
import com.huaweicloud.sermant.router.spring.cache.AppCache;
import com.huaweicloud.sermant.router.spring.strategy.RuleStrategyHandler;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 类描述
 *
 * @author lilai
 * @since 2023-02-21
 */
public class TagRouteHandler extends AbstractRouteHandler {
    private final RouterConfig routerConfig;

    public TagRouteHandler() {
        routerConfig = PluginConfigManager.getConfig(RouterConfig.class);
    }

    @Override
    public List<Object> handle(String targetName, List<Object> instances, String path,
                               Map<String, List<String>> header) {
        // todo tag筛选实例
//        EnabledStrategy strategy = ConfigCache.getEnabledStrategy(RouterConstant.SPRING_CACHE_NAME);
//        if (shouldHandle(instances) && enabledZoneRouter && strategy.getStrategy()
//                .isMatch(strategy.getValue(), targetName)) {
//            return RuleStrategyHandler.INSTANCE.getZoneInstances(targetName, instances, routerConfig.getZone());
//        }

        return super.handle(targetName, instances, path, header);
    }

    @Override
    public int getOrder() {
        return 2;
    }

    private List<Object> getTargetInstancesByRules(String targetName, List<Object> instances) {
        RouterConfiguration configuration = ConfigCache.getLabel(RouterConstant.SPRING_CACHE_NAME);
        if (RouterConfiguration.isInValid(configuration)) {
            return instances;
        }
        List<Rule> rules = TagRuleUtils.getTagRules(configuration, targetName, AppCache.INSTANCE.getAppName());
        List<Route> routes = getRoutes(rules);
        if (!CollectionUtils.isEmpty(routes)) {
            return RuleStrategyHandler.INSTANCE.getTagMatchInstances(targetName, instances, routes);
        }
        return RuleStrategyHandler.INSTANCE
                .getMismatchInstances(targetName, instances, RuleUtils.getTags(rules, false), true);
    }

    /**
     * 获取匹配的路由
     *
     * @param list 有效的规则
     * @return 匹配的路由
     */
    private List<Route> getRoutes(List<Rule> list) {
        for (Rule rule : list) {
            List<Route> routeList = getRoutes(rule);
            if (!CollectionUtils.isEmpty(routeList)) {
                return routeList;
            }
        }
        return Collections.emptyList();
    }

    private List<Route> getRoutes(Rule rule) {
        Match match = rule.getMatch();
        if (match == null) {
            return rule.getRoute();
        }
        boolean isFullMatch = match.isFullMatch();
        Map<String, List<MatchRule>> tagMatchRules = match.getTags();
        if (CollectionUtils.isEmpty(tagMatchRules)) {
            return rule.getRoute();
        }
        for (Map.Entry<String, List<MatchRule>> entry : tagMatchRules.entrySet()) {
            String key = entry.getKey();
            List<MatchRule> matchRuleList = entry.getValue();
            for (MatchRule matchRule : matchRuleList) {
                ValueMatch valueMatch = matchRule.getValueMatch();
                List<String> values = valueMatch.getValues();
                MatchStrategy matchStrategy = valueMatch.getMatchStrategy();
                String tagValue = routerConfig.getParameters().get(key);
                if (!isFullMatch && matchStrategy.isMatch(values, tagValue, matchRule.isCaseInsensitive())) {
                    // 如果不是全匹配，且匹配了一个，那么直接return
                    return rule.getRoute();
                }
                if (isFullMatch && !matchStrategy.isMatch(values, tagValue, matchRule.isCaseInsensitive())) {
                    // 如果是全匹配，且有一个不匹配，则继续下一个规则
                    return Collections.emptyList();
                }
            }
        }
        if (isFullMatch) {
            // 如果是全匹配，走到这里，说明没有不匹配的，直接return
            return rule.getRoute();
        }

        // 如果不是全匹配，走到这里，说明没有一个规则能够匹配上，则继续下一个规则
        return Collections.emptyList();
    }
}
