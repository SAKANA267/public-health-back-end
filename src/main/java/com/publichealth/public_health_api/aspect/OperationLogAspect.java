package com.publichealth.public_health_api.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.publichealth.public_health_api.context.UserContext;
import com.publichealth.public_health_api.module.operationlog.entity.OperationLog;
import com.publichealth.public_health_api.module.operationlog.enums.OperationStatus;
import com.publichealth.public_health_api.module.operationlog.service.OperationLogService;
import com.publichealth.public_health_api.module.sysuser.entity.SysUser;
import com.publichealth.public_health_api.module.sysuser.repository.SysUserRepository;
import com.publichealth.public_health_api.util.IpUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * 操作日志AOP切面
 * 拦截标注了@OperationLog注解的方法，记录操作日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class OperationLogAspect {

    private final OperationLogService operationLogService;
    private final ObjectMapper objectMapper;
    private final SysUserRepository userRepository;

    /**
     * 定义切点：拦截所有标注了@OperationLog注解的方法
     */
    @Pointcut("@annotation(com.publichealth.public_health_api.annotation.OperationLog)")
    public void operationLogPointcut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("operationLogPointcut() && @annotation(operationLog)")
    public Object around(ProceedingJoinPoint joinPoint, com.publichealth.public_health_api.annotation.OperationLog operationLog) throws Throwable {
        long startTime = System.currentTimeMillis();

        // 获取当前请求
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = method.getName();

        // 构建日志对象
        OperationLog logEntity = new OperationLog();
        logEntity.setUserId(UserContext.getUserId());
        logEntity.setUsername(UserContext.getUsername());
        logEntity.setModule(operationLog.module());
        logEntity.setOperationType(operationLog.operationType());
        logEntity.setOperation(operationLog.description());
        logEntity.setMethod(className + "." + methodName);

        // 获取IP地址和位置
        if (request != null) {
            String ipAddress = IpUtil.getClientIp(request);
            logEntity.setIpAddress(ipAddress);
            logEntity.setLocation(IpUtil.getLocation(ipAddress));
        }

        try {
            // 执行目标方法
            Object result = joinPoint.proceed();

            // 记录成功状态
            logEntity.setStatus(OperationStatus.SUCCESS);

            // 如果需要记录响应
            if (operationLog.logResponse() && result != null) {
                logEntity.setParams(truncateParams(serializeResult(result)));
            }

            return result;

        } catch (Throwable e) {
            // 记录失败状态
            logEntity.setStatus(OperationStatus.FAILURE);
            logEntity.setErrorMsg(truncateErrorMsg(e));

            // 重新抛出异常，不影响业务流程
            throw e;

        } finally {
            // 记录耗时
            long costTime = System.currentTimeMillis() - startTime;
            logEntity.setCostTime(costTime);

            // 如果需要记录参数且还未记录（logResponse=true时已在上面记录）
            if (operationLog.logParams() && logEntity.getParams() == null) {
                logEntity.setParams(truncateParams(serializeParams(joinPoint.getArgs())));
            }

            // 根据userId查询真实的username
            String realUsername = getRealUsername(logEntity.getUserId());
            logEntity.setUsername(realUsername);

            // 异步保存日志
            operationLogService.saveAsync(logEntity);

            log.debug("操作日志记录完成: module={}, operation={}, status={}, costTime={}ms",
                    logEntity.getModule(), logEntity.getOperation(), logEntity.getStatus(), costTime);
        }
    }

    /**
     * 序列化方法参数
     */
    private String serializeParams(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        try {
            // 过滤掉不需要序列化的参数类型（如HttpServletRequest、HttpServletResponse等）
            String params = Arrays.stream(args)
                    .filter(arg -> arg != null &&
                            !isServletRequestOrResponse(arg))
                    .map(arg -> {
                        try {
                            return objectMapper.writeValueAsString(arg);
                        } catch (Exception e) {
                            return arg.toString();
                        }
                    })
                    .collect(Collectors.joining(", "));

            return params.isEmpty() ? "[]" : params;
        } catch (Exception e) {
            log.error("序列化参数失败", e);
            return "参数序列化失败";
        }
    }

    /**
     * 序列化返回结果
     */
    private String serializeResult(Object result) {
        if (result == null) {
            return "null";
        }

        try {
            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            log.error("序列化返回结果失败", e);
            return "返回结果序列化失败";
        }
    }

    /**
     * 判断是否为ServletRequest或Response对象
     */
    private boolean isServletRequestOrResponse(Object arg) {
        return arg instanceof HttpServletRequest ||
                arg instanceof jakarta.servlet.http.HttpServletResponse;
    }

    /**
     * 截断过长的参数（限制2000字符）
     */
    private String truncateParams(String params) {
        if (params == null) {
            return null;
        }
        return params.length() > 2000 ? params.substring(0, 2000) + "..." : params;
    }

    /**
     * 截断错误信息（限制500字符）
     */
    private String truncateErrorMsg(Throwable e) {
        if (e == null) {
            return null;
        }

        String errorMsg = e.getMessage();
        if (errorMsg == null) {
            errorMsg = e.getClass().getSimpleName();
        }

        // 获取堆栈信息（前5行）
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace != null && stackTrace.length > 0) {
            String stackInfo = Arrays.stream(stackTrace)
                    .limit(5)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\n"));
            errorMsg = errorMsg + "\n" + stackInfo;
        }

        return errorMsg.length() > 500 ? errorMsg.substring(0, 500) + "..." : errorMsg;
    }

    /**
     * 根据用户ID或用户名获取真实的用户信息
     */
    private String getRealUsername(String userId) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("用户ID不能为空，请设置请求头 X-User-Id");
        }

        try {
            // UUID格式：通过ID查询
            if (userId.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}")) {
                return userRepository.findUserByIdWithDetails(userId)
                        .map(SysUser::getUsername)
                        .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));
            }

            // 非UUID格式：通过username查询
            return userRepository.findByUsernameAndDeletedFalse(userId)
                    .map(SysUser::getUsername)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + userId));

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("查询用户失败: userId={}", userId, e);
            throw new IllegalArgumentException("查询用户失败: " + userId, e);
        }
    }
}
