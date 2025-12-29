package com.asset.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 资源分配请求DTO
 * 
 * @author 宋思泽
 */
@Data
public class AllocationRequest {
    
    /**
     * 资源ID
     */
    @NotNull(message = "资源ID不能为空")
    private Long resourceId;
    
    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空")
    private Long projectId;
    
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
    
    /**
     * 备注
     */
    private String remark;
}

