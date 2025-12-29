package com.asset.management.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源维护窗口实体
 */
@Data
public class MaintenanceWindow {

    /**
     * 维护窗口ID
     */
    private Long id;

    /**
     * 资源ID
     */
    private Long resourceId;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 维护窗口类型
     */
    private MaintenanceType type;

    /**
     * 原因说明
     */
    private String reason;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;

    /**
     * 维护窗口类型枚举
     */
    public enum MaintenanceType {
        HARD,
        SOFT
    }
}
