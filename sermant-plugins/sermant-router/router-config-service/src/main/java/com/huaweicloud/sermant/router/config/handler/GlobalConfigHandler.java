package com.huaweicloud.sermant.router.config.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEvent;
import com.huaweicloud.sermant.core.service.dynamicconfig.common.DynamicConfigEventType;
import com.huaweicloud.sermant.core.utils.StringUtils;
import com.huaweicloud.sermant.router.common.constants.RouterConstant;
import com.huaweicloud.sermant.router.common.utils.CollectionUtils;
import com.huaweicloud.sermant.router.config.cache.ConfigCache;
import com.huaweicloud.sermant.router.config.entity.EntireRule;
import com.huaweicloud.sermant.router.config.entity.RouterConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 路由配置处理器(全局纬度)
 *
 * @author lilai
 * @since 2023-02-18
 */
public class GlobalConfigHandler extends AbstractConfigHandler {

    @Override
    public void handle(DynamicConfigEvent event, String cacheName) {
        RouterConfiguration configuration = ConfigCache.getLabel(cacheName);
        if (event.getEventType() == DynamicConfigEventType.DELETE) {
            configuration.resetGlobalRule(Collections.emptyList());
//            RuleUtils.initMatchKeys(configuration);
            return;
        }
        List<EntireRule> list = JSONArray.parseArray(JSONObject.toJSONString(getRule(event)), EntireRule.class);
//        RuleUtils.removeInvalidRules(list);
        if (CollectionUtils.isEmpty(list)) {
            configuration.resetGlobalRule(Collections.emptyList());
        } else {
            for (EntireRule rule : list) {
                rule.getRules().sort((o1, o2) -> o2.getPrecedence() - o1.getPrecedence());
            }
            configuration.resetGlobalRule(list);
        }
//        RuleUtils.updateMatchKeys(serviceName, list);
    }

    @Override
    public boolean shouldHandle(String key) {
        return RouterConstant.GLOBAL_ROUTER_KEY.equals(key);
    }

    private List<Map<String, Object>> getRule(DynamicConfigEvent event) {
        String content = event.getContent();
        if (StringUtils.isBlank(content)) {
            return Collections.emptyList();
        }
        Map<String, List<Map<String, Object>>> map = yaml.load(content);
        return map.get(RouterConstant.GLOBAL_ROUTER_KEY);
    }
}
