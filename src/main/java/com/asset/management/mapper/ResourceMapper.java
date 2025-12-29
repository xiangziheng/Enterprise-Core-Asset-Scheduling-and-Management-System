package com.asset.management.mapper;

import com.asset.management.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源Mapper接口
 * 
 * @author 宋思泽
 */
@Mapper
public interface ResourceMapper {
    
    /**
     * 查询所有资源
     */
    List<Resource> findAll();
    
    /**
     * 根据ID查询资源
     */
    Resource findById(@Param("id") Long id);

    /**
     * 根据ID锁定资源记录（用于分配并发控制）
     */
    Resource lockById(@Param("id") Long id);
    
    /**
     * 根据类别ID查询资源
     */
    List<Resource> findByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 插入资源
     */
    int insert(Resource resource);
    
    /**
     * 更新资源
     */
    int update(Resource resource);
    
    /**
     * 删除资源
     */
    int deleteById(@Param("id") Long id);
}
