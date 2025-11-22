# 文件：后端项目根目录/Dockerfile
# 基于官方 OpenJDK 17 精简版（只有 300MB 左右）
FROM openjdk:17-jdk-slim

# 作者信息（可选）
MAINTAINER 阿明楷 <your-email@example.com>

# 设置时区为上海（避免日志时间差8小时）
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建应用目录
WORKDIR /app

# 先复制 pom.xml 和 maven wrapper（如果有的话），利用 Docker 缓存加速
COPY pom.xml .
# 如果你用了 maven wrapper
# COPY .mvn/ .mvn
# COPY mvnw mvnw
# COPY mvnw.cmd mvnw.cmd

# 复制源码
COPY src ./src

# 打包（跳过测试，节省时间）
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    cp target/*.jar app.jar && \
    apt-get purge -y maven && apt-get autoremove -y && rm -rf /var/lib/apt/lists/*

# 暴露端口（你的 application.yml 配置的端口，默认 8080）
EXPOSE 8080

# 启动命令（使用 -XX:+UseG1GC 更省内存）
ENTRYPOINT ["java", "-XX:+UseG1GC", "-jar", "/app/app.jar"]