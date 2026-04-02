# CLAUDE.md

## 项目概览

- 项目名称：`shuang-pro`
- 项目类型：基于 Spring Boot 的多模块 Maven 后端项目
- 业务方向：AI 图片/视频生成 + 代理分销系统
- Java 版本：`17`
- 根包名：`cn.shuang.boot`

## 技术栈

- `Spring Boot 3.5.9`
- `MyBatis Plus`
- `MySQL`
- `Redis`
- `Maven` 多模块构建
- `Lombok`
- `MapStruct`

## 模块结构

- `shuang-server`：服务启动模块
- `shuang-framework`：通用框架封装
- `shuang-dependencies`：统一依赖管理
- `shuang-module-system`：系统基础能力，包含用户、角色、权限等
- `shuang-module-infra`：基础设施能力，包含代码生成、定时任务等
- `shuang-module-pay`：支付与钱包能力
- `shuang-module-mall`：商城相关能力
- `shuang-module-member`：会员相关能力
- `shuang-module-ai`：AI 图片/视频生成相关能力
- `shuang-module-agency`：代理分销模块
- `yudao-ui`：前端工程

## 当前业务重点

### 代理分销

- 代理体系至少包含一级代理、二级代理
- 分佣、积分、上下级关系必须可追踪
- 涉及积分变化的操作必须保留流水记录
- 涉及代理关系调整的操作必须保留日志

### AI 生成

- 图片生成、视频生成都需要消耗积分
- 积分扣减要和业务请求保持一致性
- 失败场景需要明确回滚或补偿策略

## 推荐目录约定

### 后端分层

- `controller`：接口层
- `service`：业务层
- `dal`：数据访问层
- `convert`：对象转换
- `enums`：枚举定义
- `bo`：业务对象
- `vo`：接口请求/响应对象
- `dataobject`：数据库实体对象
- `mapper`：MyBatis Mapper

### 接口分组

- `controller.admin`：管理端接口
- `controller.app`：用户端接口

## 编码规范

- 实体类优先使用 `DO` 后缀，例如 `AgencyUserDO`
- 请求对象使用 `ReqVO` 后缀
- 响应对象使用 `RespVO` 后缀
- Service 接口与实现分离，实现类使用 `ServiceImpl` 后缀
- Controller 返回值统一使用项目已有的通用返回结构
- 优先复用现有基础类、枚举、常量和工具类，不重复造轮子

## 数据约定

- 表名使用模块前缀 + 语义名，例如 `agency_user`
- 主键统一使用 `id`
- 通用字段通常包含：`create_time`、`update_time`、`deleted`
- 金额和积分优先保持统一的数据表达方式，避免在同一条链路中混用单位

## 开发要求

- 修改代码前先确认改动属于哪个模块，不要跨模块随意堆逻辑
- 优先在已有模块内扩展，不要新增重复能力
- 新增接口时同时补齐参数校验、权限控制和错误处理
- 新增数据库字段或表时，同步补充 SQL 或迁移脚本
- 涉及支付、积分、分佣的改动，优先考虑幂等、事务和审计

## 常用命令

```bash
# 编译
mvn clean package -DskipTests

# 运行服务
java -jar shuang-server/target/shuang-server.jar
```

## 提交规范

- `feat:` 新功能
- `fix:` 修复问题
- `refactor:` 重构
- `docs:` 文档更新
- `test:` 测试相关
- `chore:` 构建、配置或工具调整

## 给 Claude / 代码助手的工作指引

- 优先阅读根目录 `pom.xml` 和目标模块代码，再动手修改
- 保持现有模块边界和命名风格
- 不要随意改动无关模块
- 涉及接口、数据库、配置的改动时，明确说明影响面
- 如果需求不清楚，先确认范围，再修改代码

## gstack

本项目已安装 [gstack](https://github.com/garrytan/gstack) —— 一套由 YC CEO Garry Tan 开源的 AI 代理协作工具。

**Web 浏览**：使用 `/browse` skill 进行所有网页浏览操作，不要使用 `mcp__claude-in-chrome__*` 工具。

**可用技能**：
- `/office-hours` - 创业诊断和头脑风暴
- `/plan-ceo-review` - 产品规划审查
- `/plan-eng-review` - 技术方案审查
- `/plan-design-review` - 设计审查
- `/design-consultation` - 设计系统咨询
- `/design-shotgun` - 视觉设计探索
- `/design-html` - HTML 设计生成
- `/review` - PR 代码审查
- `/ship` - 发布流程
- `/land-and-deploy` - 合并部署
- `/canary` - 金丝雀监控
- `/benchmark` - 性能基准测试
- `/browse` - 无头浏览器操作
- `/connect-chrome` - 连接 Chrome 浏览器
- `/qa` - 质量保证测试
- `/qa-only` - 仅 QA 报告
- `/design-review` - 设计审查
- `/setup-browser-cookies` - 浏览器 Cookie 配置
- `/setup-deploy` - 部署配置
- `/retro` - 项目回顾
- `/investigate` - 问题调查
- `/document-release` - 发布文档
- `/codex` - 多 AI 意见征询
- `/cso` - 安全审计（OWASP + STRIDE）
- `/autoplan` - 自动规划流程
- `/careful` - 安全操作检查
- `/freeze` - 代码冻结
- `/guard` - 安全检查
- `/unfreeze` - 解冻代码
- `/gstack-upgrade` - gstack 升级
- `/learn` - 项目学习
- `/checkpoint` - 项目进度检查

如果 gstack 技能无法使用，运行 `cd .claude/skills/gstack && ./setup` 重新构建。
