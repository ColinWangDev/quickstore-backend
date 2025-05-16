# QuickStore Backend

QuickStore是一个现代化的仓库管理系统（WMS），使用Spring Boot和PostgreSQL构建。

## 技术栈

- Java 17
- Spring Boot 3.2.3
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT认证
- Maven

## 开发环境要求

- JDK 17或更高版本
- Maven 3.6或更高版本
- PostgreSQL 12或更高版本

## 快速开始

1. 克隆项目
```bash
git clone https://github.com/your-username/quickstore-backend.git
cd quickstore-backend
```

2. 配置数据库
- 确保PostgreSQL服务运行中
- 创建数据库：`quickstore_db`
- 配置`src/main/resources/application.yml`中的数据库连接信息

3. 构建并运行
```bash
mvn clean install
mvn spring-boot:run
```

4. 访问API
```
http://localhost:8080/api
```

## 项目结构

```
src/main/java/com/quickstore/
├── config/         # 配置类
├── controller/     # REST控制器
├── model/         # 实体类
├── repository/    # 数据访问层
├── service/       # 业务逻辑层
├── security/      # 安全相关配置
└── exception/     # 异常处理
```

## 环境变量

- `POSTGRES_USER`: 数据库用户名（默认：postgres）
- `POSTGRES_PASSWORD`: 数据库密码（默认：postgres）
- `JWT_SECRET`: JWT密钥（生产环境必须修改）
