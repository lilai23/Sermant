package com.huawei.discovery.service;

import com.alibaba.fastjson2.JSONObject;
import com.huaweicloud.sermant.core.common.LoggerFactory;
import com.huaweicloud.sermant.core.config.ConfigManager;
import com.huaweicloud.sermant.core.service.send.config.BackendConfig;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 类描述
 *
 * @author lilai
 * @since 2022-12-20
 */
public class ReportServiceImpl implements ReportService{
    private final Logger LOGGER = LoggerFactory.getLogger();

    @Override
    public void report(Map<String, Object> params) {
        HttpClient httpClient = new DefaultHttpClient();
        BackendConfig backendConfig = ConfigManager.getConfig(BackendConfig.class);
        HttpPost httpPost = new HttpPost("http://" + backendConfig.getHttpIp() + ":" + backendConfig.getHttpPort()
                + "/report");
        httpPost.setEntity(new StringEntity(JSONObject.toJSONString(params), ContentType.APPLICATION_JSON));
        try {
            httpClient.execute(httpPost);
            LOGGER.log(Level.INFO, "report showcase message success!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
