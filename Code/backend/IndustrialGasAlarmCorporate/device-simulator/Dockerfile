# ============================================================
# Simulator 设备模拟器 Dockerfile
# ============================================================
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY device-simulator/target/*.jar /app/app.jar

RUN mkdir -p /app/logs && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8082

ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-Xmx256m", \
    "-Xms128m", \
    "-jar", "/app/app.jar"]
