package com.asset.management.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资源分配实体类（核心类）
 * 
 * @author 宋思泽
 */
@Data
public class Allocation {
    
    /**
     * 分配ID
     */
    private Long id;
    
    /**
     * 资源ID
     */
    private Long resourceId;
    
    /**
     * 项目ID
     */
    private Long projectId;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 分配状态
     */
    private AllocationStatus status;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 分配状态枚举
     */
    public enum AllocationStatus {
        /**
         * 生效中
         */
        ACTIVE,
        
        /**
         * 已完成
         */
        COMPLETED,
        
        /**
         * 已取消
         */
        CANCELLED
    }
}

