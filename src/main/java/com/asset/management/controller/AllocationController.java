package com.asset.management.controller;

import com.asset.management.dto.AllocationConflictRequest;
import com.asset.management.dto.AllocationRequest;
import com.asset.management.dto.ConflictCheckResponse;
import com.asset.management.dto.Result;
import com.asset.management.entity.Allocation;
import com.asset.management.service.AllocationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源分配控制器（核心控制器）
 * 
 * @author 宋思泽
 */
@RestController
@RequestMapping("/allocations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AllocationController {
    
    private final AllocationService allocationService;
    
    /**
     * 查询所有分配记录
     */
    @GetMapping
    public Result<List<Allocation>> findAll() {
        List<Allocation> allocations = allocationService.findAll();
        return Result.success(allocations);
    }
    
    /**
     * 根据ID查询分配记录
     */
    @GetMapping("/{id}")
    public Result<Allocation> findById(@PathVariable Long id) {
        Allocation allocation = allocationService.findById(id);
        if (allocation == null) {
            return Result.error("分配记录不存在");
        }
        return Result.success(allocation);
    }
    
    /**
     * 根据资源ID查询分配记录
     */
    @GetMapping("/resource/{resourceId}")
    public Result<List<Allocation>> findByResourceId(@PathVariable Long resourceId) {
        List<Allocation> allocations = allocationService.findByResourceId(resourceId);
        return Result.success(allocations);
    }
    
    /**
     * 根据项目ID查询分配记录
     */
    @GetMapping("/project/{projectId}")
    public Result<List<Allocation>> findByProjectId(@PathVariable Long projectId) {
        List<Allocation> allocations = allocationService.findByProjectId(projectId);
        return Result.success(allocations);
    }
    
    /**
     * 创建资源分配（核心接口）
     * 在分配前会自动进行冲突检测
     */
    @PostMapping
    public Result<Allocation> create(@Valid @RequestBody AllocationRequest request) {
        Allocation allocation = allocationService.createAllocation(request);
        return Result.success(allocation);
    }
    
    /**
     * 更新资源分配
     */
    @PutMapping("/{id}")
    public Result<Allocation> update(@PathVariable Long id, 
                                     @Valid @RequestBody AllocationRequest request) {
        Allocation allocation = allocationService.updateAllocation(id, request);
        return Result.success(allocation);
    }
    
    /**
     * 取消资源分配
     */
    @PutMapping("/{id}/cancel")
    public Result<Void> cancel(@PathVariable Long id) {
        allocationService.cancelAllocation(id);
        return Result.success();
    }
    
    /**
     * 删除资源分配
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        allocationService.deleteAllocation(id);
        return Result.success();
    }
    
    /**
     * 检测资源冲突（独立接口，用于前端预检测）
     */
    @PostMapping("/check-conflict")
    public Result<ConflictCheckResponse> checkConflict(@Valid @RequestBody AllocationConflictRequest request) {
        ConflictCheckResponse response = allocationService.checkConflict(
                request.getResourceId(),
                request.getStartTime(),
                request.getEndTime()
        );

        return Result.success(response);
    }
}
