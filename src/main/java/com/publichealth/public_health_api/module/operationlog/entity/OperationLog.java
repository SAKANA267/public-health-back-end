package com.publichealth.public_health_api.module.operationlog.entity;

import com.publichealth.public_health_api.module.operationlog.enums.OperationStatus;
import com.publichealth.public_health_api.module.operationlog.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * 对应数据库表: operation_log
 */
@Entity
@Table(name = "operation_log", indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_module", columnList = "module"),
        @Index(name = "idx_operation_type", columnList = "operation_type"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_create_time", columnList = "create_time")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLog {

    /**
     * 主键ID
     * 使用UUID作为主键
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    /**
     * 操作用户ID
     */
    @Column(name = "user_id", length = 36)
    private String userId;

    /**
     * 操作用户名
     */
    @Column(name = "username", length = 50)
    private String username;

    /**
     * 模块名称
     */
    @Column(name = "module", nullable = false, length = 50)
    private String module;

    /**
     * 操作类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false, length = 20)
    private OperationType operationType;

    /**
     * 操作描述
     */
    @Column(name = "operation", nullable = false, length = 100)
    private String operation;

    /**
     * 请求方法（类名.方法名）
     */
    @Column(name = "method", length = 200)
    private String method;

    /**
     * 请求参数（JSON格式）
     */
    @Column(name = "params", columnDefinition = "TEXT")
    private String params;

    /**
     * IP地址
     */
    @Column(name = "ip_address", length = 50)
    private String ipAddress;

    /**
     * 地理位置
     */
    @Column(name = "location", length = 100)
    private String location;

    /**
     * 操作状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private OperationStatus status;

    /**
     * 错误信息
     */
    @Column(name = "error_msg", columnDefinition = "TEXT")
    private String errorMsg;

    /**
     * 执行耗时（毫秒）
     */
    @Column(name = "cost_time")
    private Long costTime;

    /**
     * 创建时间（日志记录时间）
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 生命周期回调: 持久化前生成UUID
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
    }
}
