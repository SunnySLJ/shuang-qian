# 项目计划总览

> 每次开发前先读此文件，开发结束后更新本文件。

## 项目基本信息

- **项目名称**: shuang-pro
- **类型**: Spring Boot 3 + Vue 3 多模块 Maven 项目
- **Java**: 17 | Spring Boot: 3.5.9
- **业务**: AI 图片/视频生成 + 代理分销 + 积分钱包

## 当前阶段

第一批次（上线前核心功能）

## 核心模块

| 模块 | 状态 | 说明 |
|------|------|------|
| shuang-module-ai | 已实现 | AI 生图/视频/分析/提示词优化 |
| shuang-module-pay | 已实现 | 钱包+积分+充值+支付 |
| shuang-module-agency | 已实现 | 二级代理+分佣+积分分配 |
| shuang-module-member | 已实现 | 会员+登录+邀请码 |
| shuang-ui/zhuimeng-dream | 已实现 | Vue 3 前端 |

## 技术难点清单

详见 `technical-difficulties.md`

## 开发进度

详见 `DEVELOPMENT-PROGRESS.md`

## 进度追踪

- 所有待开发功能记录在 `DEVELOPMENT-PROGRESS.md`，按优先级 P0/P1/P2 排列
- 开发过程中每次完成一个任务，同步更新该文件对应任务状态
- 完成进度更新后，同步更新 `docs/PROGRESS.md` 的进度报告
- 每次提交前确保 `DEVELOPMENT-PROGRESS.md` 对应任务已标记完成

## 常用命令

```bash
# 编译后端
mvn clean package -DskipTests

# 运行后端
java -jar shuang-server/target/shuang-server.jar

# 前端开发
cd shuang-ui/zhuimeng-dream && npm run dev
```

## 更新日志

- 2026-04-12: 初始化项目计划文档，完成全项目分析
