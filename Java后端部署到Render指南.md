# Java Spring Boot 后端部署到 Render 完整指南

## 概述
本指南将帮助你将 QuickStore 的 Java Spring Boot 后端项目部署到 Render 平台，实现稳定、可扩展的在线 API 服务。

## 前置条件
- ✅ Java Spring Boot 项目已开发完成
- ✅ 项目已推送到 Git 仓库（GitHub/GitLab/Bitbucket）
- ✅ PostgreSQL 数据库已准备就绪（稍后部署到 Neon）
- ✅ 本地开发环境正常运行

---

## 第一步：准备项目配置

### 1.1 检查项目结构
确保你的项目包含以下关键文件：
```
quickstore-backend/
├── src/main/java/com/quickstore/
│   ├── QuickStoreApplication.java
│   ├── controller/
│   ├── service/
│   ├── model/
│   ├── repository/
│   ├── security/
│   └── config/
├── src/main/resources/
│   └── application.yml
├── pom.xml
└── README.md
```

### 1.2 创建生产环境配置文件
创建 `src/main/resources/application-prod.yml`：
```yaml
spring:
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        dialect: org.hibernate.dialect.PostgreSQLDialect
  security:
    jwt:
      secret: ${JWT_SECRET}
      expiration: 86400000

server:
  port: ${PORT:8080}
  servlet:
    context-path: /api

logging:
  level:
    com.quickstore: INFO
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
```

### 1.3 更新主配置文件
修改 `src/main/resources/application.yml`，添加生产环境配置：
```yaml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}
  datasource:
    url: jdbc:postgresql://localhost:5432/quickstore_db
    username: postgres
    password: colin1234
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.PostgreSQLDialect
  security:
    jwt:
      secret: ${JWT_SECRET:your-secret-key-here-minimum-256-bits}
      expiration: 86400000

server:
  port: 8080
  servlet:
    context-path: /api

logging:
  level:
    com.quickstore: DEBUG
    org.springframework.security: INFO
```

### 1.4 更新 CORS 配置
修改 `SecurityConfig.java` 中的 CORS 配置，允许前端域名：
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:3000", 
        "http://localhost:3001",
        "https://your-frontend-domain.vercel.app"  // 添加你的 Vercel 域名
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

---

## 第二步：推送到 Git 仓库

### 2.1 提交配置更改
```bash
cd /Users/colinwang/Documents/IMPORTANT\ INFORMATION/Apps/QuickStore/quickstore-backend
git add .
git commit -m "Add production configuration for Render deployment"
git push origin main
```

---

## 第三步：部署到 Render

