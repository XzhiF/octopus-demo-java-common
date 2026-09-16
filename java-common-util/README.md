# java-common-util

通用工具模块：JWT 生成与解析、AES 加密（AED）、MD5 摘要、文本高亮，以及 **MaskUtils PII 脱敏**。纯静态工具类无需 Spring 环境即可使用。

## MaskUtils 脱敏工具

`com.octopus.demo.common.util.MaskUtils` 为日志与前端展示提供手机号 / 身份证 / 邮箱三类 PII 的掩码，规则写死为常量（前3后4 / 前3后2 / 用户名掩码），null/空白原样返回、不抛异常。三个方法一行示例：

```java
MaskUtils.maskPhone("13812345678");         // 138****5678
MaskUtils.maskIdCard("110101199003078888"); // 110*************88
MaskUtils.maskEmail("someone@example.com"); // so****e@example.com
```

测试：`mvn -B -pl java-common-util test`（见 `src/test/java/com/octopus/demo/common/util/MaskUtilsTest.java`）。
