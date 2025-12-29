package com.asset.management.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 冲突检测响应
 */
@Data
@AllArgsConstructor
public class ConflictCheckResponse {

    private boolean hasConflict;
    private ConflictType type;
    private String message;
}
