package com.asset.management.service;

import com.asset.management.dto.MaintenanceWindowRequest;
import com.asset.management.entity.MaintenanceWindow;
import com.asset.management.mapper.MaintenanceWindowMapper;
import com.asset.management.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 维护窗口服务类
 */
@Service
@RequiredArgsConstructor
public class MaintenanceWindowService {

    private final MaintenanceWindowMapper maintenanceWindowMapper;
    private final ResourceMapper resourceMapper;

    public MaintenanceWindow findById(Long id) {
        return maintenanceWindowMapper.findById(id);
    }

    public List<MaintenanceWindow> findByResourceId(Long resourceId) {
        return maintenanceWindowMapper.findByResourceId(resourceId);
    }

    public List<MaintenanceWindow> findByResourceAndRange(Long resourceId,
                                                          LocalDateTime startTime,
                                                          LocalDateTime endTime) {
        validateTimeRange(startTime, endTime);
        return maintenanceWindowMapper.findByResourceAndRange(resourceId, startTime, endTime);
    }

    @Transactional(rollbackFor = Exception.class)
    public MaintenanceWindow create(MaintenanceWindowRequest request) {
        validateRequest(request);
        assertResourceExists(request.getResourceId());

        MaintenanceWindow window = new MaintenanceWindow();
        window.setResourceId(request.getResourceId());
        window.setStartTime(request.getStartTime());
        window.setEndTime(request.getEndTime());
        window.setType(request.getType() == null
                ? MaintenanceWindow.MaintenanceType.HARD
                : request.getType());
        window.setReason(request.getReason());

        maintenanceWindowMapper.insert(window);
        return window;
    }

    @Transactional(rollbackFor = Exception.class)
    public MaintenanceWindow update(Long id, MaintenanceWindowRequest request) {
        validateRequest(request);
        MaintenanceWindow existing = maintenanceWindowMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("维护窗口不存在: id=" + id);
        }
        assertResourceExists(request.getResourceId());

        existing.setResourceId(request.getResourceId());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setType(request.getType() == null
                ? MaintenanceWindow.MaintenanceType.HARD
                : request.getType());
        existing.setReason(request.getReason());

        maintenanceWindowMapper.update(existing);
        return existing;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        maintenanceWindowMapper.deleteById(id);
    }

    private void validateRequest(MaintenanceWindowRequest request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());
    }

    private void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("结束时间必须大于开始时间");
        }
    }

    private void assertResourceExists(Long resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        if (resourceMapper.findById(resourceId) == null) {
            throw new IllegalArgumentException("资源不存在: id=" + resourceId);
        }
    }
}
