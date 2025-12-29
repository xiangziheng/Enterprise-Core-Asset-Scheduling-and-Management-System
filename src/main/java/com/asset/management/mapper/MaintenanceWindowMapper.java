package com.asset.management.mapper;

import com.asset.management.entity.MaintenanceWindow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 维护窗口Mapper接口
 */
@Mapper
public interface MaintenanceWindowMapper {

    MaintenanceWindow findById(@Param("id") Long id);

    List<MaintenanceWindow> findByResourceId(@Param("resourceId") Long resourceId);

    List<MaintenanceWindow> findAll();

    List<MaintenanceWindow> findByResourceAndRange(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<MaintenanceWindow> findAllByRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    List<MaintenanceWindow> findConflicts(
            @Param("resourceId") Long resourceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    int insert(MaintenanceWindow window);

    int update(MaintenanceWindow window);

    int deleteById(@Param("id") Long id);
}
