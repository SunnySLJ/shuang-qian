'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { login, sendSmsCode } from '@/lib/api';

export default function LoginPage() {
  const router = useRouter();
  const [phone, setPhone] = useState('');
  const [password, setPassword] = useState('');
  const [captcha, setCaptcha] = useState('');
  const [countdown, setCountdown] = useState(0);
  const [loading, setLoading] = useState(false);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [loginType, setLoginType] = useState<'password' | 'sms'>('password');

  const handleSendCaptcha = async () => {
    if (!phone) {
      setErrors({ phone: '请输入手机号' });
      return;
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      setErrors({ phone: '手机号格式不正确' });
      return;
    }
    setErrors({});

    try {
      // scene: 1=登录场景
      await sendSmsCode({ mobile: phone, scene: 1 });
      setCountdown(60);
      const timer = setInterval(() => {
        setCountdown((prev) => {
          if (prev <= 1) {
            clearInterval(timer);
            return 0;
          }
          return prev - 1;
        });
      }, 1000);
    } catch (err) {
      setErrors({ captcha: err instanceof Error ? err.message : '发送验证码失败' });
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const newErrors: Record<string, string> = {};

    if (!phone) newErrors.phone = '请输入手机号';
    else if (!/^1[3-9]\d{9}$/.test(phone)) newErrors.phone = '手机号格式不正确';

    if (loginType === 'password') {
      if (!password) newErrors.password = '请输入密码';
      else if (password.length < 4) newErrors.password = '密码至少4位';
    } else {
      if (!captcha) newErrors.captcha = '请输入验证码';
    }

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors);
      return;
    }

    setLoading(true);
    try {
      if (loginType === 'password') {
        await login({ mobile: phone, password });
      } else {
        // 短信登录
        const { smsLogin } = await import('@/lib/api/auth');
        await smsLogin({ mobile: phone, code: captcha });
      }
      router.push('/');
    } catch (err) {
      setErrors({ submit: err instanceof Error ? err.message : '登录失败，请重试' });
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex">
      {/* Left Brand Section */}
      <div className="hidden lg:flex lg:w-1/2 relative overflow-hidden">
        <div className="absolute inset-0 bg-gradient-to-br from-[#0d0d12] via-[#0a0a0c] to-[#0f0a14]" />
        <div className="absolute inset-0">
          <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-[#5b7ce8] opacity-20 rounded-full blur-[100px]" />
          <div className="absolute bottom-1/4 right-1/4 w-80 h-80 bg-[#c044d8] opacity-15 rounded-full blur-[80px]" />
        </div>
        <div className="absolute inset-0 bg-[url('data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNjAiIGhlaWdodD0iNjAiIHhtbG5zPSJodHRwOi8vd3d3LnczLm9yZy8yMDAwL3N2ZyI+PGRlZnM+PHBhdHRlcm4gaWQ9ImdyaWQiIHdpZHRoPSI2MCIgaGVpZ2h0PSI2MCIgcGF0dGVyblVuaXRzPSJ1c2VyU3BhY2VPblVzZSI+PHBhdGggZD0iTSAxMCAwIEwgMCAwIDAgMTAiIGZpbGw9Im5vbmUiIHN0cm9rZT0icmdiYSgyNTUsMjU1LDI1NSwwLjAyKSIgc3Ryb2tlLXdpZHRoPSIxIi8+PC9wYXR0ZXJuPjwvZGVmcz48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSJ1cmwoI2dyaWQpIi8+PC9zdmc+')] opacity-50" />

        <div className="relative z-10 flex flex-col items-center justify-center w-full p-12 text-center">
          <div className="w-24 h-24 bg-gradient-to-br from-[#5b7ce8] to-[#c044d8] rounded-3xl flex items-center justify-center mb-8 shadow-[0_20px_60px_rgba(91,124,232,0.3)] animate-[float_6s_ease-in-out_infinite]">
            <svg className="w-12 h-12 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
              <path d="M12 2L2 7l10 5 10-5-10-5z" />
              <path d="M2 17l10 5 10-5" />
              <path d="M2 12l10 5 10-5" />
            </svg>
          </div>
          <h1 className="text-5xl font-bold bg-gradient-to-r from-[#5b7ce8] via-[#a854e8] to-[#c044d8] bg-clip-text text-transparent mb-4 tracking-wider">
            梦恋
          </h1>
          <p className="text-lg text-[#9898a0] mb-12 leading-relaxed">
            AI 视频创作平台<br />让创意触手可及
          </p>
          <div className="flex gap-4 flex-wrap justify-center">
            {['AI 智能生成', '快速创作', '安全可靠'].map((feature) => (
              <div key={feature} className="flex items-center gap-2 px-5 py-3 bg-white/[0.03] border border-white/[0.08] rounded-xl text-sm text-[#9898a0]">
                <svg className="w-4 h-4 text-[#5b7ce8]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
                  <polyline points="22 4 12 14.01 9 11.01" />
                </svg>
                {feature}
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Right Login Section */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 bg-[#121216] relative">
        <div className="absolute left-0 top-0 bottom-0 w-px bg-gradient-to-b from-transparent via-white/[0.08] to-transparent" />
        <div className="absolute top-1/4 right-1/4 w-64 h-64 bg-[#5b7ce8] opacity-10 rounded-full blur-[60px]" />
        <div className="absolute bottom-1/4 left-1/4 w-48 h-48 bg-[#c044d8] opacity-10 rounded-full blur-[50px]" />

        <div className="w-full max-w-md relative z-10">
          <div className="text-center mb-10">
            <h2 className="text-3xl font-semibold text-white mb-2">欢迎回来</h2>
            <p className="text-[#5f5f68]">登录您的账户，开启创作之旅</p>
          </div>

          {/* Login Type Tabs */}
          <div className="flex gap-2 mb-6">
            <button
              onClick={() => setLoginType('password')}
              className={`flex-1 py-2 rounded-lg text-sm transition-all ${
                loginType === 'password'
                  ? 'bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 text-white'
                  : 'bg-white/[0.05] text-[#9898a0]'
              }`}
            >
              密码登录
            </button>
            <button
              onClick={() => setLoginType('sms')}
              className={`flex-1 py-2 rounded-lg text-sm transition-all ${
                loginType === 'sms'
                  ? 'bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 text-white'
                  : 'bg-white/[0.05] text-[#9898a0]'
              }`}
            >
              验证码登录
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {/* Phone */}
            <div>
              <label className="block text-sm font-medium text-[#9898a0] mb-2">手机号</label>
              <div className="relative">
                <input
                  type="tel"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                  placeholder="请输入手机号"
                  maxLength={11}
                  className="w-full h-14 pl-14 pr-4 bg-white/[0.05] border border-white/[0.08] rounded-xl text-white text-base outline-none transition-all focus:border-[#5b7ce8] focus:bg-white/[0.08] focus:shadow-[0_0_0_4px_rgba(91,124,232,0.15)]"
                />
                <svg className="absolute left-5 top-1/2 -translate-y-1/2 w-5 h-5 text-[#5f5f68]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                  <rect x="5" y="2" width="14" height="20" rx="2" ry="2" />
                  <line x1="12" y1="18" x2="12.01" y2="18" />
                </svg>
              </div>
              {errors.phone && <p className="text-red-400 text-sm mt-2">{errors.phone}</p>}
            </div>

            {/* Password (only for password login) */}
            {loginType === 'password' && (
              <div>
                <label className="block text-sm font-medium text-[#9898a0] mb-2">密码</label>
                <div className="relative">
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="请输入密码"
                    className="w-full h-14 pl-14 pr-4 bg-white/[0.05] border border-white/[0.08] rounded-xl text-white text-base outline-none transition-all focus:border-[#5b7ce8] focus:bg-white/[0.08] focus:shadow-[0_0_0_4px_rgba(91,124,232,0.15)]"
                  />
                  <svg className="absolute left-5 top-1/2 -translate-y-1/2 w-5 h-5 text-[#5f5f68]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2" />
                    <path d="M7 11V7a5 5 0 0 1 10 0v4" />
                  </svg>
                </div>
                {errors.password && <p className="text-red-400 text-sm mt-2">{errors.password}</p>}
              </div>
            )}

            {/* Captcha (only for sms login) */}
            {loginType === 'sms' && (
              <div>
                <label className="block text-sm font-medium text-[#9898a0] mb-2">验证码</label>
                <div className="flex gap-3">
                  <div className="relative flex-1">
                    <input
                      type="text"
                      value={captcha}
                      onChange={(e) => setCaptcha(e.target.value)}
                      placeholder="请输入验证码"
                      maxLength={6}
                      className="w-full h-14 pl-14 pr-4 bg-white/[0.05] border border-white/[0.08] rounded-xl text-white text-base outline-none transition-all focus:border-[#5b7ce8] focus:bg-white/[0.08] focus:shadow-[0_0_0_4px_rgba(91,124,232,0.15)]"
                    />
                    <svg className="absolute left-5 top-1/2 -translate-y-1/2 w-5 h-5 text-[#5f5f68]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14" />
                      <polyline points="22 4 12 14.01 9 11.01" />
                    </svg>
                  </div>
                  <button
                    type="button"
                    onClick={handleSendCaptcha}
                    disabled={countdown > 0}
                    className="h-14 px-5 bg-[#1a1a20] border border-white/[0.08] rounded-xl text-sm text-[#9898a0] transition-all hover:border-[#5b7ce8] hover:text-[#5b7ce8] disabled:opacity-50 disabled:cursor-not-allowed whitespace-nowrap"
                  >
                    {countdown > 0 ? `${countdown}s` : '获取验证码'}
                  </button>
                </div>
                {errors.captcha && <p className="text-red-400 text-sm mt-2">{errors.captcha}</p>}
              </div>
            )}

            {/* Submit Error */}
            {errors.submit && (
              <div className="p-3 bg-red-500/10 border border-red-500/30 rounded-lg text-red-400 text-sm">
                {errors.submit}
              </div>
            )}

            {/* Options */}
            <div className="flex items-center justify-between">
              <label className="flex items-center gap-2 cursor-pointer">
                <input type="checkbox" className="w-4 h-4 rounded border-white/[0.08] bg-transparent checked:bg-gradient-to-r checked:from-[#5b7ce8] checked:to-[#c044d8]" />
                <span className="text-sm text-[#9898a0]">记住我</span>
              </label>
              <a href="#" className="text-sm text-[#5b7ce8] hover:text-[#c044d8] transition-colors">忘记密码?</a>
            </div>

            {/* Submit */}
            <button
              type="submit"
              disabled={loading}
              className="w-full h-14 bg-gradient-to-r from-[#5b7ce8] via-[#a854e8] to-[#c044d8] rounded-xl text-white text-base font-semibold transition-all hover:-translate-y-0.5 hover:shadow-[0_12px_40px_rgba(91,124,232,0.4)] disabled:opacity-60 disabled:cursor-not-allowed disabled:translate-y-0"
            >
              {loading ? (
                <span className="inline-block w-5 h-5 border-2 border-white/30 border-t-white rounded-full animate-spin" />
              ) : (
                '登 录'
              )}
            </button>
          </form>

          <p className="text-center mt-8 text-sm text-[#5f5f68]">
            还没有账户?{' '}
            <Link href="/signup" className="text-[#5b7ce8] hover:text-[#c044d8] font-medium transition-colors">
              立即注册
            </Link>
          </p>
        </div>
      </div>

      <style jsx>{`
        @keyframes float {
          0%, 100% { transform: translateY(0); }
          50% { transform: translateY(-10px); }
        }
      `}</style>
    </div>
  );
}