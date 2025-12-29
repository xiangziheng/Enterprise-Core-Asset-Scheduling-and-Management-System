# 企业核心资产调度管理系统

## 项目简介

企业核心资产调度管理系统（Enterprise Asset Management System）是一个用于解决企业内部核心资产（如服务器、会议室、昂贵设备）在多项目间的分配与调度问题的管理系统。

**核心功能**：防止资源在同一时间段内被重复分配（即冲突检测）

## 技术栈

### 后端技术
- **框架**：Spring Boot 3.2.0
- **ORM**：MyBatis 3.0.3
- **数据库**：MySQL 8.0 / H2（测试）
- **Java版本**：17

### 前端技术
- **框架**：Vue 3
- **UI组件库**：Element Plus
- **HTTP客户端**：Axios

### 开发工具
- **构建工具**：Maven
- **版本控制**：Git
- **开发环境**：JDK 17, MySQL 8.0

## 项目结构

```
软工实践项目/
├── src/
│   ├── main/
│   │   ├── java/com/asset/management/
│   │   │   ├── AssetManagementApplication.java  # 主启动类
│   │   │   ├── controller/                      # 控制器层
│   │   │   │   ├── AllocationController.java    # 资源分配控制器（核心）
│   │   │   │   ├── ResourceController.java      # 资源管理控制器
│   │   │   │   ├── ProjectController.java       # 项目管理控制器
│   │   │   │   └── CategoryController.java      # 类别管理控制器
│   │   │   ├── service/                         # 服务层
│   │   │   │   ├── AllocationService.java       # 资源分配服务（含冲突检测算法）
│   │   │   │   ├── ResourceService.java
│   │   │   │   ├── ProjectService.java
│   │   │   │   └── CategoryService.java
│   │   │   ├── mapper/                          # 数据访问层
│   │   │   │   ├── AllocationMapper.java
│   │   │   │   ├── ResourceMapper.java
│   │   │   │   ├── ProjectMapper.java
│   │   │   │   └── CategoryMapper.java
│   │   │   ├── entity/                          # 实体类
│   │   │   │   ├── Allocation.java              # 资源分配实体
│   │   │   │   ├── Resource.java                # 资源实体
│   │   │   │   ├── Project.java                 # 项目实体
│   │   │   │   └── Category.java                # 类别实体
│   │   │   ├── dto/                             # 数据传输对象
│   │   │   │   ├── AllocationRequest.java
│   │   │   │   └── Result.java                  # 统一响应结果
│   │   │   └── exception/                       # 异常处理
│   │   │       ├── ResourceConflictException.java
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── mapper/                          # MyBatis XML映射文件
│   │       │   ├── AllocationMapper.xml
│   │       │   ├── ResourceMapper.xml
│   │       │   ├── ProjectMapper.xml
│   │       │   └── CategoryMapper.xml
│   │       ├── static/                          # 静态资源（前端）
│   │       │   ├── index.html                   # 前端页面
│   │       │   └── app.js                       # 前端逻辑
│   │       ├── application.yml                  # 配置文件
│   │       └── schema.sql                       # 数据库初始化脚本
│   └── test/                                    # 测试代码
├── pom.xml                                      # Maven配置文件
└── README.md                                    # 项目说明文档
```

## 核心功能模块

### 1. 资源分配管理（核心功能）
- **新建分配**：将指定资源在特定时间段内分配给某项目
- **冲突检测**：自动检测新分配的时间段是否与已有分配重叠
- **编辑分配**：修改分配信息（含冲突检测）
- **取消/删除分配**：取消或删除分配记录
- **查询分配**：查看所有分配记录、按资源查询、按项目查询

### 2. 资源管理
- 录入企业资产信息
- 指定资源类别
- 设置资源状态（可用、维护中、已退役）
- 资源的增删改查

### 3. 项目管理
- 创建需要使用资源的项目
- 管理项目信息和状态
- 项目的增删改查

### 4. 类别管理
- 管理资源类别（如：计算资源、场地资源、设备资源）
- 类别的增删改查

### 5. 用户与权限管理
- 用户登录：账号 + 密码
- 角色区分：管理员（ADMIN）与普通用户（USER）
- 权限规则：普通用户可申请资源（新建分配），管理员可管理资源/项目/类别/维护窗口

## 核心算法：冲突检测

### 算法原理

两个时间段存在冲突，当且仅当它们有重叠部分。

时间段A `[startA, endA]` 与 时间段B `[startB, endB]` 冲突的条件是：

