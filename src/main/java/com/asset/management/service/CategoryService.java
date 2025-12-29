package com.asset.management.service;

import com.asset.management.entity.Category;
import com.asset.management.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 资源类别服务类
 * 
 * @author 宋思泽
 */
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    
    public List<Category> findAll() {
        return categoryMapper.findAll();
    }
    
    public Category findById(Long id) {
        return categoryMapper.findById(id);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Category createCategory(Category category) {
        categoryMapper.insert(category);
        return category;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Category updateCategory(Long id, Category category) {
        Category existing = categoryMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("类别不存在: id=" + id);
        }
        
        category.setId(id);
        categoryMapper.update(category);
        return category;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteCategory(Long id) {
        categoryMapper.deleteById(id);
    }
}

