# ============================================================
# App 主应用 Dockerfile
# 构建前需先在项目根目录执行: mvn clean package -DskipTests
# ============================================================
FROM eclipse-temurin:17-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup

COPY app/target/*.jar /app/app.jar

RUN mkdir -p /data/uploads /app/logs && chown -R appuser:appgroup /data /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseG1GC", \
    "-XX:MaxGCPauseMillis=200", \
    "-Xmx256m", \
    "-Xms128m", \
    "-jar", "/app/app.jar"]
