<!-- 追梦Dream · 星空图标库 v2 -->
<!-- 所有图标采用：轨道光环 + 星云渐变 + 脉冲核心 三层结构 -->

<!-- 🏠 工作台/首页 -->
<template name="icon-home">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="10" stroke="url(#g1)" stroke-width="1.5"/>
    <circle cx="12" cy="12" r="6" stroke="url(#g1)" stroke-width="1" stroke-dasharray="2 2"/>
    <circle cx="12" cy="12" r="2.5" fill="url(#g1)"/>
    <circle cx="12" cy="12" r="1" fill="white"/>
    <circle cx="12" cy="2" r="1" fill="url(#g1)" opacity="0.6"/>
    <circle cx="22" cy="12" r="0.8" fill="url(#g1)" opacity="0.6"/>
    <circle cx="12" cy="22" r="0.8" fill="url(#g1)" opacity="0.6"/>
    <circle cx="2" cy="12" r="0.8" fill="url(#g1)" opacity="0.6"/>
    <defs>
      <linearGradient id="g1" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#a78bfa"/>
        <stop offset="1" stop-color="#06b6d4"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🖼️ 图片生成 -->
<template name="icon-image">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 外圈星轨 -->
    <circle cx="12" cy="12" r="10" stroke="url(#gi)" stroke-width="1.5" stroke-dasharray="3 3"/>
    <!-- 星云内框 -->
    <rect x="5" y="5" width="14" height="14" rx="2" stroke="url(#gi)" stroke-width="1.2" fill="none"/>
    <!-- 中心星 -->
    <circle cx="9" cy="9" r="1.5" fill="url(#gi)" opacity="0.8"/>
    <!-- 闪光点 -->
    <circle cx="16" cy="7" r="0.8" fill="#06b6d4" opacity="0.7"/>
    <circle cx="17" cy="15" r="0.6" fill="#ec4899" opacity="0.6"/>
    <!-- 星座连线 -->
    <path d="M8 16 L12 11 L15 14" stroke="url(#gi)" stroke-width="1" stroke-linecap="round"/>
    <defs>
      <linearGradient id="gi" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#7c3aed"/>
        <stop offset="0.5" stop-color="#8b5cf6"/>
        <stop offset="1" stop-color="#06b6d4"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🎬 视频生成 -->
<template name="icon-video">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 星轨外框 -->
    <circle cx="12" cy="12" r="10" stroke="url(#gv)" stroke-width="1.5"/>
    <!-- 播放三角（带光晕） -->
    <circle cx="12" cy="12" r="5" fill="url(#gv)" opacity="0.15"/>
    <path d="M10 8.5 L16.5 12 L10 15.5 Z" fill="url(#gv)"/>
    <!-- 轨道点 -->
    <circle cx="5" cy="12" r="1" fill="#22d3ee" opacity="0.8"/>
    <circle cx="19" cy="12" r="1" fill="#22d3ee" opacity="0.8"/>
    <circle cx="12" cy="5" r="0.8" fill="#06b6d4" opacity="0.6"/>
    <defs>
      <linearGradient id="gv" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#06b6d4"/>
        <stop offset="1" stop-color="#22d3ee"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 💎 积分/钱包 -->
<template name="icon-star">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 外圈光环 -->
    <circle cx="12" cy="12" r="9" stroke="url(#gs)" stroke-width="1" stroke-dasharray="2 2" opacity="0.5"/>
    <!-- 主体六芒星 -->
    <path d="M12 3 L13.5 8.5 L19.5 8.5 L15 12.5 L16.5 18.5 L12 14.5 L7.5 18.5 L9 12.5 L4.5 8.5 L10.5 8.5 Z" fill="url(#gs)" opacity="0.9"/>
    <!-- 中心亮核 -->
    <circle cx="12" cy="12" r="2" fill="white" opacity="0.9"/>
    <!-- 脉冲点 -->
    <circle cx="12" cy="3" r="1" fill="#f59e0b" opacity="0.7"/>
    <circle cx="20" cy="8" r="0.8" fill="#fbbf24" opacity="0.6"/>
    <defs>
      <linearGradient id="gs" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#f59e0b"/>
        <stop offset="0.5" stop-color="#fbbf24"/>
        <stop offset="1" stop-color="#fcd34d"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 👥 代理/团队 -->
