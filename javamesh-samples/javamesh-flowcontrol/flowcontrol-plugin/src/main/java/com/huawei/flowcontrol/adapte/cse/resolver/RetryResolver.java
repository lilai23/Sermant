/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2021-2022. All rights reserved.
 */

package com.huawei.flowcontrol.adapte.cse.resolver;

import com.huawei.flowcontrol.adapte.cse.rule.RetryRule;

/**
 * 隔熔断配置解析
 *
 * @author zhouss
 * @since 2021-11-16
 */
public class RetryResolver extends AbstractRuleResolver<RetryRule> {
    /**
     * 重试配置 键
     */
    public static final String CONFIG_KEY = "servicecomb.retry";

    public RetryResolver() {
        super(CONFIG_KEY);
    }

    @Override
    protected Class<RetryRule> getRuleClass() {
        return RetryRule.class;
    }
}
