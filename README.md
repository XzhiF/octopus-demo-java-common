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

## 构建命令

```bash
mvn clean install
```

## 环境要求

- JDK 21
- Maven 3.9+