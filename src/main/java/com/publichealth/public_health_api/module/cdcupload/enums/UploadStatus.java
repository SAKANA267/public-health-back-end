package com.publichealth.public_health_api.module.cdcupload.enums;

/**
 * CDC上报状态枚举
 */
public enum UploadStatus {

    NOT_UPLOADED("未上报"),
    UPLOADING("上报中"),
    UPLOADED("已上报"),
    UPLOAD_FAILED("上报失败");

    private final String description;

    UploadStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
