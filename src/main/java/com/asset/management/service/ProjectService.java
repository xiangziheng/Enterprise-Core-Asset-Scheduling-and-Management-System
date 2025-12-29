package com.asset.management.service;

import com.asset.management.entity.Project;
import com.asset.management.mapper.ProjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 项目服务类
 * 
 * @author 宋思泽
 */
@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectMapper projectMapper;
    
    public List<Project> findAll() {
        return projectMapper.findAll();
    }
    
    public Project findById(Long id) {
        return projectMapper.findById(id);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Project createProject(Project project) {
        if (project.getStatus() == null) {
            project.setStatus(Project.ProjectStatus.ACTIVE);
        }
        projectMapper.insert(project);
        return project;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Project updateProject(Long id, Project project) {
        Project existing = projectMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("项目不存在: id=" + id);
        }
        
        project.setId(id);
        projectMapper.update(project);
        return project;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteProject(Long id) {
        projectMapper.deleteById(id);
    }
}

