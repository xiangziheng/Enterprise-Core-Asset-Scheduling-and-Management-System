package com.asset.management.service;

import com.asset.management.dto.AllocationRequest;
import com.asset.management.entity.Allocation;
import com.asset.management.dto.ConflictCheckResponse;
import com.asset.management.dto.ConflictType;
import com.asset.management.entity.MaintenanceWindow;
import com.asset.management.exception.ResourceConflictException;
import com.asset.management.mapper.AllocationMapper;
import com.asset.management.mapper.MaintenanceWindowMapper;
import com.asset.management.mapper.ProjectMapper;
import com.asset.management.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源分配服务类
 * 实现核心冲突检测算法
 * 
 * @author 宋思泽
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AllocationService {
    
    private final AllocationMapper allocationMapper;
    private final ResourceMapper resourceMapper;
    private final ProjectMapper projectMapper;
    private final MaintenanceWindowMapper maintenanceWindowMapper;
    
    /**
     * 查询所有分配记录
     */
    public List<Allocation> findAll() {
        return allocationMapper.findAll();
    }
    
    /**
     * 根据ID查询分配记录
     */
    public Allocation findById(Long id) {
        return allocationMapper.findById(id);
    }
    
    /**
     * 根据资源ID查询分配记录
     */
    public List<Allocation> findByResourceId(Long resourceId) {
        return allocationMapper.findByResourceId(resourceId);
    }
    
    /**
     * 根据项目ID查询分配记录
     */
    public List<Allocation> findByProjectId(Long projectId) {
        return allocationMapper.findByProjectId(projectId);
    }
    
    /**
     * 创建资源分配（核心方法）
     * 在分配前进行冲突检测
     * 
     * @param request 分配请求
     * @return 分配记录
     * @throws ResourceConflictException 当资源时间冲突时抛出
     */
    @Transactional(rollbackFor = Exception.class)
    public Allocation createAllocation(AllocationRequest request) {
        // 1. 参数校验
        validateAllocationTime(request.getStartTime(), request.getEndTime());
        assertResourceExistsWithLock(request.getResourceId());
        assertProjectExists(request.getProjectId());
        
        // 2. 核心冲突检测
        ConflictType conflictType = detectConflictType(
                request.getResourceId(),
                request.getStartTime(),
                request.getEndTime(),
                null
        );
        
        if (conflictType == ConflictType.HARD) {
            log.warn("资源分配冲突: resourceId={}, startTime={}, endTime={}", 
                    request.getResourceId(), request.getStartTime(), request.getEndTime());
            throw new ResourceConflictException(
                    String.format("资源在时间段 [%s ~ %s] 内已被占用，分配失败！", 
                            request.getStartTime(), request.getEndTime())
            );
        }
        if (conflictType == ConflictType.SOFT) {
            log.warn("资源分配软冲突: resourceId={}, startTime={}, endTime={}",
                    request.getResourceId(), request.getStartTime(), request.getEndTime());
        }
        
        // 3. 创建分配记录
        Allocation allocation = new Allocation();
        allocation.setResourceId(request.getResourceId());
        allocation.setProjectId(request.getProjectId());
        allocation.setStartTime(request.getStartTime());
        allocation.setEndTime(request.getEndTime());
        allocation.setRemark(request.getRemark());
        allocation.setStatus(Allocation.AllocationStatus.ACTIVE);
        
        allocationMapper.insert(allocation);
        
        log.info("资源分配成功: id={}, resourceId={}, projectId={}", 
                allocation.getId(), allocation.getResourceId(), allocation.getProjectId());
        
        return allocation;
    }
    
    /**
     * 更新资源分配
     */
    @Transactional(rollbackFor = Exception.class)
    public Allocation updateAllocation(Long id, AllocationRequest request) {
        // 1. 检查记录是否存在
        Allocation existing = allocationMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("分配记录不存在: id=" + id);
        }
        
        // 2. 参数校验
        validateAllocationTime(request.getStartTime(), request.getEndTime());
        assertResourceExistsWithLock(request.getResourceId());
        assertProjectExists(request.getProjectId());
        
        // 3. 冲突检测（排除当前记录）
        ConflictType conflictType = detectConflictType(
                request.getResourceId(),
                request.getStartTime(),
                request.getEndTime(),
                id
        );
        
        if (conflictType == ConflictType.HARD) {
            throw new ResourceConflictException(
                    String.format("资源在时间段 [%s ~ %s] 内已被占用，更新失败！", 
                            request.getStartTime(), request.getEndTime())
            );
        }
        if (conflictType == ConflictType.SOFT) {
            log.warn("资源分配软冲突: resourceId={}, startTime={}, endTime={}",
                    request.getResourceId(), request.getStartTime(), request.getEndTime());
        }
        
        // 4. 更新记录
        existing.setResourceId(request.getResourceId());
        existing.setProjectId(request.getProjectId());
        existing.setStartTime(request.getStartTime());
        existing.setEndTime(request.getEndTime());
        existing.setRemark(request.getRemark());
        
        allocationMapper.update(existing);
        
        log.info("资源分配更新成功: id={}", id);
        
        return existing;
    }
    
    /**
     * 取消资源分配
     */
    @Transactional(rollbackFor = Exception.class)
    public void cancelAllocation(Long id) {
        Allocation allocation = allocationMapper.findById(id);
        if (allocation == null) {
            throw new IllegalArgumentException("分配记录不存在: id=" + id);
        }
        
        allocation.setStatus(Allocation.AllocationStatus.CANCELLED);
        allocationMapper.update(allocation);
        
        log.info("资源分配已取消: id={}", id);
    }
    
    /**
     * 删除资源分配
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteAllocation(Long id) {
        allocationMapper.deleteById(id);
        log.info("资源分配已删除: id={}", id);
    }
    
    /**
     * 核心冲突检测算法
     * 
     * 算法原理：
     * 两个时间段存在冲突，当且仅当它们有重叠部分
     * 时间段A [startA, endA] 与 时间段B [startB, endB] 冲突的条件是：
     * startA < endB AND startB < endA
     * 
     * 几种冲突情况：
     * 1. 完全重叠：B完全包含在A内
     * 2. 部分重叠：B的开始或结束时间在A的范围内
     * 3. 完全覆盖：B完全覆盖A
     * 
     * @param resourceId 资源ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true-有冲突，false-无冲突
     */
    public ConflictCheckResponse checkConflict(Long resourceId, LocalDateTime startTime, LocalDateTime endTime) {
        ConflictType conflictType = detectConflictType(resourceId, startTime, endTime, null);
        boolean hasConflict = conflictType != ConflictType.NONE;
        return new ConflictCheckResponse(hasConflict, conflictType, buildConflictMessage(conflictType));
    }
    
    /**
     * 校验分配时间
     */
    private void validateAllocationTime(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("结束时间必须大于开始时间");
        }
    }

    private ConflictType detectConflictType(Long resourceId,
                                            LocalDateTime startTime,
                                            LocalDateTime endTime,
                                            Long excludeAllocationId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        validateAllocationTime(startTime, endTime);

        List<Allocation> allocationConflicts = excludeAllocationId == null
                ? allocationMapper.findConflictAllocations(resourceId, startTime, endTime)
                : allocationMapper.findConflictAllocationsExcludeId(
                        resourceId, startTime, endTime, excludeAllocationId
                );

        List<MaintenanceWindow> maintenanceConflicts =
                maintenanceWindowMapper.findConflicts(resourceId, startTime, endTime);

        boolean maintenanceHard = maintenanceConflicts.stream()
                .anyMatch(window -> window.getType() == MaintenanceWindow.MaintenanceType.HARD);
        boolean maintenanceSoft = maintenanceConflicts.stream()
                .anyMatch(window -> window.getType() == MaintenanceWindow.MaintenanceType.SOFT);

        if (!allocationConflicts.isEmpty() || maintenanceHard) {
            log.debug("检测到硬冲突: resourceId={}, 分配冲突数={}, 维护冲突数={}",
                    resourceId, allocationConflicts.size(), maintenanceConflicts.size());
            return ConflictType.HARD;
        }
        if (maintenanceSoft) {
            log.debug("检测到软冲突: resourceId={}, 维护冲突数={}", resourceId, maintenanceConflicts.size());
            return ConflictType.SOFT;
        }
        return ConflictType.NONE;
    }

    private String buildConflictMessage(ConflictType conflictType) {
        if (conflictType == ConflictType.HARD) {
            return "资源在该时间段内已被占用或处于维护窗口，无法分配";
        }
        if (conflictType == ConflictType.SOFT) {
            return "该时间段存在软冲突（维护建议），可继续提交";
        }
        return "资源可用";
    }

    private void assertResourceExistsWithLock(Long resourceId) {
        if (resourceId == null) {
            throw new IllegalArgumentException("资源ID不能为空");
        }
        if (resourceMapper.lockById(resourceId) == null) {
            throw new IllegalArgumentException("资源不存在: id=" + resourceId);
        }
    }

    private void assertProjectExists(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("项目ID不能为空");
        }
        if (projectMapper.findById(projectId) == null) {
            throw new IllegalArgumentException("项目不存在: id=" + projectId);
        }
    }
}