<template name="icon-users">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 轨道 -->
    <circle cx="12" cy="8" r="4" stroke="url(#gu)" stroke-width="1.5"/>
    <!-- 头部 -->
    <circle cx="12" cy="6.5" r="2" fill="url(#gu)"/>
    <!-- 身体 -->
    <path d="M8 12 Q12 10 16 12" stroke="url(#gu)" stroke-width="1.5" stroke-linecap="round"/>
    <!-- 环绕粒子 -->
    <circle cx="4" cy="6" r="2.5" stroke="url(#gu)" stroke-width="1" opacity="0.5"/>
    <circle cx="4" cy="6" r="1" fill="url(#gu)" opacity="0.3"/>
    <circle cx="20" cy="6" r="2.5" stroke="url(#gu)" stroke-width="1" opacity="0.5"/>
    <circle cx="20" cy="6" r="1" fill="url(#gu)" opacity="0.3"/>
    <!-- 底部连线 -->
    <path d="M12 12 L12 16 M8 16 L16 16" stroke="url(#gu)" stroke-width="1" opacity="0.4"/>
    <circle cx="8" cy="19" r="1.5" stroke="url(#gu)" stroke-width="1" opacity="0.3"/>
    <circle cx="16" cy="19" r="1.5" stroke="url(#gu)" stroke-width="1" opacity="0.3"/>
    <defs>
      <linearGradient id="gu" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#a78bfa"/>
        <stop offset="1" stop-color="#06b6d4"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 📜 历史 -->
<template name="icon-history">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 外轨 -->
    <circle cx="12" cy="12" r="9.5" stroke="url(#gh)" stroke-width="1.5"/>
    <!-- 内圈 -->
    <circle cx="12" cy="12" r="5" stroke="url(#gh)" stroke-width="1" stroke-dasharray="2 1.5" opacity="0.5"/>
    <!-- 时钟指针 -->
    <path d="M12 8 L12 12 L15.5 13.5" stroke="url(#gh)" stroke-width="1.8" stroke-linecap="round"/>
    <!-- 中心 -->
    <circle cx="12" cy="12" r="1" fill="url(#gh)"/>
    <!-- 脉冲点 -->
    <circle cx="12" cy="2.5" r="1" fill="#22d3ee" opacity="0.7"/>
    <circle cx="21.5" cy="12" r="1" fill="#a78bfa" opacity="0.7"/>
    <circle cx="12" cy="21.5" r="1" fill="#ec4899" opacity="0.6"/>
    <circle cx="2.5" cy="12" r="1" fill="#06b6d4" opacity="0.7"/>
    <defs>
      <linearGradient id="gh" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#6366f1"/>
        <stop offset="1" stop-color="#8b5cf6"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 💰 充值 -->
<template name="icon-recharge">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 外圈 -->
    <circle cx="12" cy="12" r="9.5" stroke="url(#gr)" stroke-width="1.5"/>
    <!-- 内部方孔 -->
    <rect x="7" y="7" width="10" height="10" rx="2" stroke="url(#gr)" stroke-width="1.2"/>
    <!-- ¥符号 -->
    <path d="M12 9 L12 15 M10 10.5 Q10 9.5 12 9.5 Q14 9.5 14 11 Q14 12 12 12.5 Q10 13 10 14 Q10 15 12 15 Q14 15 14 14" stroke="url(#gr)" stroke-width="1.5" stroke-linecap="round"/>
    <!-- 光芒 -->
    <circle cx="12" cy="12" r="11.5" stroke="url(#gr)" stroke-width="0.5" opacity="0.3"/>
    <defs>
      <linearGradient id="gr" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#10b981"/>
        <stop offset="1" stop-color="#34d399"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- ⚙️ 设置 -->
