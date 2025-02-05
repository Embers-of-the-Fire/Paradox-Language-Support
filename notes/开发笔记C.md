# 开发笔记

## 需求：脚本片段注入

### 需求描述

提供数种方式，可以为注入的脚本片段提供CWT规则上下文，以便后续提供高级语言功能。

注入的脚本片段包括以下几种情况：

* 手动语言注入
* markdown文件中的code fence的语言注入
* 对于脚本文件中作为参数值的用引号括起的参数值，强制进行语言注入（注入脚本片段）

高级语言功能如下所示：

* 基于CWT规则提供文件高亮
* 基于CWT规则提供引用解析、代码导航、快速文档等功能
* 基于CWT规则提供代码补全

规则上下文有以下几种情况：

* 直接的规则上下文
* 内联脚本调用中的规则上下文
* 内联脚本文件中的规则上下文
* 来自脚本参数值位置的规则上下文
* 来自手动语言注入的规则上下文


