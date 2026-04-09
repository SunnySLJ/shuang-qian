'use client';

import { useState } from 'react';

export default function DirectorPage() {
  const [activeTab, setActiveTab] = useState('script');
  const [scriptContent, setScriptContent] = useState('');
  const [scenes, setScenes] = useState([
    { id: 1, name: '开场', duration: '00:10', description: '产品展示开场' },
    { id: 2, name: '介绍', duration: '00:15', description: '产品功能介绍' },
    { id: 3, name: '演示', duration: '00:20', description: '使用场景演示' },
  ]);

  const tools = [
    { id: 'script', name: '脚本创作', icon: '📝' },
    { id: 'scenes', name: '场景管理', icon: '🎬' },
    { id: 'storyboard', name: '故事板', icon: '🖼️' },
    { id: 'timeline', name: '时间轴', icon: '⏱️' },
  ];

  const templates = [
    { name: '产品介绍', category: '商业', icon: '📦' },
    { name: '故事短片', category: '创意', icon: '📖' },
    { name: '教学视频', category: '教育', icon: '🎓' },
    { name: '品牌宣传', category: '商业', icon: '🏆' },
    { name: '旅行记录', category: '生活', icon: '✈️' },
    { name: '美食分享', category: '生活', icon: '🍜' },
  ];

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
            <li>
              <a href="/" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all">
                <span className="text-lg">💡</span>
                <span className="text-sm text-[#9898a0]">灵感</span>
              </a>
            </li>
            <li>
              <a href="/director" className="flex items-center gap-3 px-4 py-3 rounded-xl bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 transition-all">
                <span className="text-lg">🎬</span>
                <span className="text-sm text-white">编导</span>
              </a>
            </li>
            <li>
              <a href="/image" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all">
                <span className="text-lg">🎨</span>
                <span className="text-sm text-[#9898a0]">做图</span>
              </a>
            </li>
            <li>
              <a href="/audio" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all">
                <span className="text-lg">🎵</span>
                <span className="text-sm text-[#9898a0]">音频</span>
              </a>
            </li>
            <li>
              <a href="/video" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all">
                <span className="text-lg">📹</span>
                <span className="text-sm text-[#9898a0]">视频</span>
              </a>
            </li>
            <li>
              <a href="/assets" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all">
                <span className="text-lg">📦</span>
                <span className="text-sm text-[#9898a0]">资产</span>
              </a>
            </li>
          </ul>
        </nav>

        <div className="p-4 border-t border-white/[0.08]">
          <div className="bg-gradient-to-r from-[#5b7ce8]/10 to-[#c044d8]/10 rounded-xl p-4 border border-[#5b7ce8]/20">
            <div className="flex items-center justify-between mb-2">
              <span className="text-sm text-[#9898a0]">剩余积分</span>
              <span className="text-lg font-semibold text-white">9,999</span>
            </div>
            <button className="w-full h-10 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-white text-sm font-medium hover:opacity-90 transition-opacity">
              充值积分
            </button>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 flex flex-col">
        {/* Header */}
        <header className="h-16 bg-[#121216] border-b border-white/[0.08] flex items-center justify-between px-6">
          <h2 className="text-lg font-medium text-white">编导工作台</h2>
          <div className="flex items-center gap-4">
            <button className="px-4 py-2 bg-white/[0.05] border border-white/[0.08] rounded-lg text-sm text-[#9898a0] hover:border-[#5b7ce8] transition-all">
              导入脚本
            </button>
            <button className="px-4 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white font-medium">
              新建项目
            </button>
          </div>
        </header>

        {/* Content */}
        <div className="flex-1 p-6 flex gap-6">
          {/* Tools Panel */}
          <div className="w-72 bg-[#121216] rounded-xl border border-white/[0.08] p-4">
            <div className="flex gap-2 mb-6">
              {tools.map((tool) => (
                <button
                  key={tool.id}
                  onClick={() => setActiveTab(tool.id)}
                  className={`flex flex-col items-center gap-1 p-3 rounded-lg transition-all ${
                    activeTab === tool.id
                      ? 'bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30'
                      : 'hover:bg-white/[0.05]'
                  }`}
                >
                  <span className="text-xl">{tool.icon}</span>
                  <span className="text-xs text-[#9898a0]">{tool.name}</span>
                </button>
              ))}
            </div>

            {/* Templates */}
            <div className="border-t border-white/[0.08] pt-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">快速模板</h3>
              <div className="grid grid-cols-2 gap-2">
                {templates.map((template) => (
                  <button key={template.name} className="flex flex-col items-center gap-2 p-3 bg-white/[0.03] rounded-lg hover:bg-white/[0.05] transition-all">
                    <span className="text-2xl">{template.icon}</span>
                    <span className="text-xs text-[#9898a0]">{template.name}</span>
                    <span className="text-xs text-[#5f5f68]">{template.category}</span>
                  </button>
                ))}
              </div>
            </div>

            {/* AI Assist */}
            <div className="border-t border-white/[0.08] pt-4 mt-4">
              <h3 className="text-sm text-[#5f5f68] mb-3">AI 辅助</h3>
              <button className="w-full flex items-center gap-2 p-3 bg-gradient-to-r from-[#5b7ce8]/10 to-[#c044d8]/10 rounded-lg border border-[#5b7ce8]/20 hover:border-[#5b7ce8]/40 transition-all">
                <span className="text-xl">✨</span>
                <span className="text-sm text-white">AI 生成脚本</span>
              </button>
              <button className="w-full flex items-center gap-2 p-3 mt-2 bg-white/[0.03] rounded-lg hover:bg-white/[0.05] transition-all">
                <span className="text-xl">🔄</span>
                <span className="text-sm text-[#9898a0]">AI 优化脚本</span>
              </button>
            </div>
          </div>

          {/* Editor */}
          <div className="flex-1 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            {activeTab === 'script' && (
              <>
                <div className="p-4 border-b border-white/[0.08] flex items-center justify-between">
                  <span className="text-sm text-[#9898a0]">脚本内容</span>
                  <div className="flex gap-2">
                    <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">
                      保存
                    </button>
                    <button className="px-3 py-1.5 bg-white/[0.05] rounded-lg text-xs text-[#9898a0] hover:bg-white/[0.08] transition-all">
                      预览
                    </button>
                  </div>
                </div>
                <div className="flex-1 p-4">
                  <textarea
                    value={scriptContent}
                    onChange={(e) => setScriptContent(e.target.value)}
                    placeholder="在此输入脚本内容，或使用 AI 生成..."
                    className="w-full h-full bg-white/[0.03] border border-white/[0.08] rounded-lg p-4 text-white text-sm resize-none outline-none focus:border-[#5b7ce8] transition-colors"
                  />
                </div>
              </>
            )}

            {activeTab === 'scenes' && (
              <>
                <div className="p-4 border-b border-white/[0.08] flex items-center justify-between">
                  <span className="text-sm text-[#9898a0]">场景列表</span>
                  <button className="px-3 py-1.5 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-xs text-white font-medium">
                    + 添加场景
                  </button>
                </div>
                <div className="flex-1 p-4 space-y-3 overflow-auto">
                  {scenes.map((scene) => (
                    <div key={scene.id} className="flex items-center gap-4 p-4 bg-white/[0.03] rounded-lg border border-white/[0.08] hover:border-[#5b7ce8]/30 transition-all">
                      <div className="w-16 h-16 bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 rounded-lg flex items-center justify-center">
                        <span className="text-lg">🎬</span>
                      </div>
                      <div className="flex-1">
                        <h4 className="text-sm text-white mb-1">{scene.name}</h4>
                        <p className="text-xs text-[#5f5f68]">{scene.description}</p>
                      </div>
                      <div className="text-right">
                        <span className="text-sm text-[#9898a0]">{scene.duration}</span>
                      </div>
                      <button className="p-2 hover:bg-white/[0.05] rounded-lg transition-all">
                        <svg className="w-4 h-4 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                          <circle cx="12" cy="12" r="1" />
                          <circle cx="12" cy="5" r="1" />
                          <circle cx="12" cy="19" r="1" />
                        </svg>
                      </button>
                    </div>
                  ))}
                </div>
              </>
            )}

            {activeTab === 'storyboard' && (
              <div className="flex-1 p-4 flex flex-col items-center justify-center">
                <div className="text-center">
                  <span className="text-4xl mb-4">🖼️</span>
                  <h3 className="text-lg text-white mb-2">故事板视图</h3>
                  <p className="text-sm text-[#5f5f68] mb-4">可视化场景排列，快速调整顺序</p>
                  <button className="px-4 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white font-medium">
                    创建故事板
                  </button>
                </div>
              </div>
            )}

            {activeTab === 'timeline' && (
              <div className="flex-1 p-4 flex flex-col items-center justify-center">
                <div className="text-center">
                  <span className="text-4xl mb-4">⏱️</span>
                  <h3 className="text-lg text-white mb-2">时间轴编辑</h3>
                  <p className="text-sm text-[#5f5f68] mb-4">精确控制每个场景的时长和过渡</p>
                  <button className="px-4 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white font-medium">
                    打开时间轴
                  </button>
                </div>
              </div>
            )}
          </div>

          {/* Preview Panel */}
          <div className="w-80 bg-[#121216] rounded-xl border border-white/[0.08] flex flex-col">
            <div className="p-4 border-b border-white/[0.08]">
              <span className="text-sm text-[#9898a0]">预览</span>
            </div>
            <div className="flex-1 flex flex-col items-center justify-center p-4">
              <div className="w-full aspect-video bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 rounded-lg flex items-center justify-center mb-4">
                <svg className="w-12 h-12 text-white/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                  <polygon points="5 3 19 12 5 21 5 3" />
                </svg>
              </div>
              <div className="flex gap-2">
                <button className="p-2 bg-white/[0.05] rounded-lg hover:bg-white/[0.08] transition-all">
                  <svg className="w-5 h-5 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polygon points="19 20 9 12 19 4 19 20" />
                    <line x1="5" y1="19" x2="5" y2="5" />
                  </svg>
                </button>
                <button className="p-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg">
                  <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="currentColor">
                    <polygon points="5 3 19 12 5 21 5 3" />
                  </svg>
                </button>
                <button className="p-2 bg-white/[0.05] rounded-lg hover:bg-white/[0.08] transition-all">
                  <svg className="w-5 h-5 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <polygon points="5 4 15 12 5 20 5 4" />
                    <line x1="19" y1="5" x2="19" y2="19" />
                  </svg>
                </button>
              </div>
            </div>
            <div className="p-4 border-t border-white/[0.08]">
              <div className="flex items-center justify-between text-xs text-[#5f5f68]">
                <span>00:00</span>
                <span>总时长: 00:45</span>
                <span>00:45</span>
              </div>
              <div className="mt-2 h-2 bg-white/[0.05] rounded-full overflow-hidden">
                <div className="w-1/3 h-full bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-full" />
              </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}