# 追梦Dream — AI 内容创作平台前端

## 项目简介

追梦Dream 是基于 Vue 3 + TypeScript + TailwindCSS 构建的新一代 AI 内容创作平台 UI，涵盖落地页、AI 图片生成、AI 视频生成、积分充值、代理分销等完整业务流程。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.x | 渐进式 JavaScript 框架 |
| TypeScript | 5.x | 类型安全 |
| Vite | 5.x | 下一代前端构建工具 |
| TailwindCSS | 4.x | 原子化 CSS 框架 |
| Vue Router | 4.x | SPA 路由管理 |
| Pinia | 2.x | 状态管理 |
| Framer Motion | — | 页面动画 |
| VueUse | — | Vue 组合式 API 工具库 |

## 目录结构

```
zhuimeng-dream/
├── src/
│   ├── assets/
│   │   └── styles/
│   │       └── main.css          # 全局样式 + TailwindCSS主题
│   ├── components/
│   │   ├── common/
│   │   │   ├── ParticleBackground.vue  # 粒子背景
│   │   │   └── GlassCard.vue          # 毛玻璃卡片
│   │   └── layout/
│   │       ├── NavBar.vue        # 顶部导航栏
│   │       └── FooterBar.vue     # 底部页脚
│   ├── composables/              # 组合式函数
│   ├── router/
│   │   └── index.ts              # 路由配置
│   ├── stores/
│   │   └── app.ts               # Pinia 状态管理
│   ├── types/
│   │   └── index.ts             # TypeScript 类型定义
│   ├── utils/                   # 工具函数
│   └── views/
│       ├── home/                # 落地页
│       ├── dashboard/            # 用户工作台
│       ├── ai/                  # AI 生成（图片/视频/历史）
│       ├── recharge/            # 积分充值
│       ├── agency/              # 代理中心
│       └── login/               # 登录注册
├── index.html
├── vite.config.ts
└── tailwind.config.js           # TailwindCSS v4 配置（内置）
```

## 设计特色

- **深邃宇宙美学** — `#030014` 深紫黑背景，营造沉浸式创作氛围
- **霓虹渐变品牌色** — 紫色 `#7c3aed` → 青色 `#06b6d4` → 粉色 `#ec4899` 三色渐变
- **毛玻璃质感** — 卡片背景 `rgba(255,255,255,0.03)` + `backdrop-filter: blur(20px)`
- **动态粒子网络** — Canvas 粒子系统 + 连线效果，底部漂浮渐变光斑
- **精致动画** — 渐显上浮、浮动呼吸、光晕脉冲等 10+ 套 CSS 动画

## 快速开始

```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 生产构建
npm run build

# 预览构建产物
npm run preview

# 类型检查
npx vue-tsc --noEmit
```

## 页面路由

| 路径 | 页面 | 说明 |
|------|------|------|
| `/` | 落地页 | Hero + 功能展示 + 代理 + 定价 |
| `/login` | 登录/注册 | 手机号 + 验证码 |
| `/dashboard` | 工作台 | 积分统计 + 快捷入口 + 近期作品 |
| `/ai/image` | AI 图片生成 | Prompt + 风格 + 尺寸 + 生成结果 |
| `/ai/video` | AI 视频生成 | Prompt + 时长 + 视频预览 |
| `/ai/history` | 作品历史 | 图片/视频分类列表 |
| `/recharge` | 积分充值 | 套餐选择 + 支付 |
| `/agency` | 代理中心 | 分佣记录 + 积分分配 + 关系链 |

## TailwindCSS v4 主题变量

在 `src/assets/styles/main.css` 中定义：

```css
/* 品牌色系 */
--color-dream-primary: #7c3aed
--color-dream-accent: #06b6d4
--color-dream-pink: #ec4899

/* 背景层次 */
--color-bg-base: #030014
--color-bg-surface: #0f0a1f

/* 组件类 */
.glass-card  /* 毛玻璃卡片 */
.btn-primary /* 渐变主按钮 */
.input-dream /* 深色输入框 */
.text-gradient /* 渐变文字 */
```

## API 对接说明

当前 UI 为纯前端展示，所有 API 对接部分标注了 `// TODO:` 注释，主要涉及：

- 登录/注册（`/login`）
- 积分余额查询（Pinia store）
- AI 图片/视频生成调用
- 充值支付流程
- 代理分佣记录查询
- 积分分配操作

后端接口开发完成后，只需替换 `TODO` 部分即可。

## 注意事项

- 推荐使用 Node.js 18+ 环境
- 生产构建使用 `npm run build`，所有资源自动 gzip 优化
- TailwindCSS 使用 v4 内置 `@tailwindcss/vite` 插件，无需独立配置文件
