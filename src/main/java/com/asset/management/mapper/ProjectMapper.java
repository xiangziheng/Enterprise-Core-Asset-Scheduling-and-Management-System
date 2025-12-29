package com.asset.management.mapper;

import com.asset.management.entity.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目Mapper接口
 * 
 * @author 宋思泽
 */
@Mapper
public interface ProjectMapper {
    
    /**
     * 查询所有项目
     */
    List<Project> findAll();
    
    /**
     * 根据ID查询项目
     */
    Project findById(@Param("id") Long id);
    
    /**
     * 插入项目
     */
    int insert(Project project);
    
    /**
     * 更新项目
     */
    int update(Project project);
    
    /**
     * 删除项目
     */
    int deleteById(@Param("id") Long id);
}

