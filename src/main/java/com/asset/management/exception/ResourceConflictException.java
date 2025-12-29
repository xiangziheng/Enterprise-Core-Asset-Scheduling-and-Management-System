package com.asset.management.exception;

/**
 * 资源冲突异常
 * 当资源分配时间段冲突时抛出此异常
 * 
 * @author 宋思泽
 */
public class ResourceConflictException extends RuntimeException {
    
    public ResourceConflictException(String message) {
        super(message);
    }
    
    public ResourceConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

