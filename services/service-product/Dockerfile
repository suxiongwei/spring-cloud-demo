# 多阶段构建 - 编译阶段
FROM maven:3.8.1-openjdk-21 AS builder

WORKDIR /build

# 复制整个项目
COPY . /build/

# 编译服务
RUN mvn clean package -am -pl services/service-product -DskipTests

# 运行阶段
FROM openjdk:21-slim

WORKDIR /app

# 从构建阶段复制JAR文件
COPY --from=builder /build/services/service-product/target/service-product-*.jar app.jar

# 设置时区
ENV TZ=Asia/Shanghai

# 暴露端口
EXPOSE 8010

# 启动应用
ENTRYPOINT ["java", "-Duser.timezone=Asia/Shanghai", "-jar", "app.jar"]
