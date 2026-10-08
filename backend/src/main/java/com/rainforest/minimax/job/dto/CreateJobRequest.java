package com.rainforest.minimax.job.dto;

import com.rainforest.minimax.job.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

/**
 * 前端建立任務的請求。依 type 不同，只有對應的欄位會被使用：
 * - TEXT_TO_VIDEO: 只需要 prompt + 通用參數
 * - IMAGE_TO_VIDEO: 需要 imageUrl（起始幀），可選 endImageUrl（結束幀）
 * - REFERENCE_TO_VIDEO: 需要 referenceImageUrls / referenceVideoUrls / referenceAudioUrls 其中至少一項
 */
public class CreateJobRequest {

    @NotNull
    private JobType type;

    @NotBlank
    private String prompt;

    // 通用參數
    private Integer duration;          // 秒，預設 5
    private String resolution;         // 480P / 768P / 2K / 4K
    private String aspectRatio;        // 16:9 / 9:16 / 1:1 / ... / adaptive
    private Integer seed;

    // IMAGE_TO_VIDEO 用
    private String imageUrl;
    private String endImageUrl;

    // REFERENCE_TO_VIDEO 用（各自上限：圖片9、影片3、音訊3，合計不超過12）
    private List<String> referenceImageUrls;
    private List<String> referenceVideoUrls;
    private List<String> referenceAudioUrls;

    public JobType getType() {
        return type;
    }

    public void setType(JobType type) {
        this.type = type;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getAspectRatio() {
        return aspectRatio;
    }

    public void setAspectRatio(String aspectRatio) {
        this.aspectRatio = aspectRatio;
    }

    public Integer getSeed() {
        return seed;
    }

    public void setSeed(Integer seed) {
        this.seed = seed;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEndImageUrl() {
        return endImageUrl;
    }

    public void setEndImageUrl(String endImageUrl) {
        this.endImageUrl = endImageUrl;
    }

    public List<String> getReferenceImageUrls() {
        return referenceImageUrls;
    }

    public void setReferenceImageUrls(List<String> referenceImageUrls) {
        this.referenceImageUrls = referenceImageUrls;
    }

    public List<String> getReferenceVideoUrls() {
        return referenceVideoUrls;
    }

    public void setReferenceVideoUrls(List<String> referenceVideoUrls) {
        this.referenceVideoUrls = referenceVideoUrls;
    }

    public List<String> getReferenceAudioUrls() {
        return referenceAudioUrls;
    }

    public void setReferenceAudioUrls(List<String> referenceAudioUrls) {
        this.referenceAudioUrls = referenceAudioUrls;
    }
}
