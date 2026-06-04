# ============================================================
# App 主应用 Dockerfile
# 构建前需先在项目根目录执行: mvn clean package -DskipTests
# ============================================================
FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache fontconfig ttf-dejavu && \
    addgroup -S appgroup && adduser -S appuser -G appgroup

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

COPY app/target/*.jar /app/app.jar

RUN mkdir -p /data/uploads /app/logs && chown -R appuser:appgroup /data /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
    "-Dfile.encoding=UTF-8", \
    "-Dsun.jnu.encoding=UTF-8", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-Xmx256m", \
    "-Xms128m", \
    "-jar", "/app/app.jar"]
