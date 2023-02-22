package com.huaweicloud.sermant.router.config.utils;

import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.entity.Match;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.entity.Rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 类描述
 *
 * @author lilai
 * @since 2023-02-21
 */
public class TagRuleUtils extends RuleUtils {
    public static List<Rule> getTagRules(RouterConfiguration configuration, String targetService,
                                         String serviceName) {
        if (RouterConfiguration.isInValid(configuration)) {
            return Collections.emptyList();
        }
        Map<String, Map<String, List<Rule>>> routeRule = configuration.getRouteRule();
        if (CollectionUtils.isEmpty(routeRule)) {
            return Collections.emptyList();
        }
        Map<String, List<Rule>> ruleMap = routeRule.get(targetService);
        if (CollectionUtils.isEmpty(ruleMap)) {
            return Collections.emptyList();
        }
        List<Rule> rules = ruleMap.get(RouterConstant.TAG_MATCH_KIND);
        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyList();
        }

        List<Rule> list = new ArrayList<>();
        for (Rule rule : rules) {
            if (isTargetTagRule(rule, serviceName)) {
                list.add(rule);
            }
        }
        return list;
    }

    private static boolean isTargetTagRule(Rule rule, String serviceName) {
        if (rule == null) {
            return false;
        }
        Match match = rule.getMatch();
        if (match != null) {
            String source = match.getSource();
            if (StringUtils.isExist(source) && !source.equals(serviceName)) {
                return false;
            }
        }
        return !CollectionUtils.isEmpty(rule.getRoute());
    }
}
