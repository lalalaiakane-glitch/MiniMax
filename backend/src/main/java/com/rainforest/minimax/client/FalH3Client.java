package com.rainforest.minimax.client;

import com.rainforest.minimax.client.dto.FalStatusResponse;
import com.rainforest.minimax.client.dto.FalSubmitResponse;
import com.rainforest.minimax.client.dto.FalVideoResult;
import com.rainforest.minimax.config.FalProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

/**
 * 唯一負責跟 fal.ai 的 MiniMax H3 API 溝通的地方。
 * 其他程式碼一律透過這個 client，不要自己組 fal.ai 的網址。
 *
 * 流程：submit -> pollStatus（重複呼叫直到 completed/failed） -> getResult
 * 目前為同步阻塞呼叫（.block()），MVP 先求簡單；若之後任務量大，
 * 建議改成非阻塞或搭配訊息佇列/排程器。
 */
@Component
public class FalH3Client {

    private static final Logger log = LoggerFactory.getLogger(FalH3Client.class);

    private final WebClient webClient;
    private final FalProperties properties;

    public FalH3Client(WebClient falWebClient, FalProperties properties) {
        this.webClient = falWebClient;
        this.properties = properties;
    }

    public String resolveModelId(String type) {
        String modelId = properties.getModels().get(type);
        if (modelId == null) {
            throw new IllegalArgumentException("未知的生成類型，找不到對應的 fal model id: " + type);
        }
        return modelId;
    }

    /**
     * 提交一個生成任務到 fal.ai 的 queue。
     *
     * @param modelId 例如 "minimax/h3/text-to-video"
     * @param payload 依 fal.ai 該 model 的參數格式（prompt、resolution、aspect_ratio... ）
     */
    public FalSubmitResponse submit(String modelId, Map<String, Object> payload) {
        log.debug("Submitting to fal.ai model={} payload={}", modelId, payload);
        FalSubmitResponse response = webClient.post()
                // 直接用字串拼接路徑，不要用 "/{modelId}" 這種 URI 樣板變數寫法——
                // Spring 會把變數值當成單一路徑片段編碼，導致 modelId 裡的 "/" 被轉成 %2F，
                // 打到 fal.ai 會變成 404（實際踩過這個坑）。
                .uri("/" + modelId)
                .bodyValue(payload)
                .retrieve()
                .bodyToMono(FalSubmitResponse.class)
                .block();
        log.info("fal.ai submit response: requestId={}", response != null ? response.getRequestId() : null);
        return response;
    }

    /**
     * 用完整的 status_url 查詢目前狀態（fal.ai 回傳的是完整絕對網址，直接用另一個 WebClient 呼叫）。
     */
    public FalStatusResponse pollStatus(String statusUrl) {
        return WebClient.create()
                .get()
                .uri(statusUrl)
                .header("Authorization", "Key " + properties.getApiKey())
                .retrieve()
                .bodyToMono(FalStatusResponse.class)
                .block();
    }

    /**
     * 用完整的 response_url 取回最終結果。
     */
    public FalVideoResult getResult(String responseUrl) {
        return WebClient.create()
                .get()
                .uri(responseUrl)
                .header("Authorization", "Key " + properties.getApiKey())
                .retrieve()
                .bodyToMono(FalVideoResult.class)
                .block();
    }
}
