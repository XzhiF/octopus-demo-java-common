# octopus-demo-java-common

octopus-demo 项目公共库，提供统一的依赖版本管理（BOM）和基础 Bean 定义。

## 模块说明

### java-common-dependencies

BOM 模块，统一管理所有依赖版本。其他项目通过 `<scope>import</scope>` 引入：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>com.octopus.demo</groupId>
            <artifactId>java-common-dependencies</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

当前管理版本：
- Spring Boot: 3.4.5

### java-common-bean

基础 Bean 模块，定义项目中使用的通用类型：

- **R\<T\>** — 统一响应包装（code、data、msg、timestamp）
- **PageQueryBean** — 分页查询参数（page、size、sort）
- **PageResultBean\<T\>** — 分页结果（count、list）
- **BaseException** — 业务异常基类（code 与 R.code 对应）

引用方式：

```xml
<dependency>
    <groupId>com.octopus.demo</groupId>
    <artifactId>java-common-bean</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

### java-common-auth

认证模块，提供基于注解的 userId 提取机制。

| 类 | 说明 |
|---|---|
| `@RequireAuth` | 标注 Controller 类或方法，支持 `required` 参数（默认 true） |
| `UserContext` | ThreadLocal 包装类，获取当前请求的 userId |
| `AuthInterceptor` | Spring MVC 拦截器，从 X-User-Id header 提取 userId |
| `AuthAutoConfiguration` | Spring Boot 自动配置，自动注册拦截器 |

引用方式：

```xml
<dependency>
    <groupId>com.octopus.demo</groupId>
    <artifactId>java-common-auth</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

使用示例：

```java
@RestController
@RequireAuth  // 类级别，所有方法默认需要 userId
public class UserController {

    @GetMapping("/profile")
    public R<?> getProfile() {
        Long userId = UserContext.getUserId();
        // ...
    }

    @GetMapping("/public-info")
    @RequireAuth(required = false)  // 方法级覆盖，userId 可选
    public R<?> getPublicInfo() {
        Long userId = UserContext.getUserId();  // 可能为 null
        // ...
    }
}
```

### java-common-util

工具模块，提供 JWT 生成与解析功能。

| 类 | 说明 |
|---|---|
| `JwtUtil` | JWT 生成与解析核心类，使用 HMAC-SHA256 签名 |
| `JwtProperties` | Spring Boot 配置属性（`octopus.jwt.secret-key`、`octopus.jwt.expiration-days`） |
| `JwtAutoConfiguration` | Spring Boot 自动配置，自动注册 JwtUtil bean |
| `JwtTokenExpiredException` | token 过期异常（code=401） |
| `JwtTokenInvalidException` | token 无效异常（code=401） |

引用方式：

```xml
<dependency>
    <groupId>com.octopus.demo</groupId>
    <artifactId>java-common-util</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

使用示例：

```java
// Spring Boot 自动配置方式（推荐）
@Autowired
private JwtUtil jwtUtil;

String token = jwtUtil.generateToken(userId);
Long userId = jwtUtil.parseToken(token);

// 纯工具方式
JwtUtil util = JwtUtil.createDefault();
String token = util.generateToken(1L);
```

配置项：

```yaml
octopus:
  jwt:
    secret-key: ""       # 空=自动随机生成，生产环境必须配置固定密钥
    expiration-days: 30  # 默认30天
```

## 构建命令

```bash
mvn clean install
```

## 环境要求

- JDK 21
- Maven 3.9+