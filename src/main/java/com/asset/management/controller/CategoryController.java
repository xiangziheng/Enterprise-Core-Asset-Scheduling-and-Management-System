package com.asset.management.controller;

import com.asset.management.dto.Result;
import com.asset.management.entity.Category;
import com.asset.management.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 资源类别控制器
 * 
 * @author 宋思泽
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {
    
    private final CategoryService categoryService;
    
    /**
     * 查询所有类别
     */
    @GetMapping
    public Result<List<Category>> findAll() {
        List<Category> categories = categoryService.findAll();
        return Result.success(categories);
    }
    
    /**
     * 根据ID查询类别
     */
    @GetMapping("/{id}")
    public Result<Category> findById(@PathVariable Long id) {
        Category category = categoryService.findById(id);
        if (category == null) {
            return Result.error("类别不存在");
        }
        return Result.success(category);
    }
    
    /**
     * 创建类别
     */
    @PostMapping
    public Result<Category> create(@RequestBody Category category) {
        Category created = categoryService.createCategory(category);
        return Result.success(created);
    }
    
    /**
     * 更新类别
     */
    @PutMapping("/{id}")
    public Result<Category> update(@PathVariable Long id, @RequestBody Category category) {
        Category updated = categoryService.updateCategory(id, category);
        return Result.success(updated);
    }
    
    /**
     * 删除类别
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success();
    }
}

