# 多阶段构建 Dockerfile
# 用于将Spring Boot应用也容器化（可选）

# 阶段1：构建
FROM maven:3.8-openjdk-17 AS builder
WORKDIR /app

# 复制pom.xml并下载依赖（利用Docker缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 阶段2：运行
FROM openjdk:17-jdk-slim
WORKDIR /app

# 复制jar包
COPY --from=builder /app/target/asset-management-system-1.0.0.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "app.jar"]

