'use client';

import { useState } from 'react';

export default function VideoPage() {
  const [activeTool, setActiveTool] = useState('sora');
  const [prompt, setPrompt] = useState('');
  const [generating, setGenerating] = useState(false);
  const [duration, setDuration] = useState('15');

  const tools = [
    { id: 'sora', name: '索拉', desc: 'AI 视频生成', icon: '🎬', cost: 50 },
    { id: 'mix', name: 'AI超级混剪', desc: '智能剪辑', icon: '✂️', cost: 20 },
    { id: 'avatar', name: '燃动数字人', desc: '数字人视频', icon: '👤', cost: 30 },
    { id: 'template', name: '模板视频', desc: '快速制作', icon: '📋', cost: 10 },
  ];

  const durations = ['5秒', '10秒', '15秒', '30秒', '60秒'];
  const resolutions = ['720P', '1080P', '4K'];

  const templates = [
    { name: '产品展示', category: '商业', icon: '📦' },
    { name: '品牌故事', category: '商业', icon: '🏆' },
    { name: '教学演示', category: '教育', icon: '🎓' },
    { name: '创意短片', category: '创意', icon: '💡' },
  ];

  const recentVideos = [
    { id: 1, title: '梦幻森林漫游', duration: '00:15', status: '已完成', time: '10分钟前' },
    { id: 2, title: '城市夜景延时', duration: '00:30', status: '生成中', time: '5分钟前' },
    { id: 3, title: '产品介绍动画', duration: '00:45', status: '已完成', time: '1小时前' },
  ];

  const handleGenerate = async () => {
    if (!prompt.trim()) return;
    setGenerating(true);
    await new Promise((resolve) => setTimeout(resolve, 3000));
    setGenerating(false);
  };

  return (
    <div className="min-h-screen flex bg-[#0a0a0c]">
      {/* Sidebar */}
      <aside className="w-64 bg-[#121216] border-r border-white/[0.08] flex flex-col">
        <div className="h-16 flex items-center px-6 border-b border-white/[0.08]">
          <h1 className="text-xl font-semibold bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] bg-clip-text text-transparent">梦恋</h1>
        </div>
        <nav className="flex-1 p-4">
          <ul className="space-y-2">
            <li><a href="/" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">💡</span><span className="text-sm text-[#9898a0]">灵感</span></a></li>
            <li><a href="/director" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">🎬</span><span className="text-sm text-[#9898a0]">编导</span></a></li>
            <li><a href="/image" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">🎨</span><span className="text-sm text-[#9898a0]">做图</span></a></li>
            <li><a href="/audio" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">🎵</span><span className="text-sm text-[#9898a0]">音频</span></a></li>
            <li><a href="/video" className="flex items-center gap-3 px-4 py-3 rounded-xl bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 transition-all"><span className="text-lg">📹</span><span className="text-sm text-white">视频</span></a></li>
            <li><a href="/assets" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">📦</span><span className="text-sm text-[#9898a0]">资产</span></a></li>
          </ul>
        </nav>
        <div className="p-4 border-t border-white/[0.08]">
          <div className="bg-gradient-to-r from-[#5b7ce8]/10 to-[#c044d8]/10 rounded-xl p-4 border border-[#5b7ce8]/20">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-[#9898a0]">剩余积分</span>
              <span className="text-lg font-semibold text-white">9,999</span>
            </div>
            <button className="w-full h-10 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-white text-sm font-medium hover:opacity-90 transition-opacity">充值积分</button>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col">
        <header className="h-16 bg-[#121216] border-b border-white/[0.08] flex items-center justify-between px-6">
          <h2 className="text-lg font-medium text-white">视频工作台</h2>
          <div className="flex items-center gap-4">
            <button className="px-4 py-2 bg-white/[0.05] border border-white/[0.08] rounded-lg text-sm text-[#9898a0] hover:border-[#5b7ce8] transition-all">导入素材</button>
            <button className="px-4 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white font-medium">新建项目</button>
          </div>
        </header>

        <div className="flex-1 p-6 flex gap-6">
          {/* Tools & Settings */}
          <div className="w-80 flex flex-col gap-4">
            {/* Tools */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">AI 视频工具</h3>
              <div className="space-y-2">
                {tools.map((tool) => (
                  <button
                    key={tool.id}
                    onClick={() => setActiveTool(tool.id)}
                    className={`w-full flex items-center gap-3 p-3 rounded-lg transition-all ${
                      activeTool === tool.id
                        ? 'bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30'
                        : 'bg-white/[0.03] hover:bg-white/[0.05]'
                    }`}
                  >
                    <span className="text-2xl">{tool.icon}</span>
                    <div className="flex-1">
                      <span className="text-sm text-white">{tool.name}</span>
                      <span className="text-xs text-[#5f5f68] ml-2">{tool.desc}</span>
                    </div>
                    <span className="text-xs text-[#c044d8]">-{tool.cost}积分</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Prompt */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">视频描述</h3>
              <textarea
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
                placeholder="描述你想生成的视频内容..."
                className="w-full h-32 bg-white/[0.03] border border-white/[0.08] rounded-lg p-3 text-white text-sm resize-none outline-none focus:border-[#5b7ce8] transition-colors"
              />
              <div className="flex gap-2 mt-3">
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">✨ AI 优化</button>
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">📝 参考脚本</button>
              </div>
            </div>

            {/* Settings */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">视频设置</h3>
              <div className="space-y-4">
                <div>
                  <label className="text-xs text-[#5f5f68] mb-2 block">时长</label>
                  <div className="flex gap-2">
                    {durations.map((d) => (
                      <button key={d} className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.05] hover:text-white transition-all">{d}</button>
                    ))}
                  </div>
                </div>
                <div>
                  <label className="text-xs text-[#5f5f68] mb-2 block">分辨率</label>
                  <div className="flex gap-2">
                    {resolutions.map((r) => (
                      <button key={r} className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.05] hover:text-white transition-all">{r}</button>
                    ))}
                  </div>
                </div>
              </div>
            </div>

            {/* Templates */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">快速模板</h3>
              <div className="grid grid-cols-2 gap-2">
                {templates.map((t) => (
                  <button key={t.name} className="flex flex-col items-center gap-2 p-3 bg-white/[0.03] rounded-lg hover:bg-white/[0.05] transition-all">
                    <span className="text-2xl">{t.icon}</span>
                    <span className="text-xs text-[#9898a0]">{t.name}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* Generate Button */}
            <button
              onClick={handleGenerate}
              disabled={generating || !prompt.trim()}
              className="w-full h-14 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-xl text-white text-base font-semibold hover:-translate-y-0.5 hover:shadow-[0_12px_40px_rgba(91,124,232,0.4)] disabled:opacity-60 disabled:cursor-not-allowed disabled:translate-y-0 transition-all"
            >
              {generating ? (
                <span className="inline-flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  生成中...
                </span>
              ) : (
                '开始生成 (-50积分)'
              )}
            </button>
          </div>

          {/* Preview */}
          <div className="flex-1 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            <div className="p-4 border-b border-white/[0.08] flex items-center justify-between">
              <span className="text-sm text-[#9898a0]">预览</span>
              <div className="flex gap-2">
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">下载</button>
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">分享</button>
              </div>
            </div>
            <div className="flex-1 flex items-center justify-center p-4">
              <div className="w-full max-w-2xl">
                <div className="aspect-video bg-gradient-to-br from-[#5b7ce8]/30 to-[#c044d8]/30 rounded-xl flex items-center justify-center mb-4">
                  <div className="text-center">
                    <svg className="w-16 h-16 text-white/30 mb-2" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                      <polygon points="5 3 19 12 5 21 5 3" />
                    </svg>
                    <p className="text-sm text-[#5f5f68]">输入描述后点击生成</p>
                  </div>
                </div>
                <div className="flex items-center justify-center gap-4">
                  <button className="p-2 bg-white/[0.05] rounded-lg hover:bg-white/[0.08] transition-all">
                    <svg className="w-5 h-5 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <polygon points="19 20 9 12 19 4 19 20" />
                      <line x1="5" y1="19" x2="5" y2="5" />
                    </svg>
                  </button>
                  <div className="flex-1 h-2 bg-white/[0.05] rounded-full overflow-hidden">
                    <div className="w-0 h-full bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-full" />
                  </div>
                  <span className="text-xs text-[#5f5f68]">00:00 / 00:15</span>
                </div>
              </div>
            </div>
          </div>

          {/* Recent Videos */}
          <div className="w-72 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            <div className="p-4 border-b border-white/[0.08]">
              <span className="text-sm text-[#9898a0]">最近生成</span>
            </div>
            <div className="flex-1 p-4 space-y-3 overflow-auto">
              {recentVideos.map((video) => (
                <div key={video.id} className="p-3 bg-white/[0.03] rounded-lg hover:bg-white/[0.05] transition-all cursor-pointer">
                  <div className="flex items-center gap-3 mb-2">
                    <div className="w-16 h-10 bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 rounded-lg flex items-center justify-center">
                      <svg className="w-6 h-6 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                        <polygon points="5 3 19 12 5 21 5 3" />
                      </svg>
                    </div>
                    <div className="flex-1">
                      <p className="text-sm text-white truncate">{video.title}</p>
                      <p className="text-xs text-[#5f5f68]">{video.duration}</p>
                    </div>
                  </div>
                  <div className="flex items-center justify-between">
                    <span className={`text-xs px-2 py-1 rounded ${video.status === '已完成' ? 'bg-[#5b7ce8]/20 text-[#5b7ce8]' : 'bg-[#c044d8]/20 text-[#c044d8]'}`}>{video.status}</span>
                    <span className="text-xs text-[#5f5f68]">{video.time}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}