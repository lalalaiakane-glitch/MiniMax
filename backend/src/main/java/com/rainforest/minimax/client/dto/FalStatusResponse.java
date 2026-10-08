package com.rainforest.minimax.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * fal.ai queue status 的回應。status 常見值：IN_QUEUE / IN_PROGRESS / COMPLETED
 * （未在真實環境驗證過，串接時務必印出原始 JSON 核對。）
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FalStatusResponse {
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isCompleted() {
        return "COMPLETED".equalsIgnoreCase(status);
    }

    public boolean isFailed() {
        return "ERROR".equalsIgnoreCase(status) || "FAILED".equalsIgnoreCase(status);
    }
}
