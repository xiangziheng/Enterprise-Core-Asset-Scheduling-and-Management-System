package com.asset.management.controller;

import com.asset.management.dto.Result;
import com.asset.management.entity.Project;
import com.asset.management.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目控制器
 * 
 * @author 宋思泽
 */
@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {
    
    private final ProjectService projectService;
    
    /**
     * 查询所有项目
     */
    @GetMapping
    public Result<List<Project>> findAll() {
        List<Project> projects = projectService.findAll();
        return Result.success(projects);
    }
    
    /**
     * 根据ID查询项目
     */
    @GetMapping("/{id}")
    public Result<Project> findById(@PathVariable Long id) {
        Project project = projectService.findById(id);
        if (project == null) {
            return Result.error("项目不存在");
        }
        return Result.success(project);
    }
    
    /**
     * 创建项目
     */
    @PostMapping
    public Result<Project> create(@RequestBody Project project) {
        Project created = projectService.createProject(project);
        return Result.success(created);
    }
    
    /**
     * 更新项目
     */
    @PutMapping("/{id}")
    public Result<Project> update(@PathVariable Long id, @RequestBody Project project) {
        Project updated = projectService.updateProject(id, project);
        return Result.success(updated);
    }
    
    /**
     * 删除项目
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return Result.success();
    }
}

