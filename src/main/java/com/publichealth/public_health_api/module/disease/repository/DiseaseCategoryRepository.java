package com.publichealth.public_health_api.module.disease.repository;

import com.publichealth.public_health_api.module.disease.entity.DiseaseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 疾病分类数据访问层
 */
@Repository
public interface DiseaseCategoryRepository extends JpaRepository<DiseaseCategory, String> {

    /**
     * 根据分类编码查询
     */
    Optional<DiseaseCategory> findByCategoryCode(String categoryCode);

    /**
     * 根据分类编码查询未删除的分类
     */
    Optional<DiseaseCategory> findByCategoryCodeAndDeletedFalse(String categoryCode);

    /**
     * 根据状态查询分类列表
     */
    List<DiseaseCategory> findByStatus(Integer status);

    /**
     * 根据状态查询未删除的分类列表
     */
    List<DiseaseCategory> findByStatusAndDeletedFalse(Integer status);

    /**
     * 查询所有未删除的分类
     */
    List<DiseaseCategory> findByDeletedFalse();

    /**
     * 查询所有未删除的分类 (分页)
     */
    Page<DiseaseCategory> findByDeletedFalse(Pageable pageable);

    /**
     * 按排序序号查询未删除的分类列表
     */
    List<DiseaseCategory> findByDeletedFalseOrderBySortOrderAsc();

    /**
     * 搜索分类 (分类名称或编码模糊匹配)
     */
    @Query("SELECT dc FROM DiseaseCategory dc WHERE dc.deleted = false AND " +
           "(LOWER(dc.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dc.categoryCode) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<DiseaseCategory> searchCategories(@Param("keyword") String keyword);

    /**
     * 统一条件查询 (支持多条件组合)
     */
    @Query("SELECT dc FROM DiseaseCategory dc WHERE " +
           "(:keyword IS NULL OR LOWER(dc.categoryName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(dc.categoryCode) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR dc.status = :status) AND " +
           "dc.deleted = false")
    Page<DiseaseCategory> findByConditions(
            @Param("keyword") String keyword,
            @Param("status") Integer status,
            Pageable pageable
    );

    /**
     * 检查分类编码是否存在
     */
    boolean existsByCategoryCode(String categoryCode);

    /**
     * 检查分类编码是否存在（仅未删除）
     */
    @Query("SELECT CASE WHEN COUNT(dc) > 0 THEN true ELSE false END FROM DiseaseCategory dc " +
           "WHERE dc.categoryCode = :categoryCode AND dc.deleted = false")
    boolean existsByCategoryCodeAndDeletedFalse(@Param("categoryCode") String categoryCode);

    /**
     * 检查分类编码是否存在 (排除指定ID)
     */
    @Query("SELECT CASE WHEN COUNT(dc) > 0 THEN true ELSE false END FROM DiseaseCategory dc " +
           "WHERE dc.categoryCode = :categoryCode AND dc.id != :id AND dc.deleted = false")
    boolean existsByCategoryCodeAndIdNot(@Param("categoryCode") String categoryCode, @Param("id") String id);
}
