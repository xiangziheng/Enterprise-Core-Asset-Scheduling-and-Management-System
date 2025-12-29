package com.asset.management.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 项目实体类
 * 
 * @author 宋思泽
 */
@Data
public class Project {
    
    /**
     * 项目ID
     */
    private Long id;
    
    /**
     * 项目名称
     */
    private String name;
    
    /**
     * 项目负责人
     */
    private String manager;
    
    /**
     * 项目描述
     */
    private String description;
    
    /**
     * 项目状态
     */
    private ProjectStatus status;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
    
    /**
     * 项目状态枚举
     */
    public enum ProjectStatus {
        /**
         * 进行中
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

