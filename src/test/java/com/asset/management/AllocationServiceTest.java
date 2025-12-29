package com.asset.management;

import com.asset.management.dto.AllocationRequest;
import com.asset.management.entity.Allocation;
import com.asset.management.exception.ResourceConflictException;
import com.asset.management.service.AllocationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 资源分配冲突检测测试
 * 
 * @author 宋思泽
 */
@SpringBootTest
class AllocationServiceTest {
    
    @Autowired
    private AllocationService allocationService;
    
    /**
     * TC-02: 正常分配资源
     */
    @Test
    void testCreateAllocation_Success() {
        AllocationRequest request = new AllocationRequest();
        request.setResourceId(1L);
        request.setProjectId(1L);
        request.setStartTime(LocalDateTime.of(2025, 1, 1, 9, 0));
        request.setEndTime(LocalDateTime.of(2025, 1, 5, 18, 0));
        request.setRemark("测试正常分配");
        
        Allocation allocation = allocationService.createAllocation(request);
        
        assertNotNull(allocation);
        assertNotNull(allocation.getId());
        assertEquals(Allocation.AllocationStatus.ACTIVE, allocation.getStatus());
        
        System.out.println("✅ TC-02: 正常分配资源 - 通过");
    }
    
    /**
     * TC-03: 完全重叠冲突
     */
    @Test
    void testCreateAllocation_CompleteOverlap() {
        // 先创建一个分配
        AllocationRequest request1 = new AllocationRequest();
        request1.setResourceId(2L);
        request1.setProjectId(1L);
        request1.setStartTime(LocalDateTime.of(2025, 1, 10, 9, 0));
        request1.setEndTime(LocalDateTime.of(2025, 1, 15, 18, 0));
        allocationService.createAllocation(request1);
        
        // 尝试创建完全重叠的分配
        AllocationRequest request2 = new AllocationRequest();
        request2.setResourceId(2L);
        request2.setProjectId(2L);
        request2.setStartTime(LocalDateTime.of(2025, 1, 12, 10, 0));
        request2.setEndTime(LocalDateTime.of(2025, 1, 14, 16, 0));
        
        assertThrows(ResourceConflictException.class, () -> {
            allocationService.createAllocation(request2);
        });
        
        System.out.println("✅ TC-03: 完全重叠冲突 - 通过");
    }
    
    /**
     * TC-04: 部分重叠冲突
     */
    @Test
    void testCreateAllocation_PartialOverlap() {
        // 先创建一个分配
        AllocationRequest request1 = new AllocationRequest();
        request1.setResourceId(3L);
        request1.setProjectId(1L);
        request1.setStartTime(LocalDateTime.of(2025, 2, 1, 9, 0));
        request1.setEndTime(LocalDateTime.of(2025, 2, 5, 18, 0));
        allocationService.createAllocation(request1);
        
        // 尝试创建部分重叠的分配
        AllocationRequest request2 = new AllocationRequest();
        request2.setResourceId(3L);
        request2.setProjectId(2L);
        request2.setStartTime(LocalDateTime.of(2025, 2, 4, 10, 0));
        request2.setEndTime(LocalDateTime.of(2025, 2, 8, 16, 0));
        
        assertThrows(ResourceConflictException.class, () -> {
            allocationService.createAllocation(request2);
        });
        
        System.out.println("✅ TC-04: 部分重叠冲突 - 通过");
    }
    
    /**
     * TC-05: 边界条件测试（相邻时间段）
     */
    @Test
    void testCreateAllocation_Adjacent() {
        // 先创建一个分配
        AllocationRequest request1 = new AllocationRequest();
        request1.setResourceId(4L);
        request1.setProjectId(1L);
        request1.setStartTime(LocalDateTime.of(2025, 3, 1, 9, 0));
        request1.setEndTime(LocalDateTime.of(2025, 3, 5, 18, 0));
        allocationService.createAllocation(request1);
        
        // 创建紧邻的分配（开始时间 = 前一个结束时间）
        AllocationRequest request2 = new AllocationRequest();
        request2.setResourceId(4L);
        request2.setProjectId(2L);
        request2.setStartTime(LocalDateTime.of(2025, 3, 5, 18, 0));
        request2.setEndTime(LocalDateTime.of(2025, 3, 8, 18, 0));
        
        // 根据业务逻辑，边界相等不算冲突
        Allocation allocation = allocationService.createAllocation(request2);
        assertNotNull(allocation);
        
        System.out.println("✅ TC-05: 边界条件测试 - 通过");
    }
    
    /**
     * 测试冲突检测方法
     */
    @Test
    void testCheckConflict() {
        // 先创建一个分配
        AllocationRequest request = new AllocationRequest();
        request.setResourceId(5L);
        request.setProjectId(1L);
        request.setStartTime(LocalDateTime.of(2025, 4, 1, 9, 0));
        request.setEndTime(LocalDateTime.of(2025, 4, 5, 18, 0));
        allocationService.createAllocation(request);
        
        // 检测冲突
        var conflict = allocationService.checkConflict(
                5L,
                LocalDateTime.of(2025, 4, 3, 10, 0),
                LocalDateTime.of(2025, 4, 7, 18, 0)
        );
        
        assertTrue(conflict.isHasConflict(), "应该检测到冲突");
        
        // 检测无冲突的时间段
        var noConflict = allocationService.checkConflict(
                5L,
                LocalDateTime.of(2025, 4, 10, 10, 0),
                LocalDateTime.of(2025, 4, 15, 18, 0)
        );
        
        assertFalse(noConflict.isHasConflict(), "不应该检测到冲突");
        
        System.out.println("✅ 冲突检测方法测试 - 通过");
    }
}
