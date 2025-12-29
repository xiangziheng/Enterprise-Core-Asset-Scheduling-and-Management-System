package com.asset.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资源时间段视图条目
 */
@Data
@AllArgsConstructor
public class ResourceScheduleItem {

    private String type;
    private String level;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String label;
    private Long referenceId;
}