```
startA < endB AND startB < endA
```

### 算法实现

```java
// 核心SQL查询（AllocationMapper.xml）
SELECT * FROM t_allocation
WHERE resource_id = #{resourceId}
  AND status = 'ACTIVE'
  AND start_time < #{endTime}
  AND end_time > #{startTime}
```

### 冲突情况分析

1. **完全重叠**：新分配完全包含在已有分配内
2. **部分重叠**：新分配的开始或结束时间在已有分配的范围内
3. **完全覆盖**：新分配完全覆盖已有分配

只要查询结果不为空，就表示存在冲突。

## 快速开始

### 方法一：使用Docker（推荐⭐）

#### 1. 环境准备

- Docker Desktop（macOS/Windows）或 Docker Engine（Linux）
- JDK 17+
- Maven 3.6+

#### 2. 启动MySQL容器

```bash
# 在项目根目录执行
docker-compose up -d
```

这个命令会自动：
- ✅ 拉取MySQL 8.0镜像
- ✅ 创建并启动MySQL容器
- ✅ 自动创建数据库
- ✅ 自动执行初始化脚本

#### 3. 启动应用

```bash
mvn spring-boot:run
```

#### 4. 访问前端

打开浏览器：http://localhost:8080/api/index.html

#### 5. 默认账号

- 管理员：admin / admin123
- 普通用户：user / user123

**详细说明请查看：[Docker使用指南.md](Docker使用指南.md)**

---

### 方法二：传统方式（手动安装MySQL）

#### 1. 环境准备

- JDK 17+
- Maven 3.6+
- MySQL 8.0+

#### 2. 数据库配置

1. 创建数据库：

```bash
mysql -u root -p
CREATE DATABASE asset_management DEFAULT CHARACTER SET utf8mb4;
```

2. 执行初始化脚本：

```bash
mysql -u root -p asset_management < src/main/resources/schema.sql
```

3. 修改配置文件 `src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/asset_management?useUnicode=true&characterEncoding=utf8
    username: root
    password: your_password  # 修改为你的MySQL密码
```

#### 3. 启动后端

```bash
# 使用Maven启动
mvn spring-boot:run

# 或者先编译再运行
mvn clean package
java -jar target/asset-management-system-1.0.0.jar
```

后端服务将在 `http://localhost:8080/api` 启动

#### 4. 访问前端

打开浏览器访问：`http://localhost:8080/api/index.html`

或直接打开 `src/main/resources/static/index.html` 文件（需要修改 `app.js` 中的 API_BASE 地址）

## API接口文档

### 基础响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

### 资源分配接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /allocations | 查询所有分配 |
| GET | /allocations/{id} | 根据ID查询 |
| GET | /allocations/resource/{resourceId} | 按资源查询 |
| GET | /allocations/project/{projectId} | 按项目查询 |
| POST | /allocations | 创建分配（含冲突检测） |
| PUT | /allocations/{id} | 更新分配 |
| DELETE | /allocations/{id} | 删除分配 |
| POST | /allocations/check-conflict | 检测冲突 |

### 资源管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /resources | 查询所有资源 |
| GET | /resources/{id} | 根据ID查询 |
| GET | /resources/category/{categoryId} | 按类别查询 |
| POST | /resources | 创建资源 |
| PUT | /resources/{id} | 更新资源 |
| DELETE | /resources/{id} | 删除资源 |

### 项目管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /projects | 查询所有项目 |
| GET | /projects/{id} | 根据ID查询 |
| POST | /projects | 创建项目 |
| PUT | /projects/{id} | 更新项目 |
| DELETE | /projects/{id} | 删除项目 |

### 类别管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /categories | 查询所有类别 |
| GET | /categories/{id} | 根据ID查询 |
| POST | /categories | 创建类别 |
| PUT | /categories/{id} | 更新类别 |
| DELETE | /categories/{id} | 删除类别 |

## 面向对象设计

### 领域模型
- **Resource** (资源)：企业拥有的实体资产
- **Category** (类别)：资源的分类
- **Project** (项目)：资源的使用方
- **Allocation** (分配)：关联资源与项目的中间对象
- **Conflict** (冲突)：当分配发生时间重叠时产生的逻辑概念

### 类图设计
详见项目技术报告

## 注意事项

1. 首次运行前请确保数据库已创建并执行初始化脚本
2. 修改配置文件中的数据库连接信息

## 许可证

本项目仅用于软件工程实践课程学习，未开源。

