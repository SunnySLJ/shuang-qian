// API配置
export const API_CONFIG = {
  // 使用Next.js代理，避免CORS问题
  baseURL: typeof window !== 'undefined' ? '' : 'http://127.0.0.1:48080',
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json',
  },
};

// API端点
export const API_ENDPOINTS = {
  // 认证相关
  auth: {
    login: '/app-api/member/auth/login',           // 手机+密码登录
    smsLogin: '/app-api/member/auth/sms-login',    // 手机+验证码登录
    sendSmsCode: '/app-api/member/auth/send-sms-code', // 发送验证码
    logout: '/app-api/member/auth/logout',         // 登出
    refreshToken: '/app-api/member/auth/refresh-token', // 刷新token
  },
  // 用户相关
  user: {
    profile: '/member/user/profile',       // 用户信息
    update: '/member/user/update',         // 更新用户信息
  },
  // AI相关
  ai: {
    imageGenerate: '/member/ai/image/generate', // AI图片生成
    videoGenerate: '/member/ai/video/generate', // AI视频生成
  },
};

// Token存储key
export const TOKEN_KEY = 'menglian_token';
export const REFRESH_TOKEN_KEY = 'menglian_refresh_token';
export const USER_INFO_KEY = 'menglian_user_info';