-- ============================================================
-- 操作日志表 (operation_log)
-- ============================================================

CREATE TABLE IF NOT EXISTS operation_log (
    id VARCHAR(36) PRIMARY KEY COMMENT '主键ID',
    user_id VARCHAR(36) COMMENT '操作用户ID',
    username VARCHAR(50) COMMENT '操作用户名',
    module VARCHAR(50) NOT NULL COMMENT '模块名称',
    operation_type VARCHAR(20) NOT NULL COMMENT '操作类型',
    operation VARCHAR(100) NOT NULL COMMENT '操作描述',
    method VARCHAR(200) COMMENT '请求方法',
    params TEXT COMMENT '请求参数',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    location VARCHAR(100) COMMENT '地理位置',
    status VARCHAR(20) NOT NULL COMMENT '操作状态',
    error_msg TEXT COMMENT '错误信息',
    cost_time BIGINT COMMENT '执行耗时(ms)',
    create_time DATETIME NOT NULL COMMENT '创建时间',

    INDEX idx_user_id (user_id),
    INDEX idx_module (module),
    INDEX idx_operation_type (operation_type),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================================
-- 操作类型说明 (operation_type)
-- ============================================================
-- CREATE: 创建操作
-- UPDATE: 更新操作
-- DELETE: 删除操作
-- QUERY: 查询操作
-- EXPORT: 导出操作
-- IMPORT: 导入操作
-- LOGIN: 登录操作
-- LOGOUT: 登出操作
-- AUDIT: 审核操作
-- OTHER: 其他操作

-- ============================================================
-- 操作状态说明 (status)
-- ============================================================
-- SUCCESS: 操作成功
-- FAILURE: 操作失败
-- PARTIAL: 部分成功
