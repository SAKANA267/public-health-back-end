package com.publichealth.public_health_api.module.sysuser.service.impl;

import com.publichealth.public_health_api.module.assignment.repository.ReportCardAssignmentRepository;
import com.publichealth.public_health_api.module.operationlog.entity.OperationLog;
import com.publichealth.public_health_api.module.operationlog.enums.OperationStatus;
import com.publichealth.public_health_api.module.operationlog.repository.OperationLogRepository;
import com.publichealth.public_health_api.module.sysuser.dto.*;
import com.publichealth.public_health_api.module.sysuser.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户个人中心Service实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final OperationLogRepository operationLogRepository;
    private final ReportCardAssignmentRepository assignmentRepository;

    @Override
    public UserProfileStatisticsDTO getStatistics(String userId) {
        log.info("获取用户统计数据: userId={}", userId);

        UserProfileStatisticsDTO dto = new UserProfileStatisticsDTO();

        // 审核记录数 - 统计用户的总操作数量
        dto.setAuditRecords(operationLogRepository.countByUserId(userId));

        // 完成任务数 - 统计用户完成的审核任务数
        // 这里简化处理，通过操作日志中审核相关的操作来统计
        long completedTasks = operationLogRepository.countByUserAndStatusAndTimeRange(
                userId,
                OperationStatus.SUCCESS,
                LocalDateTime.now().minusMonths(12), // 统计近一年的数据
                LocalDateTime.now()
        );
        dto.setCompletedTasks(completedTasks);

        // 工作时长 - 根据操作日志中的costTime累加（单位：毫秒转换为小时）
        // 这里简化处理，假设每个审核任务平均耗时30分钟
        // 实际应该从工作统计表中获取
        double workHours = completedTasks * 0.5; // 每个任务0.5小时
        dto.setWorkHours(workHours);

        // 审核准确率 - 这里简化处理，默认98.5%
        // 实际应该根据审核结果（驳回率）来计算
        dto.setAccuracyRate(98.5);

        return dto;
    }

    @Override
    public List<ContributionDTO> getContributions(String userId) {
        log.info("获取用户贡献日历数据: userId={}", userId);

        // 统计近365天的数据
        LocalDateTime startDate = LocalDateTime.now().minusDays(365);
        List<Object[]> results = operationLogRepository.countByUserAndDateGroup(userId, startDate);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return results.stream()
                .map(result -> {
                    String date = result[0].toString();
                    Long count = (Long) result[1];
                    return new ContributionDTO(date, count);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<UserRecentActivityDTO> getRecentActivities(String userId, Integer limit) {
        log.info("获取用户最近活动: userId={}, limit={}", userId, limit);

        PageRequest pageRequest = PageRequest.of(0, limit != null ? limit : 10, Sort.by(Sort.Direction.DESC, "createTime"));
        List<OperationLog> logs = operationLogRepository.findRecentByUserId(userId, pageRequest);

        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        return logs.stream()
                .map(log -> {
                    UserRecentActivityDTO dto = new UserRecentActivityDTO();
                    dto.setAction(log.getOperation());
                    // 从操作描述中提取目标对象，或使用模块名称
                    dto.setTarget(log.getModule());
                    dto.setTime(log.getCreateTime().format(formatter));
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