### 3.1 注册 Render 账号
1. 访问 [render.com](https://render.com)
2. 点击 "Get Started for Free"
3. 推荐使用 GitHub 账号登录（便于后续管理）

### 3.2 创建 Web Service
1. 登录 Render Dashboard
2. 点击 "New +" → "Web Service"
3. 选择 "Build and deploy from a Git repository"
4. 连接你的 GitHub 账号
5. 选择 `quickstore-backend` 仓库

### 3.3 配置服务设置
在服务配置页面填写：

**Basic Settings:**
- **Name**: `quickstore-backend`
- **Environment**: `Java`
- **Region**: `Oregon (US West)` 或 `Frankfurt (EU Central)`
- **Branch**: `main`

**Build & Deploy:**
- **Build Command**: `mvn clean package -DskipTests`
- **Start Command**: `java -jar target/quickstore-backend-0.0.1-SNAPSHOT.jar`
- **Java Version**: `17`

**Advanced Settings:**
- **Auto-Deploy**: `Yes` (推荐)
- **Health Check Path**: `/api/test/health`

### 3.4 配置环境变量
在 "Environment" 部分添加以下环境变量：

**必需的环境变量：**
```
SPRING_PROFILES_ACTIVE = prod
PORT = 10000
JWT_SECRET = your-super-secret-jwt-key-minimum-256-bits-long
```

**数据库相关（稍后从 Neon 获取）：**
```
DATABASE_URL = jdbc:postgresql://your-neon-host:5432/quickstore_db
DATABASE_USERNAME = your-neon-username
DATABASE_PASSWORD = your-neon-password
```

**可选的环境变量：**
```
SPRING_DATASOURCE_URL = jdbc:postgresql://your-neon-host:5432/quickstore_db
SPRING_DATASOURCE_USERNAME = your-neon-username
SPRING_DATASOURCE_PASSWORD = your-neon-password
```

### 3.5 部署服务
1. 点击 "Create Web Service"
2. 等待构建完成（通常 3-8 分钟）
3. 部署成功后，你会得到一个 Render 域名，如：`https://quickstore-backend-xxx.onrender.com`

---

## 第四步：测试部署

### 4.1 健康检查
访问健康检查端点：
```bash
curl https://your-render-domain.onrender.com/api/test/health
```

### 4.2 测试 API 端点
```bash
# 测试注册
curl -X POST https://your-render-domain.onrender.com/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123",
    "fullName": "Test User",
    "role": "user"
  }'

# 测试登录
curl -X POST https://your-render-domain.onrender.com/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "testpass123"
  }'
```

### 4.3 检查日志
在 Render Dashboard 中查看 "Logs" 标签页，确保没有错误信息。

---

## 第五步：配置数据库（Neon）

### 5.1 创建 Neon 数据库
1. 访问 [neon.tech](https://neon.tech)
2. 注册账号并创建新项目
3. 记录数据库连接信息：
   - Host
   - Database name
   - Username
   - Password
   - Port (通常是 5432)

### 5.2 更新 Render 环境变量
在 Render Dashboard 中更新数据库相关环境变量：
```
DATABASE_URL = jdbc:postgresql://your-neon-host:5432/quickstore_db
DATABASE_USERNAME = your-neon-username
DATABASE_PASSWORD = your-neon-password
```

### 5.3 重新部署
更新环境变量后，Render 会自动重新部署服务。

---

## 第六步：优化配置

### 6.1 创建 render.yaml 配置文件（可选）
在项目根目录创建 `render.yaml`：
```yaml
services:
  - type: web
    name: quickstore-backend
    env: java
    buildCommand: mvn clean package -DskipTests
    startCommand: java -jar target/quickstore-backend-0.0.1-SNAPSHOT.jar
    healthCheckPath: /api/test/health
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: JWT_SECRET
        generateValue: true
```

### 6.2 配置自定义域名（可选）
1. 在 Render Dashboard 中进入服务设置
2. 点击 "Custom Domains"
3. 添加你的自定义域名
4. 按照提示配置 DNS 记录

---

## 第七步：监控和维护

### 7.1 查看服务状态
- 在 Render Dashboard 中监控服务状态
- 查看 CPU、内存使用情况
- 监控响应时间和错误率

### 7.2 查看日志
- 实时日志：Dashboard → Logs
- 历史日志：可下载或通过 API 获取

### 7.3 自动部署
- 每次推送到 `main` 分支会自动触发部署
- 可以配置预览部署（PR 部署）

---

## 常见问题解决

### Q1: 构建失败
**问题**: Maven 构建失败
**解决**: 
- 检查 `pom.xml` 中的依赖版本
- 确保 Java 版本设置为 17
- 查看构建日志中的具体错误信息

### Q2: 服务启动失败
**问题**: 应用启动后立即停止
**解决**:
- 检查环境变量是否正确设置
- 确保数据库连接信息正确
- 查看启动日志中的错误信息

### Q3: 数据库连接失败
**问题**: 无法连接到 PostgreSQL 数据库
**解决**:
- 检查 Neon 数据库是否正常运行
- 验证连接字符串格式
- 确保 IP 白名单设置正确

### Q4: CORS 错误
**问题**: 前端无法访问后端 API
**解决**:
- 检查 CORS 配置中的允许域名
- 确保前端域名已添加到允许列表
- 验证请求头设置

### Q5: JWT 密钥问题
**问题**: JWT 相关错误
**解决**:
- 确保 `JWT_SECRET` 环境变量已设置
- 密钥长度至少 256 位
- 生产环境使用强随机密钥

---

## 性能优化建议

### 1. 数据库优化
- 为常用查询字段添加索引
- 使用连接池配置
- 定期清理日志表

### 2. 应用优化
- 启用 JVM 性能调优
- 配置适当的堆内存大小
- 使用缓存减少数据库查询

### 3. 监控设置
- 设置健康检查告警
- 监控响应时间
- 配置错误率告警

---

## 下一步

完成后端部署后，接下来需要：
1. 部署前端到 Vercel
2. 配置前端 API 地址
3. 进行端到端测试
4. 设置监控和告警

---

## 参考资源

- [Render 官方文档](https://render.com/docs)
- [Spring Boot 部署指南](https://spring.io/guides/gs/spring-boot-for-azure/)
- [PostgreSQL 连接配置](https://www.postgresql.org/docs/current/libpq-connect.html)
- [JWT 最佳实践](https://tools.ietf.org/html/rfc7519)

---

## 联系支持

如果在部署过程中遇到问题，可以：
1. 查看 Render 构建和运行日志
2. 检查 GitHub Issues
3. 联系 Render 支持团队
4. 参考本指南的常见问题部分
