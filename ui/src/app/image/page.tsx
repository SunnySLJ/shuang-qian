'use client';

import { useState } from 'react';

export default function ImagePage() {
  const [activeTool, setActiveTool] = useState('banana');
  const [prompt, setPrompt] = useState('');
  const [generating, setGenerating] = useState(false);
  const [generatedImages, setGeneratedImages] = useState<string[]>([]);

  const tools = [
    { id: 'banana', name: '香蕉生图', desc: 'AI 图片生成', icon: '🍌' },
    { id: 'mark', name: '马克', desc: '专业图片创作', icon: '✏️' },
    { id: 'enhance', name: '图片增强', desc: '画质提升', icon: '✨' },
    { id: 'edit', name: '智能编辑', desc: 'AI 图片编辑', icon: '🎨' },
  ];

  const styles = [
    '写实', '动漫', '油画', '水彩', '素描', '3D渲染',
    '赛博朋克', '复古', '极简', '抽象', '像素', '水墨',
  ];

  const ratios = ['1:1', '4:3', '3:4', '16:9', '9:16', '自定义'];

  const history = [
    { id: 1, prompt: '梦幻森林', style: '油画', time: '2分钟前' },
    { id: 2, prompt: '城市夜景', style: '写实', time: '5分钟前' },
    { id: 3, prompt: '海边日落', style: '水彩', time: '10分钟前' },
  ];

  const handleGenerate = async () => {
    if (!prompt.trim()) return;
    setGenerating(true);
    // Simulate generation
    await new Promise((resolve) => setTimeout(resolve, 2000));
    setGeneratedImages(['img1', 'img2', 'img3', 'img4']);
    setGenerating(false);
  };

  return (
    <div className="min-h-screen flex bg-[#0a0a0c]">
      {/* Sidebar */}
      <aside className="w-64 bg-[#121216] border-r border-white/[0.08] flex flex-col">
        <div className="h-16 flex items-center px-6 border-b border-white/[0.08]">
          <h1 className="text-xl font-semibold bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] bg-clip-text text-transparent">
            梦恋
          </h1>
        </div>

        <nav className="flex-1 p-4">
          <ul className="space-y-2">
            <li><a href="/" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">💡</span><span className="text-sm text-[#9898a0]">灵感</span></a></li>
            <li><a href="/director" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">🎬</span><span className="text-sm text-[#9898a0]">编导</span></a></li>
            <li><a href="/image" className="flex items-center gap-3 px-4 py-3 rounded-xl bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 transition-all"><span className="text-lg">🎨</span><span className="text-sm text-white">做图</span></a></li>
            <li><a href="/audio" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">🎵</span><span className="text-sm text-[#9898a0]">音频</span></a></li>
            <li><a href="/video" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">📹</span><span className="text-sm text-[#9898a0]">视频</span></a></li>
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
          <h2 className="text-lg font-medium text-white">做图工作台</h2>
          <div className="flex items-center gap-4">
            <button className="px-4 py-2 bg-white/[0.05] border border-white/[0.08] rounded-lg text-sm text-[#9898a0] hover:border-[#5b7ce8] transition-all">导入图片</button>
            <button className="px-4 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white font-medium">新建创作</button>
          </div>
        </header>

        <div className="flex-1 p-6 flex gap-6">
          {/* Tools & Settings */}
          <div className="w-80 flex flex-col gap-4">
            {/* Tools */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">AI 工具</h3>
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
                  </button>
                ))}
              </div>
            </div>

            {/* Prompt Input */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">描述内容</h3>
              <textarea
                value={prompt}
                onChange={(e) => setPrompt(e.target.value)}
                placeholder="描述你想生成的图片..."
                className="w-full h-24 bg-white/[0.03] border border-white/[0.08] rounded-lg p-3 text-white text-sm resize-none outline-none focus:border-[#5b7ce8] transition-colors"
              />
              <div className="flex gap-2 mt-3">
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">
                  ✨ AI 优化
                </button>
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">
                  📝 参考模板
                </button>
              </div>
            </div>

            {/* Style */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">风格选择</h3>
              <div className="grid grid-cols-3 gap-2">
                {styles.map((style) => (
                  <button key={style} className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.05] hover:text-white transition-all">
                    {style}
                  </button>
                ))}
              </div>
            </div>

            {/* Ratio */}
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">图片比例</h3>
              <div className="flex gap-2">
                {ratios.map((ratio) => (
                  <button key={ratio} className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.05] hover:text-white transition-all">
                    {ratio}
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
                '开始生成 (-10积分)'
              )}
            </button>
          </div>

          {/* Result Area */}
          <div className="flex-1 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            <div className="p-4 border-b border-white/[0.08] flex items-center justify-between">
              <span className="text-sm text-[#9898a0]">生成结果</span>
              {generatedImages.length > 0 && (
                <div className="flex gap-2">
                  <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">全部保存</button>
                  <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">重新生成</button>
                </div>
              )}
            </div>

            <div className="flex-1 p-4 flex items-center justify-center">
              {generatedImages.length === 0 ? (
                <div className="text-center">
                  <div className="w-32 h-32 bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 rounded-2xl flex items-center justify-center mb-4 mx-auto">
                    <svg className="w-16 h-16 text-white/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                      <circle cx="8.5" cy="8.5" r="1.5" />
                      <polyline points="21 15 16 10 5 21" />
                    </svg>
                  </div>
                  <p className="text-sm text-[#5f5f68]">输入描述后点击生成</p>
                </div>
              ) : (
                <div className="grid grid-cols-2 gap-4 w-full max-w-2xl">
                  {generatedImages.map((img, idx) => (
                    <div key={idx} className="group relative aspect-square bg-gradient-to-br from-[#5b7ce8]/30 to-[#c044d8]/30 rounded-xl overflow-hidden">
                      <div className="absolute inset-0 flex items-center justify-center">
                        <svg className="w-12 h-12 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                          <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                          <circle cx="8.5" cy="8.5" r="1.5" />
                          <polyline points="21 15 16 10 5 21" />
                        </svg>
                      </div>
                      <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                        <button className="px-3 py-1.5 bg-white/[0.1] rounded-lg text-xs text-white hover:bg-white/[0.2] transition-all">下载</button>
                        <button className="px-3 py-1.5 bg-white/[0.1] rounded-lg text-xs text-white hover:bg-white/[0.2] transition-all">收藏</button>
                        <button className="px-3 py-1.5 bg-white/[0.1] rounded-lg text-xs text-white hover:bg-white/[0.2] transition-all">编辑</button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </div>
          </div>

          {/* History */}
          <div className="w-72 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            <div className="p-4 border-b border-white/[0.08]">
              <span className="text-sm text-[#9898a0]">历史记录</span>
            </div>
            <div className="flex-1 p-4 space-y-3 overflow-auto">
              {history.map((item) => (
                <div key={item.id} className="flex items-center gap-3 p-3 bg-white/[0.03] rounded-lg hover:bg-white/[0.05] transition-all cursor-pointer">
                  <div className="w-12 h-12 bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 rounded-lg flex items-center justify-center">
                    <svg className="w-6 h-6 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                      <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <p className="text-sm text-white">{item.prompt}</p>
                    <p className="text-xs text-[#5f5f68]">{item.style} · {item.time}</p>
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