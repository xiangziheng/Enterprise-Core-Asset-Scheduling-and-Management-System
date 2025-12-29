package com.asset.management.dto;

import com.asset.management.entity.MaintenanceWindow;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 维护窗口请求DTO
 */
@Data
public class MaintenanceWindowRequest {

    @NotNull(message = "资源ID不能为空")
    private Long resourceId;

    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    private MaintenanceWindow.MaintenanceType type;

    private String reason;
}
