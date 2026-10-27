# java-common-util

xzf 组织公共工具模块，提供 JWT / AES / MD5 / 高亮 / 敏感信息脱敏等零依赖静态工具类。

## 敏感信息脱敏

用于日志输出与界面回显时对手机号、邮箱打码（`com.octopus.demo.common.util.MaskUtils`）。

```java
// 手机号：保留前 3 后 4
MaskUtils.maskPhone("13812345678");  // -> 138****5678

// 邮箱：本地部分保留首尾字符，域名不变
MaskUtils.maskEmail("alice@example.com");  // -> a***e@example.com
```

约定：非法或异常输入（`null`、空串、手机号长度不符、邮箱缺 `@`）一律原样返回（`null` 返回 `null`），脱敏绝不抛异常，保证日志路径零风险。
