package com.publichealth.public_health_api.module.disease.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * 疾病种类实体类
 * 对应数据库表: disease_type
 */
@Entity
@Table(name = "disease_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseType {

    /**
     * 主键ID
     */
    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    /**
     * 疾病编码 (唯一)
     */
    @Column(name = "disease_code", nullable = false, unique = true, length = 50)
    private String diseaseCode;

    /**
     * 疾病名称
     */
    @Column(name = "disease_name", nullable = false, length = 100)
    private String diseaseName;

    /**
     * 所属分类ID
     */
    @Column(name = "category_id", nullable = false, length = 36)
    private String categoryId;

    /**
     * ICD-10编码
     */
    @Column(name = "icd_code", length = 20)
    private String icdCode;

    /**
     * 疾病描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 传染级别: 1-甲类 2-乙类 3-丙类 4-非传染病
     */
    @Column(name = "infectious_level")
    private Integer infectiousLevel;

    /**
     * 是否需要报卡: 1-是 0-否
     */
    @Column(name = "report_required", nullable = false)
    private Integer reportRequired = 1;

    /**
     * 报卡时限（小时）
     */
    @Column(name = "report_deadline")
    private Integer reportDeadline;

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
        if (this.reportRequired == null) {
            this.reportRequired = 1;
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

    /**
     * 传染级别枚举
     */
    public enum InfectiousLevel {
        CLASS_A(1, "甲类传染病"),
        CLASS_B(2, "乙类传染病"),
        CLASS_C(3, "丙类传染病"),
        NON_INFECTIOUS(4, "非传染病");

        private final Integer value;
        private final String description;

        InfectiousLevel(Integer value, String description) {
            this.value = value;
            this.description = description;
        }

        public Integer getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        public static InfectiousLevel fromValue(Integer value) {
            for (InfectiousLevel level : values()) {
                if (level.value.equals(value)) {
                    return level;
                }
            }
            return NON_INFECTIOUS;
        }
    }
}
