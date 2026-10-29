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

#### 敏感词过滤

内置「辱骂 / 色情」两类别默认词库，Trie(DFA) 字符树 O(n) 扫描；匹配前归一化（大小写折叠、全角转半角、词中干扰符剥离），命中位置映射回原文。实例构建后不可变，可安全并发复用。

| 类 | 说明 |
|---|---|
| `SensitiveWordFilter` | 过滤核心，`builder()` 构建，`detect`/`mask` 两个入口 |
| `SensitiveWordHit` | record：`word`（归一词）、`category`、`startIndex`/`endIndex`（原文坐标，含端点） |
| `SensitiveWordCategory` | enum：`INSULT`（辱骂）、`PORNO`（色情） |
| `WordEntry` | record：`word` + `category`，override 词表条目 |

依赖坐标同上（`com.octopus.demo:java-common-util`）。

默认用法：

```java
SensitiveWordFilter filter = SensitiveWordFilter.builder().build();

List<SensitiveWordHit> hits = filter.detect("你个傻逼"); // [SensitiveWordHit[word=傻逼, category=INSULT, startIndex=2, endIndex=3]]
String masked = filter.mask("你个傻逼");                  // "你个**"
```

`detect(null)` / `detect("")` 返回空列表；`mask(null)` 原样返回；干净文本零误报。变体（`傻★逼`、`ＦＵＣＫ`、`傻 逼`）同样命中，位置指回原文整段。

自定义词库：

```java
// append：在默认词库上追加业务专属违禁词（显式指定类别），检测按并集生效
SensitiveWordFilter appended = SensitiveWordFilter.builder()
        .appendWords(SensitiveWordCategory.INSULT, List.of("专属违禁词"))
        .build();

// override：完全替换默认词库，实例只认传入词表
SensitiveWordFilter override = SensitiveWordFilter.builder()
        .overrideWords(List.of(new WordEntry("专属违禁词", SensitiveWordCategory.PORNO)))
        .build();
```

合并语义：append 去重；同词跨类别冲突时自定义类别覆盖默认词库。换词库 = 重建实例，不支持运行时热重载。

词库维护：默认词库为 classpath 资源 `src/main/resources/sensitive-words.txt`，行式 `类别:词`（如 `INSULT:傻逼`），`#` 开头为注释；增删词直接改文件即可生效，无需改代码。非法行跳过、不中断构建。

## 构建命令

```bash
mvn clean install
```

## 环境要求

- JDK 21
- Maven 3.9+