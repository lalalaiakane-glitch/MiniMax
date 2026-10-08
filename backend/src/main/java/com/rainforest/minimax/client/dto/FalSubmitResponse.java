package com.rainforest.minimax.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * fal.ai queue submit 的回應。
 * 官方文件描述的欄位，實際串接時請用真實回應核對、必要時補欄位。
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FalSubmitResponse {
    private String requestId;
    private String statusUrl;
    private String responseUrl;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getStatusUrl() {
        return statusUrl;
    }

    public void setStatusUrl(String statusUrl) {
        this.statusUrl = statusUrl;
    }

    public String getResponseUrl() {
        return responseUrl;
    }

    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }
}
