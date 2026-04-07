'use client';

import { useState } from 'react';

export default function AudioPage() {
  const [activeTool, setActiveTool] = useState('voice');
  const [text, setText] = useState('');
  const [generating, setGenerating] = useState(false);

  const tools = [
    { id: 'voice', name: '威尔', desc: 'AI 配音', icon: '🎙️', cost: 5 },
    { id: 'music', name: 'AI 音乐', desc: '背景音乐生成', icon: '🎵', cost: 10 },
    { id: 'effect', name: '音效', desc: '音效素材', icon: '🔊', cost: 2 },
    { id: 'mix', name: '音频混音', desc: '多轨混音', icon: '🎛️', cost: 5 },
  ];

  const voices = [
    { id: 'male1', name: '男声-沉稳', preview: '声音预览' },
    { id: 'male2', name: '男声-活力', preview: '声音预览' },
    { id: 'female1', name: '女声-温柔', preview: '声音预览' },
    { id: 'female2', name: '女声-甜美', preview: '声音预览' },
    { id: 'child', name: '童声', preview: '声音预览' },
    { id: 'robot', name: '机械音', preview: '声音预览' },
  ];

  const musicStyles = [
    '轻快', '抒情', '摇滚', '电子', '古典', '爵士',
    '民谣', '流行', '氛围', '悬疑', '喜庆', '悲伤',
  ];

  const audioLibrary = [
    { id: 1, name: '产品介绍配音', duration: '00:30', type: 'voice', time: '10分钟前' },
    { id: 2, name: '轻快背景音乐', duration: '02:00', type: 'music', time: '30分钟前' },
    { id: 3, name: '转场音效', duration: '00:02', type: 'effect', time: '1小时前' },
  ];

  const handleGenerate = async () => {
    if (!text.trim()) return;
    setGenerating(true);
    await new Promise((resolve) => setTimeout(resolve, 2000));
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
            <li><a href="/audio" className="flex items-center gap-3 px-4 py-3 rounded-xl bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 transition-all"><span className="text-lg">🎵</span><span className="text-sm text-white">音频</span></a></li>
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
          <h2 className="text-lg font-medium text-white">音频工作台</h2>
          <div className="flex items-center gap-4">
            <button className="px-4 py-2 bg-white/[0.05] border border-white/[0.08] rounded-lg text-sm text-[#9898a0] hover:border-[#5b7ce8] transition-all">导入音频</button>
            <button className="px-4 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white font-medium">新建创作</button>
          </div>
        </header>

        <div className="flex-1 p-6 flex gap-6">
          {/* Tools */}
          <div className="w-80 flex flex-col gap-4">
            <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">AI 音频工具</h3>
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

            {/* Voice Settings */}
            {activeTool === 'voice' && (
              <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
                <h3 className="text-sm text-[#5f5f68] mb-3">配音文本</h3>
                <textarea
                  value={text}
                  onChange={(e) => setText(e.target.value)}
                  placeholder="输入需要配音的文本..."
                  className="w-full h-24 bg-white/[0.03] border border-white/[0.08] rounded-lg p-3 text-white text-sm resize-none outline-none focus:border-[#5b7ce8] transition-colors"
                />
                <div className="mt-4">
                  <h4 className="text-xs text-[#5f5f68] mb-2">选择声音</h4>
                  <div className="grid grid-cols-2 gap-2">
                    {voices.map((voice) => (
                      <button key={voice.id} className="flex flex-col items-center gap-1 p-2 bg-white/[0.03] rounded-lg hover:bg-white/[0.05] transition-all">
                        <span className="text-sm text-[#9898a0]">{voice.name}</span>
                        <span className="text-xs text-[#5f5f68]">▶ 试听</span>
                      </button>
                    ))}
                  </div>
                </div>
              </div>
            )}

            {/* Music Settings */}
            {activeTool === 'music' && (
              <div className="bg-[#121216] rounded-xl border border-white/[0.08] p-4">
                <h3 className="text-sm text-[#5f5f68] mb-3">音乐风格</h3>
                <div className="grid grid-cols-3 gap-2">
                  {musicStyles.map((style) => (
                    <button key={style} className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.05] hover:text-white transition-all">{style}</button>
                  ))}
                </div>
                <div className="mt-4 space-y-3">
                  <div>
                    <label className="text-xs text-[#5f5f68] mb-2 block">时长</label>
                    <div className="flex gap-2">
                      <button className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0]">30秒</button>
                      <button className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0]">1分钟</button>
                      <button className="px-3 py-2 bg-white/[0.03] rounded-lg text-xs text-[#9898a0]">2分钟</button>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Generate Button */}
            <button
              onClick={handleGenerate}
              disabled={generating || (activeTool === 'voice' && !text.trim())}
              className="w-full h-14 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-xl text-white text-base font-semibold hover:-translate-y-0.5 hover:shadow-[0_12px_40px_rgba(91,124,232,0.4)] disabled:opacity-60 disabled:cursor-not-allowed disabled:translate-y-0 transition-all"
            >
              {generating ? (
                <span className="inline-flex items-center gap-2">
                  <span className="w-4 h-4 border-2 border-white/30 border-t-white rounded-full animate-spin" />
                  生成中...
                </span>
              ) : (
                '开始生成 (-5积分)'
              )}
            </button>
          </div>

          {/* Preview */}
          <div className="flex-1 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            <div className="p-4 border-b border-white/[0.08] flex items-center justify-between">
              <span className="text-sm text-[#9898a0]">预览</span>
              <div className="flex gap-2">
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">下载</button>
                <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">收藏</button>
              </div>
            </div>
            <div className="flex-1 flex items-center justify-center p-8">
              <div className="w-full max-w-xl">
                <div className="h-32 bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 rounded-xl flex items-center justify-center mb-6">
                  <svg className="w-12 h-12 text-white/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                    <path d="M9 18V5l12-2v13" />
                    <circle cx="6" cy="18" r="3" />
                    <circle cx="18" cy="16" r="3" />
                  </svg>
                </div>
                {/* Waveform Visualization */}
                <div className="flex items-center justify-center gap-1 h-16 mb-6">
                  {Array.from({ length: 40 }).map((_, i) => (
                    <div key={i} className="w-1 bg-gradient-to-t from-[#5b7ce8] to-[#c044d8] rounded-full" style={{ height: `${Math.random() * 100}%` }} />
                  ))}
                </div>
                <div className="flex items-center gap-4">
                  <button className="p-3 bg-white/[0.05] rounded-lg hover:bg-white/[0.08] transition-all">
                    <svg className="w-5 h-5 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <polygon points="19 20 9 12 19 4 19 20" />
                      <line x1="5" y1="19" x2="5" y2="5" />
                    </svg>
                  </button>
                  <button className="p-4 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg">
                    <svg className="w-6 h-6 text-white" viewBox="0 0 24 24" fill="currentColor">
                      <polygon points="5 3 19 12 5 21 5 3" />
                    </svg>
                  </button>
                  <button className="p-3 bg-white/[0.05] rounded-lg hover:bg-white/[0.08] transition-all">
                    <svg className="w-5 h-5 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                      <polygon points="5 4 15 12 5 20 5 4" />
                      <line x1="19" y1="5" x2="19" y2="19" />
                    </svg>
                  </button>
                  <div className="flex-1 h-2 bg-white/[0.05] rounded-full overflow-hidden">
                    <div className="w-1/3 h-full bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-full" />
                  </div>
                  <span className="text-xs text-[#5f5f68]">00:10 / 00:30</span>
                </div>
              </div>
            </div>
          </div>

          {/* Library */}
          <div className="w-72 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            <div className="p-4 border-b border-white/[0.08]">
              <span className="text-sm text-[#9898a0]">音频库</span>
            </div>
            <div className="flex-1 p-4 space-y-3 overflow-auto">
              {audioLibrary.map((audio) => (
                <div key={audio.id} className="flex items-center gap-3 p-3 bg-white/[0.03] rounded-lg hover:bg-white/[0.05] transition-all cursor-pointer">
                  <div className="w-12 h-12 bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 rounded-lg flex items-center justify-center">
                    <svg className="w-6 h-6 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                      <path d="M9 18V5l12-2v13" />
                      <circle cx="6" cy="18" r="3" />
                      <circle cx="18" cy="16" r="3" />
                    </svg>
                  </div>
                  <div className="flex-1">
                    <p className="text-sm text-white">{audio.name}</p>
                    <p className="text-xs text-[#5f5f68]">{audio.duration} · {audio.time}</p>
                  </div>
                  <button className="p-2 hover:bg-white/[0.05] rounded-lg transition-all">
                    <svg className="w-4 h-4 text-[#9898a0]" viewBox="0 0 24 24" fill="currentColor">
                      <polygon points="5 3 19 12 5 21 5 3" />
                    </svg>
                  </button>
                </div>
              ))}
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}