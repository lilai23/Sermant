package com.huawei.discovery.service;

import com.huaweicloud.sermant.core.plugin.service.PluginService;

import java.util.Map;

public interface ReportService extends PluginService {
    void report(Map<String, Object> params);
}
