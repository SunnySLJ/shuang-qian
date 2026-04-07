'use client';

import { useState } from 'react';
import Link from 'next/link';

export default function HomePage() {
  const [activeNav, setActiveNav] = useState('灵感');
  const [activeCategory, setActiveCategory] = useState('全部');

  const navItems = [
    { name: '灵感', icon: '💡' },
    { name: '编导', icon: '🎬' },
    { name: '做图', icon: '🎨' },
    { name: '音频', icon: '🎵' },
    { name: '视频', icon: '📹' },
    { name: '资产', icon: '📦' },
  ];

  const aiTools = [
    { name: '索拉', desc: 'AI视频生成' },
    { name: '马克', desc: 'AI图片生成' },
    { name: '威尔', desc: 'AI配音' },
    { name: '燃动数字人', desc: '数字人直播' },
    { name: 'AI超级混剪', desc: '智能剪辑' },
  ];

  const categories = ['全部', '热门', '最新', '唯美', '商业', '创意', '人物', '风景'];

  const cases = [
    { id: 1, title: '梦幻森林', type: 'video', views: 1234, likes: 567, cover: '/api/placeholder/400/300' },
    { id: 2, title: '城市夜景', type: 'image', views: 2345, likes: 890, cover: '/api/placeholder/400/300' },
    { id: 3, title: '海边日落', type: 'video', views: 3456, likes: 1234, cover: '/api/placeholder/400/300' },
    { id: 4, title: '科技未来', type: 'image', views: 4567, likes: 1567, cover: '/api/placeholder/400/300' },
    { id: 5, title: '自然风光', type: 'video', views: 5678, likes: 1890, cover: '/api/placeholder/400/300' },
    { id: 6, title: '人文艺术', type: 'image', views: 6789, likes: 2123, cover: '/api/placeholder/400/300' },
    { id: 7, title: '商业广告', type: 'video', views: 7890, likes: 2456, cover: '/api/placeholder/400/300' },
    { id: 8, title: '创意短片', type: 'video', views: 8901, likes: 2789, cover: '/api/placeholder/400/300' },
    { id: 9, title: '人物肖像', type: 'image', views: 9012, likes: 3123, cover: '/api/placeholder/400/300' },
    { id: 10, title: '建筑摄影', type: 'image', views: 1023, likes: 3456, cover: '/api/placeholder/400/300' },
    { id: 11, title: '动态海报', type: 'video', views: 1234, likes: 3789, cover: '/api/placeholder/400/300' },
    { id: 12, title: '品牌故事', type: 'video', views: 2345, likes: 4012, cover: '/api/placeholder/400/300' },
  ];

  return (
    <div className="min-h-screen flex bg-[#0a0a0c]">
      {/* Sidebar */}
      <aside className="w-64 bg-[#121216] border-r border-white/[0.08] flex flex-col">
        {/* Logo */}
        <div className="h-16 flex items-center px-6 border-b border-white/[0.08]">
          <h1 className="text-xl font-semibold bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] bg-clip-text text-transparent">
            梦恋
          </h1>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4">
          <ul className="space-y-2">
            {navItems.map((item) => (
              <li key={item.name}>
                <button
                  onClick={() => setActiveNav(item.name)}
                  className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl transition-all ${
                    activeNav === item.name
                      ? 'bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30'
                      : 'hover:bg-white/[0.05]'
                  }`}
                >
                  <span className="text-lg">{item.icon}</span>
                  <span className="text-sm text-[#9898a0]">{item.name}</span>
                </button>
              </li>
            ))}
          </ul>

          {/* AI Tools Section */}
          <div className="mt-8 pt-6 border-t border-white/[0.08]">
            <h3 className="text-xs text-[#5f5f68] uppercase tracking-wider mb-4 px-4">AI 工具</h3>
            <ul className="space-y-1">
              {aiTools.map((tool) => (
                <li key={tool.name}>
                  <button className="w-full flex items-center justify-between px-4 py-2.5 rounded-lg hover:bg-white/[0.05] transition-all group">
                    <span className="text-sm text-[#9898a0] group-hover:text-white">{tool.name}</span>
                    <span className="text-xs text-[#5f5f68]">{tool.desc}</span>
                  </button>
                </li>
              ))}
            </ul>
          </div>
        </nav>

        {/* Points */}
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
          {/* Search */}
          <div className="relative w-64">
            <input
              type="text"
              placeholder="搜索灵感..."
              className="w-full h-10 pl-10 pr-4 bg-white/[0.05] border border-white/[0.08] rounded-lg text-sm text-white outline-none focus:border-[#5b7ce8] transition-colors"
            />
            <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#5f5f68]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
              <circle cx="11" cy="11" r="8" />
              <path d="m21 21-4.35-4.35" />
            </svg>
          </div>

          {/* Actions */}
          <div className="flex items-center gap-4">
            <button className="relative p-2 hover:bg-white/[0.05] rounded-lg transition-colors">
              <svg className="w-5 h-5 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9" />
                <path d="M13.73 21a2 2 0 0 0-3.46 0" />
              </svg>
              <span className="absolute top-1 right-1 w-2 h-2 bg-[#c044d8] rounded-full" />
            </button>
            <button className="p-2 hover:bg-white/[0.05] rounded-lg transition-colors">
              <svg className="w-5 h-5 text-[#9898a0]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                <circle cx="12" cy="12" r="3" />
                <path d="M12 1v6m0 6v6m-9-9h6m6 0h6" />
              </svg>
            </button>
            <div className="flex items-center gap-3 pl-4 border-l border-white/[0.08]">
              <div className="w-9 h-9 bg-gradient-to-br from-[#5b7ce8] to-[#c044d8] rounded-full flex items-center justify-center text-white text-sm font-medium">
                U
              </div>
              <span className="text-sm text-[#9898a0]">用户名</span>
            </div>
          </div>
        </header>

        {/* Content */}
        <div className="flex-1 p-6 overflow-auto">
          {/* Categories */}
          <div className="flex items-center gap-2 mb-6">
            {categories.map((cat) => (
              <button
                key={cat}
                onClick={() => setActiveCategory(cat)}
                className={`px-4 py-2 rounded-lg text-sm transition-all ${
                  activeCategory === cat
                    ? 'bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] text-white'
                    : 'bg-white/[0.05] text-[#9898a0] hover:bg-white/[0.08]'
                }`}
              >
                {cat}
              </button>
            ))}
          </div>

          {/* Cases Grid */}
          <div className="grid grid-cols-4 gap-4">
            {cases.map((item) => (
              <div
                key={item.id}
                className="group relative bg-[#121216] rounded-xl overflow-hidden border border-white/[0.08] hover:border-[#5b7ce8]/30 transition-all cursor-pointer"
              >
                {/* Cover */}
                <div className="aspect-[4/3] bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 relative">
                  <div className="absolute inset-0 flex items-center justify-center">
                    <svg className="w-12 h-12 text-white/30" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1">
                      {item.type === 'video' ? (
                        <polygon points="5 3 19 12 5 21 5 3" />
                      ) : (
                        <rect x="3" y="3" width="18" height="18" rx="2" ry="2" />
                      )}
                    </svg>
                  </div>
                  {item.type === 'video' && (
                    <div className="absolute bottom-2 right-2 px-2 py-1 bg-black/60 rounded text-xs text-white">
                      00:30
                    </div>
                  )}
                  <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                </div>

                {/* Info */}
                <div className="p-3">
                  <h3 className="text-sm text-white mb-2 truncate">{item.title}</h3>
                  <div className="flex items-center gap-4 text-xs text-[#5f5f68]">
                    <span className="flex items-center gap-1">
                      <svg className="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
                        <circle cx="12" cy="12" r="3" />
                      </svg>
                      {item.views}
                    </span>
                    <span className="flex items-center gap-1">
                      <svg className="w-3 h-3" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                        <path d="M20.84 4.61a5.5 5.5 0 0 0-7.78 0L12 5.67l-1.06-1.06a5.5 5.5 0 0 0-7.78 7.78l1.06 1.06L12 21.23l7.78-7.78 1.06-1.06a5.5 5.5 0 0 0 0-7.78z" />
                      </svg>
                      {item.likes}
                    </span>
                  </div>
                </div>

                {/* Hover Overlay */}
                <div className="absolute inset-0 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity pointer-events-none">
                  <button className="w-12 h-12 bg-[#5b7ce8] rounded-full flex items-center justify-center pointer-events-auto">
                    <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="currentColor">
                      <polygon points="5 3 19 12 5 21 5 3" />
                    </svg>
                  </button>
                </div>
              </div>
            ))}
          </div>

          {/* Load More */}
          <div className="flex justify-center mt-8">
            <button className="px-8 py-3 bg-white/[0.05] border border-white/[0.08] rounded-xl text-sm text-[#9898a0] hover:border-[#5b7ce8] hover:text-[#5b7ce8] transition-all">
              加载更多
            </button>
          </div>
        </div>
      </main>
    </div>
  );
}