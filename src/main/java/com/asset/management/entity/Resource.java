package com.asset.management.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资源实体类
 * 
 * @author 宋思泽
 */
@Data
public class Resource {
    
    /**
     * 资源ID
     */
    private Long id;
    
    /**
     * 资源名称
     */
    private String name;
    
    /**
     * 类别ID
     */
    private Long categoryId;
    
    /**
     * 资源状态
     */
    private ResourceStatus status;
    
    /**
     * 资源描述
     */
    private String description;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 资源状态枚举
     */
    public enum ResourceStatus {
        /**
         * 可用
         */
        AVAILABLE,
        
        /**
         * 维护中
         */
        MAINTENANCE,
        
        /**
         * 已退役
         */
        RETIRED
    }
}