<template name="icon-settings">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 环形轨道 -->
    <circle cx="12" cy="12" r="8" stroke="url(#gset)" stroke-width="1.5"/>
    <!-- 齿轮齿（8个点） -->
    <path d="M12 4 L13 4.5 L13.5 3.5 L14.5 3.5 L15 4.5 L16 4 L16 5 L17 5.5 L17 6.5 L18 7 L17 8 L17 9 L16 9.5 L16 10 L15 10 L14.5 11 L13.5 11 L13 10 L12 10 L11 10 L10.5 11 L9.5 11 L9 10 L8 10 L8 9 L7 8 L7 7 L8 6.5 L8 5.5 L9 5 L9 4 L10 4 L10.5 3.5 L11.5 3.5 L12 4.5 Z" fill="url(#gset)" opacity="0.9"/>
    <!-- 中心 -->
    <circle cx="12" cy="12" r="3" stroke="url(#gset)" stroke-width="1.2" fill="none"/>
    <circle cx="12" cy="12" r="1" fill="url(#gset)"/>
    <!-- 脉冲 -->
    <circle cx="12" cy="3" r="0.8" fill="#06b6d4" opacity="0.6"/>
    <circle cx="21" cy="12" r="0.8" fill="#7c3aed" opacity="0.6"/>
    <circle cx="3" cy="12" r="0.8" fill="#ec4899" opacity="0.6"/>
    <defs>
      <linearGradient id="gset" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#64748b"/>
        <stop offset="1" stop-color="#94a3b8"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- ➕ 更多/加号 -->
