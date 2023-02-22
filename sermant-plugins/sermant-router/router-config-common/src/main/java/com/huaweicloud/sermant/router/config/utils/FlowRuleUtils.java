package com.huaweicloud.sermant.router.config.utils;

import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.entity.Match;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;
import com.huaweicloud.sermant.router.config.entity.Rule;

import java.util.ArrayList;
import java.util.Arrays;
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
public class FlowRuleUtils extends RuleUtils {
    /**
     * 获取目标规则
     *
     * @param configuration 路由配置
     * @param targetService 目标服务
     * @param path          dubbo接口名/url路径
     * @param serviceName   本服务服务名
     * @return 目标规则
     */
    public static List<Rule> getFlowRules(RouterConfiguration configuration, String targetService, String path,
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
        List<Rule> rules = ruleMap.get(RouterConstant.FLOW_MATCH_KIND);
        if (CollectionUtils.isEmpty(rules)) {
            return Collections.emptyList();
        }

        List<Rule> list = new ArrayList<>();
        for (Rule rule : rules) {
            if (isTargetFlowRule(rule, path, serviceName)) {
                list.add(rule);
            }
        }
        return list;
    }

    private static boolean isTargetFlowRule(Rule rule, String path, String serviceName) {
        if (rule == null) {
            return false;
        }
        Match match = rule.getMatch();
        if (match != null) {
            String source = match.getSource();
            if (StringUtils.isExist(source) && !source.equals(serviceName)) {
                return false;
            }
            String matchPath = match.getPath();
            if (!CollectionUtils.isEmpty(match.getAttachments()) || !CollectionUtils.isEmpty(match.getHeaders())) {
                if (StringUtils.isExist(matchPath) && !Pattern.matches(matchPath, getInterfaceName(path))) {
                    return false;
                }
            } else if (!CollectionUtils.isEmpty(match.getArgs())) {
                if (StringUtils.isBlank(matchPath) || !matchPath.equals(path)) {
                    return false;
                }
            }
        }
        return !CollectionUtils.isEmpty(rule.getRoute());
    }

    /**
     * 在attachment和header规则匹配是，删除接口中的方法名
     *
     * @param path path
     * @return 去除方法名的path
     */
    private static String getInterfaceName(String path) {
        String[] pathList = path.split(":");
        pathList[0] = delMethodName(pathList[0]);
        return String.join(":", pathList);
    }

    /**
     * 删除方法名
     *
     * @param path path
     * @return 删除方法名的path
     */
    private static String delMethodName(String path) {
        ArrayList<String> list = new ArrayList<>(Arrays.asList(path.split("\\.")));
        list.remove(list.size() - 1);
        return String.join(".", list);
    }
}
