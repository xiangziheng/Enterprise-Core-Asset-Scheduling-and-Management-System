-- 企业核心资产调度管理系统数据库初始化脚本

-- 创建数据库
CREATE DATABASE IF NOT EXISTS asset_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE asset_management;

-- 关闭外键检查，便于重建表结构
SET FOREIGN_KEY_CHECKS = 0;

-- 0. 用户表
DROP TABLE IF EXISTS t_user;
CREATE TABLE t_user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    role VARCHAR(20) NOT NULL DEFAULT 'USER' COMMENT '角色：ADMIN/USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DISABLED',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 1. 资源类别表
DROP TABLE IF EXISTS t_category;
CREATE TABLE t_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '类别ID',
    name VARCHAR(100) NOT NULL UNIQUE COMMENT '类别名称',
    description VARCHAR(500) COMMENT '类别描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源类别表';

-- 2. 资源表
DROP TABLE IF EXISTS t_resource;
CREATE TABLE t_resource (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    name VARCHAR(200) NOT NULL COMMENT '资源名称',
    category_id BIGINT NOT NULL COMMENT '类别ID',
    status VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE-可用, MAINTENANCE-维护中, RETIRED-已退役',
    description VARCHAR(500) COMMENT '资源描述',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES t_category(id) ON DELETE RESTRICT,
    INDEX idx_category (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源表';

-- 3. 项目表
DROP TABLE IF EXISTS t_project;
CREATE TABLE t_project (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '项目ID',
    name VARCHAR(200) NOT NULL COMMENT '项目名称',
    manager VARCHAR(100) COMMENT '项目负责人',
    description VARCHAR(1000) COMMENT '项目描述',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-进行中, COMPLETED-已完成, CANCELLED-已取消',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 4. 资源分配表（核心表）
DROP TABLE IF EXISTS t_allocation;
CREATE TABLE t_allocation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分配ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    project_id BIGINT NOT NULL COMMENT '项目ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE-生效中, COMPLETED-已完成, CANCELLED-已取消',
    remark VARCHAR(500) COMMENT '备注',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resource_id) REFERENCES t_resource(id) ON DELETE CASCADE,
    FOREIGN KEY (project_id) REFERENCES t_project(id) ON DELETE CASCADE,
    INDEX idx_resource_time (resource_id, start_time, end_time),
    INDEX idx_project (project_id),
    INDEX idx_status (status),
    CONSTRAINT chk_time CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源分配表';

-- 5. 资源维护窗口/黑名单表
DROP TABLE IF EXISTS t_maintenance_window;
CREATE TABLE t_maintenance_window (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '维护窗口ID',
    resource_id BIGINT NOT NULL COMMENT '资源ID',
    start_time DATETIME NOT NULL COMMENT '开始时间',
    end_time DATETIME NOT NULL COMMENT '结束时间',
    type VARCHAR(20) NOT NULL DEFAULT 'HARD' COMMENT '类型：HARD-硬冲突, SOFT-软冲突',
    reason VARCHAR(500) COMMENT '原因说明',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (resource_id) REFERENCES t_resource(id) ON DELETE CASCADE,
    INDEX idx_resource_time (resource_id, start_time, end_time),
    INDEX idx_type (type),
    CONSTRAINT chk_maintenance_time CHECK (end_time > start_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源维护窗口表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 插入初始测试数据
INSERT INTO t_category (name, description) VALUES
('计算资源', 'GPU服务器、高性能计算集群等'),
('场地资源', '会议室、实验室等'),
('设备资源', '测试设备、仪器设备等');

INSERT INTO t_resource (name, category_id, description, status) VALUES
('GPU服务器01', 1, 'NVIDIA A100 GPU服务器', 'AVAILABLE'),
('GPU服务器02', 1, 'NVIDIA V100 GPU服务器', 'AVAILABLE'),
('大会议室', 2, '可容纳50人的会议室', 'AVAILABLE'),
('小会议室', 2, '可容纳10人的会议室', 'AVAILABLE'),
('测试设备A', 3, '高精度测试仪器', 'AVAILABLE');

INSERT INTO t_project (name, manager, description, status) VALUES
('AI训练项目', '张三', '深度学习模型训练', 'ACTIVE'),
('渲染项目', '李四', '3D渲染任务', 'ACTIVE'),
('测试项目', '王五', '产品质量测试', 'ACTIVE');

INSERT INTO t_user (username, password, role, status) VALUES
('admin', 'admin123', 'ADMIN', 'ACTIVE'),
('user', 'user123', 'USER', 'ACTIVE');
