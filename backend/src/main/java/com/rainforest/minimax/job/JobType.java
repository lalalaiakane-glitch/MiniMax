package com.rainforest.minimax.job;

public enum JobType {
    TEXT_TO_VIDEO("text-to-video"),
    IMAGE_TO_VIDEO("image-to-video"),
    REFERENCE_TO_VIDEO("reference-to-video");

    private final String falKey;

    JobType(String falKey) {
        this.falKey = falKey;
    }

    /** 對應 application.yml 裡 fal.models 底下的 key。 */
    public String getFalKey() {
        return falKey;
    }
}
