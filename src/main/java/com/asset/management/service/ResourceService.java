package com.asset.management.service;

import com.asset.management.entity.Resource;
import com.asset.management.mapper.ResourceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 资源服务类
 * 
 * @author 宋思泽
 */
@Service
@RequiredArgsConstructor
public class ResourceService {
    
    private final ResourceMapper resourceMapper;
    
    public List<Resource> findAll() {
        return resourceMapper.findAll();
    }
    
    public Resource findById(Long id) {
        return resourceMapper.findById(id);
    }
    
    public List<Resource> findByCategoryId(Long categoryId) {
        return resourceMapper.findByCategoryId(categoryId);
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Resource createResource(Resource resource) {
        if (resource.getStatus() == null) {
            resource.setStatus(Resource.ResourceStatus.AVAILABLE);
        }
        resourceMapper.insert(resource);
        return resource;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public Resource updateResource(Long id, Resource resource) {
        Resource existing = resourceMapper.findById(id);
        if (existing == null) {
            throw new IllegalArgumentException("资源不存在: id=" + id);
        }
        
        resource.setId(id);
        resourceMapper.update(resource);
        return resource;
    }
    
    @Transactional(rollbackFor = Exception.class)
    public void deleteResource(Long id) {
        resourceMapper.deleteById(id);
    }
}

