# ============================================================
# Collector 数据采集服务 Dockerfile
# ============================================================
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY device-collector/target/*.jar /app/app.jar

RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8081

ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-Xmx384m", \
    "-Xms128m", \
    "-jar", "/app/app.jar"]
