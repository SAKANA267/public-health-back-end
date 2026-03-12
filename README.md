# Public Health API

公共卫生系统 REST API 服务端

## 项目简介

基于 Spring Boot 3.5.10 构建的公共卫生系统后端 API，采用 JWT 认证、JPA 持久化，并集成智谱 AI 提供智能对话能力。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 | 编程语言 |
| Spring Boot | 3.5.10 | 应用框架 |
| Spring Data JPA | - | ORM 持久化 |
| Spring Security | - | 安全框架 |
| MySQL | 8.0+ | 主数据库 |
| H2 | - | 测试数据库 |
| JWT | 0.12.3 | Token 认证 |
| Lombok | - | 代码简化 |
| Spring AI | 1.0.0 | AI 框架 |
| ZhipuAI | - | LLM 模型（通过 SpringAI 集成） |

## 功能模块

### 认证授权 (`/api/auth`)
- 用户注册/登录
- JWT Token 颁发与刷新
- 登录历史记录

### 系统用户 (`/api/users`)
- 用户 CRUD 操作
- 密码修改
- 用户查询（分页/条件）

### 报告卡 (`/api/report-cards`)
- 报告卡创建/更新/删除
- 报告卡审核
- 报告卡查询（分页/条件）

### 操作日志 (`/api/operation-logs`)
- 操作日志记录（基于 AOP）
- 操作日志查询与统计

### AI 助手 (`/api/ai`)
- 智能对话（基于 SpringAI + 智谱 AI）
- 意图识别
- 业务实体操作执行（Function Calling）
- 结构化输出（Structured Output）
- 会话管理

## 项目结构

```
src/main/java/com/publichealth/public_health_api/
├── annotation/           # 自定义注解
├── aspect/              # AOP 切面
├── common/              # 公共类（ApiResponse, PageResult）
├── config/              # 配置类
├── context/             # 用户上下文
├── exception/           # 异常处理
├── module/              # 业务模块
│   ├── ai/             # AI 助手模块
│   ├── auth/           # 认证授权模块
│   ├── loginhistory/   # 登录历史模块
│   ├── operationlog/   # 操作日志模块
│   ├── reportcard/     # 报告卡模块
│   └── sysuser/        # 系统用户模块
├── security/           # 安全相关（JWT）
└── util/               # 工具类
```

## 环境要求

- JDK 21+
- Maven 3.8+
- MySQL 8.0+
- 智谱 AI API Key（用于 AI 功能）

## 快速开始

### 1. 克隆项目

```bash
git clone <repository-url>
cd public-health-api
```

### 2. 配置数据库

创建 MySQL 数据库：

```sql
CREATE DATABASE public_health CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置 application.properties

编辑 `src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/public_health
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT 配置
jwt.secret=your-secret-key-change-in-production
jwt.access-token-expiration=900000        # 15分钟
jwt.refresh-token-expiration=604800000    # 7天

# AI 配置
ai.llm.api-key=your-zhipu-ai-api-key
```

### 4. 构建并运行

```bash
# 编译
./mvnw clean compile

# 运行
./mvnw spring-boot:run

# 或打包后运行
./mvnw clean package
java -jar target/public-health-api-0.0.1-SNAPSHOT.jar
```

服务启动后访问：`http://localhost:8080`

## API 文档

详细的 API 文档请查看 [docs](./docs) 目录：

- [认证授权 API](./docs/auth_api.md)
- [系统用户 API](./docs/sysuser_api.md)
- [报告卡 API](./docs/reportcard_api.md)
- [登录历史 API](./docs/login_history_api.md)
- [AI 助手 API](docs/ai/ai_api.md)
- [模块设计手册](./docs/module-design-manual.md)

## 认证机制

项目使用 JWT (JSON Web Token) 进行身份认证：

1. **登录**：调用 `/api/auth/login` 获取 `accessToken` 和 `refreshToken`
2. **请求认证**：在请求头中携带 `Authorization: Bearer <accessToken>`
3. **Token 刷新**：当 `accessToken` 过期时，使用 `refreshToken` 调用 `/api/auth/refresh` 获取新的 `accessToken`

## 开发指南

### 代码规范

- 使用 Lombok 注解简化代码（`@RequiredArgsConstructor`, `@Data`, `@Slf4j`）
- Controller 返回统一的 `ApiResponse<T>` 包装
- Service 层修改数据的方法使用 `@Transactional` 注解
- DTO 使用 Jakarta Validation 注解进行参数校验

### 添加新功能

参考 [CLAUDE.md](./CLAUDE.md) 中的详细指南：

1. 创建 Entity（实体类）
2. 创建 Repository（继承 JpaRepository）
3. 创建 DTO（请求/响应对象）
4. 创建 Service（业务逻辑）
5. 创建 Controller（REST 端点）

### 操作日志

使用 `@OperationLog` 注解自动记录操作日志：

```java
@OperationLog(module = "用户管理", operation = "创建用户", description = "创建新用户")
public ApiResponse<SysUserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
    // ...
}
```

## 依赖说明

### 核心依赖
- `spring-boot-starter-web` - Web 应用
- `spring-boot-starter-data-jpa` - JPA 持久化
- `spring-boot-starter-security` - 安全框架
- `spring-boot-starter-validation` - 参数校验
- `spring-boot-starter-aop` - AOP 支持

### 第三方依赖
- `spring-ai-starter-model-zhipuai` - SpringAI 智谱 AI 集成
- `jjwt-*` - JWT 实现
- `lombok` - 代码生成
- `mysql-connector-j` - MySQL 驱动
- `h2` - 内存数据库（测试用）

## 许可证

MIT License

## 贡献

欢迎提交 Issue 和 Pull Request！
