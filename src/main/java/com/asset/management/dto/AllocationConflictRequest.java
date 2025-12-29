package com.asset.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源分配冲突检测请求DTO
 */
@Data
public class AllocationConflictRequest {

    /**
     * 资源ID
     */
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;
}
