package com.rainforest.minimax.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rainforest.minimax.client.FalH3Client;
import com.rainforest.minimax.client.dto.FalStatusResponse;
import com.rainforest.minimax.client.dto.FalSubmitResponse;
import com.rainforest.minimax.client.dto.FalVideoResult;
import com.rainforest.minimax.job.dto.CreateJobRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JobService {

    private static final Logger log = LoggerFactory.getLogger(JobService.class);

    private final JobRepository jobRepository;
    private final FalH3Client falH3Client;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JobService(JobRepository jobRepository, FalH3Client falH3Client) {
        this.jobRepository = jobRepository;
        this.falH3Client = falH3Client;
    }

    /**
     * 建立任務、組出 fal.ai 需要的參數、提交，並存下第一次的狀態。
     * 提交失敗（例如還沒設定 FAL_KEY）時，任務會被存成 FAILED，不會拋例外中斷 API。
     */
    public GenerationJob createAndSubmit(CreateJobRequest request) {
        Map<String, Object> payload = buildPayload(request);

        GenerationJob job = new GenerationJob();
        job.setType(request.getType());
        job.setPrompt(request.getPrompt());
        job.setParamsJson(toJson(payload));
        job.setStatus(JobStatus.PENDING);
        job = jobRepository.save(job);

        try {
            String modelId = falH3Client.resolveModelId(request.getType().getFalKey());
            FalSubmitResponse submitResponse = falH3Client.submit(modelId, payload);
            job.setFalRequestId(submitResponse.getRequestId());
            job.setStatusUrl(submitResponse.getStatusUrl());
            job.setResponseUrl(submitResponse.getResponseUrl());
            job.setStatus(JobStatus.IN_PROGRESS);
        } catch (Exception e) {
            log.error("提交任務到 fal.ai 失敗，job id={}", job.getId(), e);
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage("提交失敗：" + e.getMessage());
        }

        return jobRepository.save(job);
    }

    public java.util.List<GenerationJob> listAll() {
        return jobRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * 取得單一任務；若目前狀態是 IN_PROGRESS，先去 fal.ai 問一次最新狀態再回傳。
     */
    public GenerationJob getAndRefresh(Long id) {
        GenerationJob job = jobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到任務 id=" + id));

        if (job.getStatus() != JobStatus.IN_PROGRESS) {
            return job;
        }

        try {
            FalStatusResponse status = falH3Client.pollStatus(job.getStatusUrl());
            if (status.isCompleted()) {
                FalVideoResult result = falH3Client.getResult(job.getResponseUrl());
                job.setVideoUrl(result.getVideo() != null ? result.getVideo().getUrl() : null);
                job.setStatus(JobStatus.SUCCEEDED);
            } else if (status.isFailed()) {
                job.setStatus(JobStatus.FAILED);
                job.setErrorMessage("fal.ai 回報生成失敗");
            }
            // 其餘狀態（IN_QUEUE / IN_PROGRESS）維持不變，前端稍後再輪詢
        } catch (Exception e) {
            log.warn("查詢 fal.ai 狀態失敗，job id={}", job.getId(), e);
            // 查詢失敗不代表任務失敗，先維持原狀態，讓下次輪詢再試
        }

        return jobRepository.save(job);
    }

    private Map<String, Object> buildPayload(CreateJobRequest request) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("prompt", request.getPrompt());
        putIfNotNull(payload, "duration", request.getDuration());
        putIfNotNull(payload, "resolution", request.getResolution());
        putIfNotNull(payload, "aspect_ratio", request.getAspectRatio());
        putIfNotNull(payload, "seed", request.getSeed());

        switch (request.getType()) {
            case IMAGE_TO_VIDEO -> {
                putIfNotNull(payload, "image_url", request.getImageUrl());
                putIfNotNull(payload, "end_image_url", request.getEndImageUrl());
            }
            case REFERENCE_TO_VIDEO -> {
                putIfNotNull(payload, "reference_image_urls", request.getReferenceImageUrls());
                putIfNotNull(payload, "reference_video_urls", request.getReferenceVideoUrls());
                putIfNotNull(payload, "reference_audio_urls", request.getReferenceAudioUrls());
            }
            case TEXT_TO_VIDEO -> {
                // 只需要通用參數，這裡不用額外處理
            }
        }
        return payload;
    }

    private void putIfNotNull(Map<String, Object> map, String key, Object value) {
        if (value != null) {
            map.put(key, value);
        }
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return String.valueOf(value);
        }
    }
}
