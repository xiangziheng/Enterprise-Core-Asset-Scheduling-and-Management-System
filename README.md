# 实习的总结、收获、体会、意见、建议等：

## 项目简介
面向企业内部核心资产（服务器、会议室、设备等）的分配与调度管理系统，核心目标是避免资源在同一时间段重复分配，并提供资源、项目、类别、维护窗口和用户的统一管理。

## 功能概览（增删改查）
- 资源管理：资源信息维护、按类别查询
- 类别管理：资源类别维护
- 项目管理：项目信息维护
- 资源分配：分配记录的增删改查、取消分配、冲突检测、按资源/项目查询
- 维护窗口：维护窗口增删改查、按资源/时间范围查询
- 用户与认证：登录/退出/当前用户、用户列表与创建
- 资源日程视图：指定时间段内的资源占用视图

## 技术栈
- 后端：Spring Boot 3.2.0、MyBatis 3.0.3、Java 17、MySQL 8.0
- 前端：Vue 3（CDN）、Element Plus、Axios
- 构建：Maven

## 认证与权限
- 登录获取 Token：`POST /auth/login`
- 请求携带 Token：`Authorization: Bearer <token>` 或 `X-Auth-Token: <token>`
- ADMIN：全量接口
- USER：允许 `GET` 全部接口；允许 `POST /allocations` 和 `POST /allocations/check-conflict`；禁止 `/users`

## 接口速览（前缀 /api）

### 资源分配
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /allocations | 查询全部分配 |
| GET | /allocations/{id} | 按ID查询 |
| GET | /allocations/resource/{resourceId} | 按资源查询 |
| GET | /allocations/project/{projectId} | 按项目查询 |
| POST | /allocations | 创建分配（含冲突检测） |
| PUT | /allocations/{id} | 更新分配 |
| PUT | /allocations/{id}/cancel | 取消分配 |
| DELETE | /allocations/{id} | 删除分配 |
| POST | /allocations/check-conflict | 冲突检测 |

### 资源
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /resources | 查询全部资源 |
| GET | /resources/{id} | 按ID查询 |
| GET | /resources/category/{categoryId} | 按类别查询 |
| GET | /resources/{id}/schedule | 资源时间段视图（startTime, endTime） |
| POST | /resources | 创建资源 |
| PUT | /resources/{id} | 更新资源 |
| DELETE | /resources/{id} | 删除资源 |

### 项目
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /projects | 查询全部项目 |
| GET | /projects/{id} | 按ID查询 |
| POST | /projects | 创建项目 |
| PUT | /projects/{id} | 更新项目 |
| DELETE | /projects/{id} | 删除项目 |

### 类别
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /categories | 查询全部类别 |
| GET | /categories/{id} | 按ID查询 |
| POST | /categories | 创建类别 |
| PUT | /categories/{id} | 更新类别 |
| DELETE | /categories/{id} | 删除类别 |

### 维护窗口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /maintenance-windows | 查询维护窗口（resourceId 可选，startTime/endTime 可选） |
| POST | /maintenance-windows | 创建维护窗口 |
| PUT | /maintenance-windows/{id} | 更新维护窗口 |
| DELETE | /maintenance-windows/{id} | 删除维护窗口 |

### 用户与认证
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /auth/login | 登录 |
| POST | /auth/logout | 退出 |
| GET | /auth/me | 当前用户 |
| GET | /users | 用户列表 |
| POST | /users | 创建用户 |

## 快速开始

### 方式零：脚本一键启动
1. （首次）赋予执行权限：
   ```bash
   chmod +x start.sh stop.sh reset.sh
   ```
2. 启动（会拉起 MySQL 并执行 `mvn spring-boot:run`）：
   ```bash
   ./start.sh
   ```
3. 停止 MySQL 容器：
   ```bash
   ./stop.sh
   ```
4. 重置数据库（清空并重新初始化）：
   ```bash
   ./reset.sh
   ```

### 方式一：Docker 启动 MySQL
1. 启动数据库（端口 3307）：
   ```bash
   docker-compose up -d
   ```
2. 启动后端：
   ```bash
   mvn spring-boot:run
   ```
3. 访问前端：
   `http://localhost:8080/api/index.html`

### 方式二：本地 MySQL
1. 创建数据库并初始化：
   ```bash
   mysql -u root -p
   CREATE DATABASE asset_management DEFAULT CHARACTER SET utf8mb4;
   mysql -u root -p asset_management < src/main/resources/schema.sql
   ```
2. 修改配置文件 `src/main/resources/application.yml` 中的数据源地址和账号密码。
3. 运行应用：
   ```bash
   mvn spring-boot:run
   ```

### 默认账号
- 管理员：admin / admin123
- 普通用户：user / user123

## 备注
本项目用于软件工程实践课程学习。
