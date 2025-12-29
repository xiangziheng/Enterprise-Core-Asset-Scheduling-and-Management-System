package com.asset.management.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 资源类别实体类
 * 
 * @author 宋思泽
 */
@Data
public class Category {
    
    /**
     * 类别ID
     */
    private Long id;
    
    /**
     * 类别名称
     */
    private String name;
    
    /**
     * 类别描述
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
}

