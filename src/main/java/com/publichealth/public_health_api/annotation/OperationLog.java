package com.publichealth.public_health_api.annotation;

import com.publichealth.public_health_api.module.operationlog.enums.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 标记需要记录操作日志的Controller方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /**
     * 模块名称
     *
     * @return 模块名称
     */
    String module();

    /**
     * 操作类型
     *
     * @return 操作类型
     */
    OperationType operationType();

    /**
     * 操作描述
     *
     * @return 操作描述
     */
    String description() default "";

    /**
     * 是否记录请求参数
     * 对于敏感操作（如修改密码）可以设置为false
     *
     * @return 是否记录参数
     */
    boolean logParams() default true;

    /**
     * 是否记录响应结果
     *
     * @return 是否记录响应
     */
    boolean logResponse() default false;
}
