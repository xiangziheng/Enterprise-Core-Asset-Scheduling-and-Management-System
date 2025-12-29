package com.asset.management.mapper;

import com.asset.management.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源类别Mapper接口
 * 
 * @author 宋思泽
 */
@Mapper
public interface CategoryMapper {
    
    /**
     * 查询所有类别
     */
    List<Category> findAll();
    
    /**
     * 根据ID查询类别
     */
    Category findById(@Param("id") Long id);
    
    /**
     * 插入类别
     */
    int insert(Category category);
    
    /**
     * 更新类别
     */
    int update(Category category);
    
    /**
     * 删除类别
     */
    int deleteById(@Param("id") Long id);
}

