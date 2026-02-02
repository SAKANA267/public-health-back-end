package com.publichealth.public_health_api.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应结果
 * @param <T> 列表数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer totalPages;

    /**
     * 数据列表
     */
    private List<T> records;

    /**
     * 是否有上一页
     */
    private Boolean hasPrevious;

    /**
     * 是否有下一页
     */
    private Boolean hasNext;

    /**
     * 构造分页结果
     */
    public static <T> PageResult<T> of(Integer page, Integer size, Long total, List<T> records) {
        PageResult<T> result = new PageResult<>();
        result.setPage(page);
        result.setSize(size);
        result.setTotal(total);
        result.setRecords(records);
        result.setTotalPages((int) Math.ceil((double) total / size));
        result.setHasPrevious(page > 1);
        result.setHasNext(page < result.getTotalPages());
        return result;
    }
}
