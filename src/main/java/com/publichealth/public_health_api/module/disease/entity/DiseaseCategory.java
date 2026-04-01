package com.publichealth.public_health_api.module.disease.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 疾病分类实体类
 * 对应数据库表: disease_category
 */
@Entity
@Table(name = "disease_category")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseCategory {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    /**
     * 分类编码 (唯一)
     */
    @Column(name = "category_code", nullable = false, unique = true, length = 50)
    private String categoryCode;

    /**
     * 分类名称
     */
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    /**
     * 分类描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 排序序号
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * 状态: 1-启用 0-停用
     */
    @Column(name = "status", nullable = false)
    private Integer status = 1;

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;

    /**
     * 创建时间 (自动填充)
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新人
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;

    /**
     * 更新时间 (自动更新)
     */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记 (0-未删除, 1-已删除)
     */
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;

    /**
     * 生命周期回调: 持久化前生成UUID
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.sortOrder == null) {
            this.sortOrder = 0;
        }
        if (this.status == null) {
            this.status = 1;
        }
    }

    /**
     * 状态枚举
     */
    public enum Status {
        DISABLED(0, "停用"),
        ENABLED(1, "启用");

        private final Integer value;
        private final String description;

        Status(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static Status fromValue(Integer value) {
            for (Status s : values()) {
                if (s.value.equals(value)) {
                    return s;
                }
            }
            return ENABLED;
        }
    }
}
