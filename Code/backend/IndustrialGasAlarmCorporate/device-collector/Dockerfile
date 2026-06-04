# ============================================================
# Collector 数据采集服务 Dockerfile
# ============================================================
FROM eclipse-temurin:17-jre-alpine

RUN apk add --no-cache fontconfig ttf-dejavu && \
    addgroup -S appgroup && adduser -S appuser -G appgroup

ENV LANG=C.UTF-8 \
    LC_ALL=C.UTF-8

COPY device-collector/target/*.jar /app/app.jar

RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", \
    "-Dfile.encoding=UTF-8", \
    "-Dsun.jnu.encoding=UTF-8", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-Xmx192m", \
    "-Xms96m", \
    "-jar", "/app/app.jar"]
