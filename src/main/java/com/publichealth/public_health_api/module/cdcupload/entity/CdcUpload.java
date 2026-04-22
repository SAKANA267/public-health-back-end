package com.publichealth.public_health_api.module.cdcupload.entity;

import com.publichealth.public_health_api.module.cdcupload.enums.UploadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * CDC上报记录实体类
 * 对应数据库表: cdc_upload
 */
@Entity
@Table(name = "cdc_upload")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CdcUpload {

    // ============================================
    // 主键字段
    // ============================================

    @Id
    @Column(name = "id", nullable = false, length = 64)
    private String id;

    // ============================================
    // 关联字段
    // ============================================

    /**
     * 报告卡ID（关联 report_card 表，唯一约束）
     */
    @Column(name = "report_card_id", nullable = false, length = 64, unique = true)
    private String reportCardId;

    // ============================================
    // 上报信息字段
    // ============================================

    /**
     * 上报状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "upload_status", nullable = false, length = 20)
    private UploadStatus uploadStatus = UploadStatus.NOT_UPLOADED;

    /**
     * 上报时间
     */
    @Column(name = "upload_time")
    private LocalDateTime uploadTime;

    /**
     * 上报操作人ID
     */
    @Column(name = "upload_operator", length = 64)
    private String uploadOperator;

    /**
     * 上报操作人姓名（冗余字段，避免频繁联表查询）
     */
    @Column(name = "upload_operator_name", length = 50)
    private String uploadOperatorName;

    /**
     * 失败原因
     */
    @Column(name = "fail_reason", length = 500)
    private String failReason;

    /**
     * 重试次数
     */
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    /**
     * CDC系统返回的流水号，用于回溯查询
     */
    @Column(name = "cdc_serial_no", length = 100)
    private String cdcSerialNo;

    // ============================================
    // 时间戳字段
    // ============================================

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    // ============================================
    // 逻辑删除标记
    // ============================================

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    // ============================================
    // 生命周期回调
    // ============================================

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.uploadStatus == null) {
            this.uploadStatus = UploadStatus.NOT_UPLOADED;
        }
        if (this.retryCount == null) {
            this.retryCount = 0;
        }
    }
}
