package com.rainforest.minimax.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FalVideoResult {
    private VideoFile video;
    private String expandedPrompt;

    public VideoFile getVideo() {
        return video;
    }

    public void setVideo(VideoFile video) {
        this.video = video;
    }

    public String getExpandedPrompt() {
        return expandedPrompt;
    }

    public void setExpandedPrompt(String expandedPrompt) {
        this.expandedPrompt = expandedPrompt;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VideoFile {
        private String url;
        private String fileName;
        private Long fileSize;
        private String contentType;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }

        public String getContentType() {
            return contentType;
        }

        public void setContentType(String contentType) {
            this.contentType = contentType;
        }
    }
}
