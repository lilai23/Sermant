package com.huaweicloud.sermant.router.config.entity;

import java.util.List;
import java.util.Map;

/**
 * 整体规则实体
 *
 * @author lilai
 * @since 2023-02-18
 */
public class EntireRule {
    private String kind;

    private String description;

    private List<Rule> rules;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }
}
