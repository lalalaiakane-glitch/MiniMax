package com.rainforest.minimax.job.dto;

import com.rainforest.minimax.job.GenerationJob;
import com.rainforest.minimax.job.JobStatus;
import com.rainforest.minimax.job.JobType;

import java.time.Instant;

public class JobResponse {
    private Long id;
    private JobType type;
    private String prompt;
    private JobStatus status;
    private String videoUrl;
    private String errorMessage;
    private Instant createdAt;
    private Instant updatedAt;

    public static JobResponse from(GenerationJob job) {
        JobResponse dto = new JobResponse();
        dto.id = job.getId();
        dto.type = job.getType();
        dto.prompt = job.getPrompt();
        dto.status = job.getStatus();
        dto.videoUrl = job.getVideoUrl();
        dto.errorMessage = job.getErrorMessage();
        dto.createdAt = job.getCreatedAt();
        dto.updatedAt = job.getUpdatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public JobType getType() {
        return type;
    }

    public String getPrompt() {
        return prompt;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
