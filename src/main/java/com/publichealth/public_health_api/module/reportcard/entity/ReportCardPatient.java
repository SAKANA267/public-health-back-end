package com.publichealth.public_health_api.module.reportcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 传染病报告卡患者信息实体类
 * 对应数据库表: report_card_patient
 */
@Entity
@Table(name = "report_card_patient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReportCardPatient {

    @Id
    @Column(name = "id", nullable = false, length = 36)
    private String id;

    @Column(name = "report_card_id", nullable = false, length = 36, unique = true)
    private String reportCardId;

    @Column(name = "patient_name", nullable = false, length = 50)
    private String patientName;

    @Column(name = "id_card", nullable = false, length = 18, unique = true)
    private String idCard;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 10)
    private Gender gender;

    @Column(name = "age")
    private Integer age;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "parent_name", length = 50)
    private String parentName;

    @Column(name = "work_unit", length = 100)
    private String workUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", length = 20)
    private AddressType addressType;

    @Column(name = "detail_address", nullable = false, length = 200)
    private String detailAddress;

    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = java.util.UUID.randomUUID().toString();
        }
        if (this.gender == null) {
            this.gender = Gender.MALE;
        }
        if (this.addressType == null) {
            this.addressType = AddressType.COUNTY;
        }
    }

    public enum Gender {
        MALE("男"),
        FEMALE("女");

        private final String description;

        Gender(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum AddressType {
        COUNTY("本县"),
        CITY("本市"),
        PROVINCE("本省"),
        OTHER_PROVINCE("外省"),
        HK_MACAO_TAIWAN("港澳台"),
        FOREIGN("外籍");

        private final String description;

        AddressType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }
}