<template name="icon-plus">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9" stroke="url(#gp)" stroke-width="1.5"/>
    <path d="M12 8 L12 16 M8 12 L16 12" stroke="url(#gp)" stroke-width="2" stroke-linecap="round"/>
    <circle cx="12" cy="12" r="10" stroke="url(#gp)" stroke-width="0.5" opacity="0.3"/>
    <defs>
      <linearGradient id="gp" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#7c3aed"/>
        <stop offset="1" stop-color="#06b6d4"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🚀 启动/开始 -->
<template name="icon-rocket">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <!-- 主体 -->
    <path d="M12 3 Q16 6 16 12 Q16 18 12 21 Q8 18 8 12 Q8 6 12 3Z" stroke="url(#grk)" stroke-width="1.5" fill="none"/>
    <!-- 火焰 -->
    <path d="M9 18 Q8 21 10 23 M12 19 Q12 22 12 24 M15 18 Q16 21 14 23" stroke="url(#grk)" stroke-width="1.2" stroke-linecap="round" opacity="0.7"/>
    <!-- 窗口 -->
    <circle cx="12" cy="10" r="2" stroke="url(#grk)" stroke-width="1" fill="none"/>
    <circle cx="12" cy="10" r="1" fill="url(#grk)" opacity="0.5"/>
    <!-- 星轨 -->
    <circle cx="12" cy="12" r="10" stroke="url(#grk)" stroke-width="0.5" stroke-dasharray="1 3" opacity="0.3"/>
    <defs>
      <linearGradient id="grk" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#f59e0b"/>
        <stop offset="0.5" stop-color="#fbbf24"/>
        <stop offset="1" stop-color="#fcd34d"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🔥 热门 -->
<template name="icon-fire">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M12 3 Q16 7 15 12 Q14 15 16 18 Q17 21 14 21 Q12 19 12 16 Q10 19 8 21 Q5 21 6 18 Q8 15 7 12 Q6 7 12 3Z" fill="url(#gfi)" opacity="0.15"/>
    <path d="M12 5 Q15 8 14 12 Q13 15 15 17 Q16 19 13.5 19.5 Q12 18 12 16 Q10 18 8.5 19.5 Q6 19 7 17 Q9 15 8 12 Q7 8 12 5Z" stroke="url(#gfi)" stroke-width="1.2" fill="none"/>
    <circle cx="12" cy="13" r="1.5" fill="url(#gfi)"/>
    <circle cx="12" cy="13" r="3" stroke="url(#gfi)" stroke-width="0.5" opacity="0.3"/>
    <defs>
      <linearGradient id="gfi" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#f97316"/>
        <stop offset="1" stop-color="#ec4899"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- ⭐ 精选 -->
<template name="icon-sparkle">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M12 2 L13.5 9 L20 9 L14.5 13.5 L16.5 21 L12 16 L7.5 21 L9.5 13.5 L4 9 L10.5 9 Z" fill="url(#gsp)" opacity="0.2"/>
    <path d="M12 3 L13.2 8.8 L19 9 L14.5 13 L16.2 19.5 L12 15.5 L7.8 19.5 L9.5 13 L5 9 L10.8 8.8 Z" stroke="url(#gsp)" stroke-width="1.2" fill="none"/>
    <circle cx="12" cy="11.5" r="1.5" fill="url(#gsp)"/>
    <circle cx="12" cy="11.5" r="3" stroke="url(#gsp)" stroke-width="0.5" opacity="0.4"/>
    <defs>
      <linearGradient id="gsp" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#f59e0b"/>
        <stop offset="1" stop-color="#fbbf24"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 💎 钻石/专业 -->
<template name="icon-diamond">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M12 3 L20 9 L12 21 L4 9 Z" stroke="url(#gdm)" stroke-width="1.5" fill="none"/>
    <path d="M4 9 L12 12 L20 9" stroke="url(#gdm)" stroke-width="1"/>
    <path d="M12 3 L12 12 L20 9" stroke="url(#gdm)" stroke-width="1" opacity="0.5"/>
    <path d="M12 3 L12 12 L4 9" stroke="url(#gdm)" stroke-width="1" opacity="0.5"/>
    <path d="M12 12 L12 21 L20 9" stroke="url(#gdm)" stroke-width="1" opacity="0.5"/>
    <path d="M12 12 L12 21 L4 9" stroke="url(#gdm)" stroke-width="1" opacity="0.5"/>
    <circle cx="12" cy="9" r="1.5" fill="url(#gdm)" opacity="0.6"/>
    <circle cx="12" cy="3" r="1" fill="#06b6d4" opacity="0.7"/>
    <circle cx="4" cy="9" r="0.8" fill="#7c3aed" opacity="0.6"/>
    <circle cx="20" cy="9" r="0.8" fill="#ec4899" opacity="0.6"/>
    <defs>
      <linearGradient id="gdm" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#6366f1"/>
        <stop offset="0.5" stop-color="#8b5cf6"/>
        <stop offset="1" stop-color="#a78bfa"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🧭 导航/指南针 -->
<template name="icon-compass">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9.5" stroke="url(#gcp)" stroke-width="1.5"/>
    <circle cx="12" cy="12" r="5" stroke="url(#gcp)" stroke-width="0.8" stroke-dasharray="1.5 2" opacity="0.4"/>
    <circle cx="12" cy="12" r="2" fill="url(#gcp)"/>
    <path d="M12 7 L13 11 L12 13 L11 11 Z" fill="#ec4899" opacity="0.9"/>
    <path d="M12 17 L11 13 L12 11 L13 13 Z" fill="url(#gcp)" opacity="0.6"/>
    <defs>
      <linearGradient id="gcp" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#06b6d4"/>
        <stop offset="1" stop-color="#8b5cf6"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🎯 目标/目标 -->
<template name="icon-target">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9.5" stroke="url(#gtk)" stroke-width="1.5"/>
    <circle cx="12" cy="12" r="6" stroke="url(#gtk)" stroke-width="1.2"/>
    <circle cx="12" cy="12" r="3" stroke="url(#gtk)" stroke-width="1"/>
    <circle cx="12" cy="12" r="1" fill="url(#gtk)"/>
    <!-- 射线 -->
    <line x1="12" y1="2" x2="12" y2="5" stroke="url(#gtk)" stroke-width="1.5" stroke-linecap="round" opacity="0.5"/>
    <line x1="22" y1="12" x2="19" y2="12" stroke="url(#gtk)" stroke-width="1.5" stroke-linecap="round" opacity="0.5"/>
    <line x1="12" y1="22" x2="12" y2="19" stroke="url(#gtk)" stroke-width="1.5" stroke-linecap="round" opacity="0.5"/>
    <line x1="2" y1="12" x2="5" y2="12" stroke="url(#gtk)" stroke-width="1.5" stroke-linecap="round" opacity="0.5"/>
    <defs>
      <linearGradient id="gtk" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#ef4444"/>
        <stop offset="1" stop-color="#f97316"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🛡️ 安全/盾牌 -->
<template name="icon-shield">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <path d="M12 3 L19 6 L19 12 Q19 18 12 21 Q5 18 5 12 L5 6 Z" stroke="url(#gsh)" stroke-width="1.5" fill="none"/>
    <path d="M12 7 L15.5 9 L15.5 12.5 Q15.5 15 12 16.5 Q8.5 15 8.5 12.5 L8.5 9 Z" fill="url(#gsh)" opacity="0.15"/>
    <path d="M12 7 L15.5 9 L15.5 12.5 Q15.5 15 12 16.5 Q8.5 15 8.5 12.5 L8.5 9 Z" stroke="url(#gsh)" stroke-width="1.2" fill="none"/>
    <polyline points="9 12 11 14 15 10" stroke="url(#gsh)" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
    <circle cx="12" cy="3" r="0.8" fill="#10b981" opacity="0.7"/>
    <defs>
      <linearGradient id="gsh" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#10b981"/>
        <stop offset="1" stop-color="#34d399"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 📊 统计 -->
<template name="icon-chart">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9.5" stroke="url(#gch)" stroke-width="1.5"/>
    <!-- 柱状 -->
    <rect x="7" y="13" width="2.5" height="5" rx="1" fill="url(#gch)" opacity="0.5"/>
    <rect x="10.75" y="10" width="2.5" height="8" rx="1" fill="url(#gch)" opacity="0.7"/>
    <rect x="14.5" y="7" width="2.5" height="11" rx="1" fill="url(#gch)" opacity="0.9"/>
    <!-- 趋势线 -->
    <path d="M7 16 Q10 14 13 10 Q16 6 18 5" stroke="url(#gch)" stroke-width="1.5" stroke-linecap="round"/>
    <!-- 点 -->
    <circle cx="7" cy="16" r="1" fill="url(#gch)"/>
    <circle cx="18" cy="5" r="1.5" fill="url(#gch)"/>
    <defs>
      <linearGradient id="gch" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#7c3aed"/>
        <stop offset="1" stop-color="#06b6d4"/>
      </linearGradient>
    </defs>
  </svg>
</template>

<!-- 🎨 创作/画笔 -->
<template name="icon-create">
  <svg viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
    <circle cx="12" cy="12" r="9.5" stroke="url(#gcr)" stroke-width="1.5"/>
    <!-- 画笔 -->
    <path d="M16 5 Q18 7 16.5 9.5 L14 12 L12 10 L14 8 Q14 7 16 5Z" stroke="url(#gcr)" stroke-width="1.2" fill="none"/>
    <path d="M14 12 L7 19 Q6 20 7 21 L9 22 Q10 22 10.5 21 L17 14.5 Z" stroke="url(#gcr)" stroke-width="1.5" fill="none"/>
    <!-- 光晕点 -->
    <circle cx="7" cy="7" r="1" fill="#ec4899" opacity="0.6"/>
    <circle cx="18" cy="16" r="0.8" fill="#06b6d4" opacity="0.6"/>
    <circle cx="5" cy="14" r="0.6" fill="#f59e0b" opacity="0.5"/>
    <defs>
      <linearGradient id="gcr" x1="0" y1="0" x2="24" y2="24" gradientUnits="userSpaceOnUse">
        <stop stop-color="#ec4899"/>
        <stop offset="1" stop-color="#8b5cf6"/>
      </linearGradient>
    </defs>
  </svg>
</template>
