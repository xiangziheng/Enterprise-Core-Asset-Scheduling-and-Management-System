package com.asset.management.controller;

import com.asset.management.dto.ResourceScheduleItem;
import com.asset.management.dto.Result;
import com.asset.management.entity.Resource;
import com.asset.management.service.ResourceService;
import com.asset.management.service.ResourceScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.time.LocalDateTime;

/**
 * 资源控制器
 * 
 * @author 宋思泽
 */
@RestController
@RequestMapping("/resources")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResourceController {
    
    private final ResourceService resourceService;
    private final ResourceScheduleService resourceScheduleService;
    
    /**
     * 查询所有资源
     */
    @GetMapping
    public Result<List<Resource>> findAll() {
        List<Resource> resources = resourceService.findAll();
        return Result.success(resources);
    }
    
    /**
     * 根据ID查询资源
     */
    @GetMapping("/{id}")
    public Result<Resource> findById(@PathVariable Long id) {
        Resource resource = resourceService.findById(id);
        if (resource == null) {
            return Result.error("资源不存在");
        }
        return Result.success(resource);
    }

    /**
     * 资源时间段视图
     */
    @GetMapping("/{id}/schedule")
    public Result<List<ResourceScheduleItem>> schedule(@PathVariable Long id,
                                                       @RequestParam
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       LocalDateTime startTime,
                                                       @RequestParam
                                                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                       LocalDateTime endTime) {
        return Result.success(resourceScheduleService.getSchedule(id, startTime, endTime));
    }
    
    /**
     * 根据类别ID查询资源
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<Resource>> findByCategoryId(@PathVariable Long categoryId) {
        List<Resource> resources = resourceService.findByCategoryId(categoryId);
        return Result.success(resources);
    }
    
    /**
     * 创建资源
     */
    @PostMapping
    public Result<Resource> create(@RequestBody Resource resource) {
        Resource created = resourceService.createResource(resource);
        return Result.success(created);
    }
    
    /**
     * 更新资源
     */
    @PutMapping("/{id}")
    public Result<Resource> update(@PathVariable Long id, @RequestBody Resource resource) {
        Resource updated = resourceService.updateResource(id, resource);
        return Result.success(updated);
    }
    
    /**
     * 删除资源
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return Result.success();
    }
}
