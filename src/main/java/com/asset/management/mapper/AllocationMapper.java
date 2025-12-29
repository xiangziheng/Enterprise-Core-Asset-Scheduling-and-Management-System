package com.asset.management.mapper;

import com.asset.management.entity.Allocation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 资源分配Mapper接口
 * 
 * @author 宋思泽
 */
@Mapper
public interface AllocationMapper {
    
    /**
     * 查询所有分配记录
     */
    List<Allocation> findAll();
    
    /**
     * 根据ID查询分配记录
     */
    Allocation findById(@Param("id") Long id);
    
    /**
     * 根据资源ID查询分配记录
     */
    List<Allocation> findByResourceId(@Param("resourceId") Long resourceId);

    /**
     * 根据资源ID和时间范围查询分配记录
     */
    List<Allocation> findByResourceIdAndRange(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 根据项目ID查询分配记录
     */
    List<Allocation> findByProjectId(@Param("projectId") Long projectId);
    
    /**
     * 查询时间段内有冲突的分配记录（核心方法）
     * 用于检测资源在指定时间段是否已被分配
     * 
     * @param resourceId 资源ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 冲突的分配记录列表
     */
    List<Allocation> findConflictAllocations(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
    
    /**
     * 查询时间段内有冲突的分配记录（排除指定ID）
     * 用于更新分配时的冲突检测
     */
    List<Allocation> findConflictAllocationsExcludeId(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime,
            @Param("excludeId") Long excludeId
    );
    
    /**
     * 插入分配记录
     */
    int insert(Allocation allocation);
    
    /**
     * 更新分配记录
     */
    int update(Allocation allocation);
    
    /**
     * 删除分配记录
     */
    int deleteById(@Param("id") Long id);
}
