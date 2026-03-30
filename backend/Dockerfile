# 后端 Dockerfile
# 多阶段构建：先 build 再打包成最小镜像

FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

# 复制 POM 和源码
COPY pom.xml .
COPY oa-platform-common/pom.xml oa-platform-common/
COPY oa-platform-framework/pom.xml oa-platform-framework/
COPY oa-platform-system/pom.xml oa-platform-system/
COPY oa-platform-workflow/pom.xml oa-platform-workflow/
COPY oa-platform-admin/pom.xml oa-platform-admin/

COPY oa-platform-common/src oa-platform-common/src
COPY oa-platform-framework/src oa-platform-framework/src
COPY oa-platform-system/src oa-platform-system/src
COPY oa-platform-workflow/src oa-platform-workflow/src
COPY oa-platform-admin/src oa-platform-admin/src

# 构建（跳过测试）
RUN mvn clean package -DskipTests -q

# 最终运行镜像
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# 复制 JAR
COPY --from=builder /app/oa-platform-admin/target/oa-platform-admin.jar app.jar

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]