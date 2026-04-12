# Shuang-Pro

> AI 视频/图片生成 + 代理分销系统

## 项目介绍

Shuang-Pro 是一个基于 Spring Boot 3 + MyBatis Plus 的 AI 内容生成平台，支持通过代理分销体系进行推广。用户可以使用积分生成 AI 视频和图片，代理可以通过推广获得积分分成。

点击一键拆解，那就要对接后段了，你帮我来选型吧，阿里的大模型百炼我是coding plan会员，豆包也可以，你帮我做好这两套，如果百炼的会员不行，那就单独用阿里的key做就可以了


### 核心功能

- **代理管理**: 支持一级/二级代理体系，可配置分成比例
- **积分系统**: 代理可分配积分给用户，用户用积分生成 AI 内容
- **AI 生成**: 支持 AI 视频生成和 AI 图片生成
- **支付充值**: 支持多种支付方式的充值功能

### 技术栈

- **后端框架**: Spring Boot 3.5.9
- **ORM**: MyBatis Plus 3.5.15
- **数据库**: MySQL 8.0+
- **缓存**: Redis
- **JDK**: 17

## 快速开始

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+
- Node.js 16+

### 一键启动（推荐）

```bash
# 进入项目根目录
cd /Users/h/Desktop/shuang-qian

# 一键启动（自动检查依赖 + 初始化数据库 + 编译后端 + 启动前后端）
./script/startup.sh

# 一键停止
./script/shutdown.sh
```

> 启动脚本会依次检查 MySQL、Redis 数据库状态，自动初始化表结构，编译后端 JAR，然后同时启动后端（端口 48080）和前端（端口 8888）。

### 访问地址

| 服务 | 地址 |
|------|------|
| 前端界面 | http://localhost:8888 |
| 后端接口 | http://localhost:48080 |
| 接口文档 | http://localhost:48080/swagger-ui |
| 爆款拆解 | http://localhost:8888/ai/video/analyze |

## 项目结构

```
shuang-pro/
├── shuang-dependencies/          # 依赖管理
├── shuang-framework/             # 框架核心
├── shuang-server/                # 主启动项目
├── shuang-module-system/         # 系统模块
├── shuang-module-infra/          # 基础设施模块
├── shuang-module-pay/            # 支付模块
├── shuang-module-member/         # 会员模块
├── shuang-module-mall/           # 商城模块
│   ├── shuang-module-product/    # 产品模块
│   ├── shuang-module-promotion/  # 营销模块
│   ├── shuang-module-trade/      # 交易模块
│   └── shuang-module-statistics/ # 统计模块
├── shuang-module-ai/             # AI 生成模块
├── shuang-ui/                    # Vue 前端目录
│   └── zhuimeng-dream/           # 当前使用的 Vite 前端
└── shuang-module-agency/         # 代理管理模块
```

## 代理体系说明

### 代理等级

| 等级 | 获取条件 | 分成比例 |
|------|----------|----------|
| 一级代理 | 支付 999 元代理费 OR 直推≥100 人 | 下级充值的 20% |
| 二级代理 | 一级代理邀请的下级 | 下级充值的 8% |

### 积分规则

- **获取方式**: 仅能通过代理分配获得
- **用途**: 用于 AI 视频/图片生成
- **消耗**:
  - AI 视频生成：10 积分/次
  - AI 图片生成：2 积分/次

## API 接口

### 代理管理接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/admin/agency/user/list` | GET | 代理用户列表 |
| `/admin/agency/user/upgrade` | POST | 升级一级代理 |
| `/admin/agency/config/list` | GET | 代理配置列表 |
| `/admin/agency/config/save` | POST | 保存代理配置 |
| `/app/agency/user/my` | GET | 获取我的代理信息 |
| `/app/agency/user/bind` | POST | 绑定上级代理 |
| `/app/agency/point/transfer` | POST | 分配积分给用户 |

## 开发规范

详见 [CLAUDE.md](CLAUDE.md)

## 需求文档

详见 [docs/requirements.md](docs/requirements.md)

## 许可证

MIT License
# shuang-qian
