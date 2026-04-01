package com.publichealth.public_health_api.module.disease.repository;

import com.publichealth.public_health_api.module.disease.entity.DiseaseType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 疾病种类数据访问层
 */
@Repository
public interface DiseaseTypeRepository extends JpaRepository<DiseaseType, String> {

    /**
     * 根据疾病编码查询
     */
    Optional<DiseaseType> findByDiseaseCode(String diseaseCode);

    /**
     * 根据疾病编码查询未删除的疾病
     */
    Optional<DiseaseType> findByDiseaseCodeAndDeletedFalse(String diseaseCode);

    /**
     * 根据分类ID查询疾病列表
     */
    List<DiseaseType> findByCategoryId(String categoryId);

    /**
     * 根据分类ID查询未删除的疾病列表
     */
    List<DiseaseType> findByCategoryIdAndDeletedFalse(String categoryId);

    /**
     * 根据分类ID查询未删除的疾病列表 (按排序序号)
     */
    List<DiseaseType> findByCategoryIdAndDeletedFalseOrderBySortOrderAsc(String categoryId);

    /**
     * 根据状态查询疾病列表
     */
    List<DiseaseType> findByStatus(Integer status);

    /**
     * 根据状态查询未删除的疾病列表
     */
    List<DiseaseType> findByStatusAndDeletedFalse(Integer status);

    /**
     * 查询所有未删除的疾病
     */
    List<DiseaseType> findByDeletedFalse();

    /**
     * 查询所有未删除的疾病 (分页)
     */
    Page<DiseaseType> findByDeletedFalse(Pageable pageable);

    /**
     * 根据传染级别查询未删除的疾病列表
     */
    List<DiseaseType> findByInfectiousLevelAndDeletedFalse(Integer infectiousLevel);

    /**
     * 搜索疾病 (疾病名称或编码模糊匹配)
     */
    @Query("SELECT dt FROM DiseaseType dt WHERE dt.deleted = false AND " +
           "(LOWER(dt.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dt.diseaseCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dt.icdCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<DiseaseType> searchDiseases(@Param("keyword") String keyword);

    /**
     * 统一条件查询 (支持多条件组合)
     */
    @Query("SELECT dt FROM DiseaseType dt WHERE " +
           "(:keyword IS NULL OR LOWER(dt.diseaseName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dt.diseaseCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dt.icdCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR dt.categoryId = :categoryId) AND " +
           "(:status IS NULL OR dt.status = :status) AND " +
           "(:infectiousLevel IS NULL OR dt.infectiousLevel = :infectiousLevel) AND " +
           "dt.deleted = false")
    Page<DiseaseType> findByConditions(
            @Param("keyword") String keyword,
            @Param("categoryId") String categoryId,
            @Param("status") Integer status,
            @Param("infectiousLevel") Integer infectiousLevel,
            Pageable pageable
    );

    /**
     * 检查疾病编码是否存在
     */
    boolean existsByDiseaseCode(String diseaseCode);

    /**
     * 检查疾病编码是否存在（仅未删除）
     */
    @Query("SELECT CASE WHEN COUNT(dt) > 0 THEN true ELSE false END FROM DiseaseType dt " +
           "WHERE dt.diseaseCode = :diseaseCode AND dt.deleted = false")
    boolean existsByDiseaseCodeAndDeletedFalse(@Param("diseaseCode") String diseaseCode);

    /**
     * 检查疾病编码是否存在 (排除指定ID)
     */
    @Query("SELECT CASE WHEN COUNT(dt) > 0 THEN true ELSE false END FROM DiseaseType dt " +
           "WHERE dt.diseaseCode = :diseaseCode AND dt.id != :id AND dt.deleted = false")
    boolean existsByDiseaseCodeAndIdNot(@Param("diseaseCode") String diseaseCode, @Param("id") String id);

    /**
     * 统计分类下的疾病数量
     */
    @Query("SELECT COUNT(dt) FROM DiseaseType dt WHERE dt.categoryId = :categoryId AND dt.deleted = false")
    long countByCategoryIdAndDeletedFalse(@Param("categoryId") String categoryId);
}
