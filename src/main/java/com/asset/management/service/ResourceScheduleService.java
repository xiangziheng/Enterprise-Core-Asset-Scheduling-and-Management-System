package com.asset.management.service;

import com.asset.management.dto.ResourceScheduleItem;
import com.asset.management.entity.Allocation;
import com.asset.management.entity.MaintenanceWindow;
import com.asset.management.mapper.AllocationMapper;
import com.asset.management.mapper.MaintenanceWindowMapper;
import com.asset.management.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 资源时间段视图服务
 */
@Service
@RequiredArgsConstructor
public class ResourceScheduleService {

    private final AllocationMapper allocationMapper;
    private final MaintenanceWindowMapper maintenanceWindowMapper;
    private final ResourceMapper resourceMapper;

    public List<ResourceScheduleItem> getSchedule(Long resourceId,
                                                  LocalDateTime startTime,
                                                  LocalDateTime endTime) {
        if (resourceId == null) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        validateTimeRange(startTime, endTime);
        if (resourceMapper.findById(resourceId) == null) {
            throw new IllegalArgumentException("资源不存在: id=" + resourceId);
        }

        List<ResourceScheduleItem> items = new ArrayList<>();

        List<Allocation> allocations =
                allocationMapper.findByResourceIdAndRange(resourceId, startTime, endTime);
        for (Allocation allocation : allocations) {
            items.add(new ResourceScheduleItem(
                    "ALLOCATION",
                    "HARD",
                    allocation.getStartTime(),
                    allocation.getEndTime(),
                    "项目ID: " + allocation.getProjectId(),
                    allocation.getId()
            ));
        }

        List<MaintenanceWindow> windows =
                maintenanceWindowMapper.findByResourceAndRange(resourceId, startTime, endTime);
        for (MaintenanceWindow window : windows) {
            String level = window.getType() == null ? "HARD" : window.getType().name();
            String label = window.getReason() == null || window.getReason().isBlank()
                    ? "维护窗口"
                    : window.getReason();
            items.add(new ResourceScheduleItem(
                    "MAINTENANCE",
                    level,
                    window.getStartTime(),
                    window.getEndTime(),
                    label,
                    window.getId()
            ));
        }

        items.sort(Comparator.comparing(ResourceScheduleItem::getStartTime));
        return items;
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("结束时间必须大于开始时间");
        }
    }
}
