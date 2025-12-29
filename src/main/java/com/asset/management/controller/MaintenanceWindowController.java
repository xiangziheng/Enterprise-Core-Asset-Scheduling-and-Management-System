package com.asset.management.controller;

import com.asset.management.dto.MaintenanceWindowRequest;
import com.asset.management.dto.Result;
import com.asset.management.entity.MaintenanceWindow;
import com.asset.management.service.MaintenanceWindowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 维护窗口控制器
 */
@RestController
@RequestMapping("/maintenance-windows")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class MaintenanceWindowController {

    private final MaintenanceWindowService maintenanceWindowService;

    @GetMapping
    public Result<List<MaintenanceWindow>> list(@RequestParam(required = false) Long resourceId,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                LocalDateTime startTime,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                                                LocalDateTime endTime) {
        if (startTime != null && endTime != null) {
            if (resourceId != null) {
                return Result.success(
                        maintenanceWindowService.findByResourceAndRange(resourceId, startTime, endTime)
                );
            }
            return Result.success(maintenanceWindowService.findAllByRange(startTime, endTime));
        }
        if (startTime != null || endTime != null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        if (resourceId != null) {
            return Result.success(maintenanceWindowService.findByResourceId(resourceId));
        }
        return Result.success(maintenanceWindowService.findAll());
    }

    @PostMapping
    public Result<MaintenanceWindow> create(@Valid @RequestBody MaintenanceWindowRequest request) {
        return Result.success(maintenanceWindowService.create(request));
    }

    @PutMapping("/{id}")
    public Result<MaintenanceWindow> update(@PathVariable Long id,
                                            @Valid @RequestBody MaintenanceWindowRequest request) {
        return Result.success(maintenanceWindowService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        maintenanceWindowService.delete(id);
        return Result.success();
    }
}
