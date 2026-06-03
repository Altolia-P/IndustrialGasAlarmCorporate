# ============================================================
# 前端多阶段构建
# Stage 1: Node 构建 Vue 项目
# Stage 2: Nginx 托管静态文件 + 反向代理
# ============================================================
FROM node:20-alpine AS builder

ARG VITE_API_BASE_URL=/api/v1
ARG VITE_AI_CHAT_TIMEOUT=30000
ENV VITE_API_BASE_URL=$VITE_API_BASE_URL
ENV VITE_AI_CHAT_TIMEOUT=$VITE_AI_CHAT_TIMEOUT

WORKDIR /app

COPY package.json package-lock.json ./
RUN npm ci

COPY . .
RUN npx vite build

# ============================================================
FROM nginx:alpine

RUN rm -f /etc/nginx/conf.d/default.conf

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
