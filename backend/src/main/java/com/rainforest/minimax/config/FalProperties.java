package com.rainforest.minimax.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

/**
 * 對應 application.yml 裡的 fal.* 設定。
 * apiKey 一律用環境變數 FAL_KEY 注入，不要寫死在設定檔或程式碼裡。
 */
@ConfigurationProperties(prefix = "fal")
public class FalProperties {

    private String apiKey;
    private String baseUrl;
    private Map<String, String> models;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, String> getModels() {
        return models;
    }

    public void setModels(Map<String, String> models) {
        this.models = models;
    }
}
