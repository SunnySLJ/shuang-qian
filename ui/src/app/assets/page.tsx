'use client';

import { useState } from 'react';

export default function AssetsPage() {
  const [activeTab, setActiveTab] = useState('all');
  const [searchQuery, setSearchQuery] = useState('');

  const tabs = [
    { id: 'all', name: '全部', count: 128 },
    { id: 'image', name: '图片', count: 45 },
    { id: 'video', name: '视频', count: 32 },
    { id: 'audio', name: '音频', count: 25 },
    { id: 'script', name: '脚本', count: 16 },
    { id: 'template', name: '模板', count: 10 },
  ];

  const assets = [
    { id: 1, name: '梦幻森林', type: 'video', size: '15.2MB', duration: '00:15', createTime: '2024-04-07', thumbnail: 'video' },
    { id: 2, name: '产品海报', type: 'image', size: '2.5MB', resolution: '1920x1080', createTime: '2024-04-06', thumbnail: 'image' },
    { id: 3, name: '品牌配音', type: 'audio', size: '1.2MB', duration: '00:30', createTime: '2024-04-05', thumbnail: 'audio' },
    { id: 4, name: '产品介绍脚本', type: 'script', size: '12KB', words: 500, createTime: '2024-04-04', thumbnail: 'script' },
    { id: 5, name: '城市夜景', type: 'video', size: '22.8MB', duration: '00:30', createTime: '2024-04-03', thumbnail: 'video' },
    { id: 6, name: '风景照片', type: 'image', size: '3.8MB', resolution: '4K', createTime: '2024-04-02', thumbnail: 'image' },
    { id: 7, name: '轻快音乐', type: 'audio', size: '4.5MB', duration: '02:00', createTime: '2024-04-01', thumbnail: 'audio' },
    { id: 8, name: '商业模板', type: 'template', size: '500KB', createTime: '2024-03-31', thumbnail: 'template' },
  ];

  const filteredAssets = activeTab === 'all' ? assets : assets.filter(a => a.type === activeTab);

  const getThumbnailIcon = (type: string) => {
    switch (type) {
      case 'video':
        return <svg className="w-8 h-8 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1"><polygon points="5 3 19 12 5 21 5 3" /></svg>;
      case 'image':
        return <svg className="w-8 h-8 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1"><rect x="3" y="3" width="18" height="18" rx="2" ry="2" /><circle cx="8.5" cy="8.5" r="1.5" /><polyline points="21 15 16 10 5 21" /></svg>;
      case 'audio':
        return <svg className="w-8 h-8 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1"><path d="M9 18V5l12-2v13" /><circle cx="6" cy="18" r="3" /><circle cx="18" cy="16" r="3" /></svg>;
      case 'script':
        return <svg className="w-8 h-8 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z" /><polyline points="14 2 14 8 20 8" /></svg>;
      default:
        return <svg className="w-8 h-8 text-white/50" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1"><rect x="3" y="3" width="7" height="7" /><rect x="14" y="3" width="7" height="7" /><rect x="14" y="14" width="7" height="7" /><rect x="3" y="14" width="7" height="7" /></svg>;
    }
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
            <li><a href="/video" className="flex items-center gap-3 px-4 py-3 rounded-xl hover:bg-white/[0.05] transition-all"><span className="text-lg">📹</span><span className="text-sm text-[#9898a0]">视频</span></a></li>
            <li><a href="/assets" className="flex items-center gap-3 px-4 py-3 rounded-xl bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 transition-all"><span className="text-lg">📦</span><span className="text-sm text-white">资产</span></a></li>
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
          <h2 className="text-lg font-medium text-white">资产库</h2>
          <div className="flex items-center gap-4">
            <div className="relative">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="搜索资产..."
                className="w-48 h-10 pl-10 pr-4 bg-white/[0.05] border border-white/[0.08] rounded-lg text-sm text-white outline-none focus:border-[#5b7ce8] transition-colors"
              />
              <svg className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-[#5f5f68]" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><circle cx="11" cy="11" r="8" /><path d="m21 21-4.35-4.35" /></svg>
            </div>
            <button className="px-4 py-2 bg-white/[0.05] border border-white/[0.08] rounded-lg text-sm text-[#9898a0] hover:border-[#5b7ce8] transition-all">上传资产</button>
            <button className="px-4 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white font-medium">新建文件夹</button>
          </div>
        </header>

        {/* Tabs */}
        <div className="px-6 pt-6 pb-4">
          <div className="flex gap-2">
            {tabs.map((tab) => (
              <button
                key={tab.id}
                onClick={() => setActiveTab(tab.id)}
                className={`flex items-center gap-2 px-4 py-2 rounded-lg text-sm transition-all ${
                  activeTab === tab.id
                    ? 'bg-gradient-to-r from-[#5b7ce8]/20 to-[#c044d8]/20 border border-[#5b7ce8]/30 text-white'
                    : 'bg-white/[0.05] text-[#9898a0] hover:bg-white/[0.08]'
                }`}
              >
                {tab.name}
                <span className="text-xs bg-white/[0.1] px-1.5 py-0.5 rounded">{tab.count}</span>
              </button>
            ))}
          </div>
        </div>

        {/* Assets Grid */}
        <div className="flex-1 px-6 pb-6 overflow-auto">
          <div className="grid grid-cols-5 gap-4">
            {filteredAssets.map((asset) => (
              <div key={asset.id} className="group bg-[#121216] rounded-xl border border-white/[0.08] overflow-hidden hover:border-[#5b7ce8]/30 transition-all cursor-pointer">
                {/* Thumbnail */}
                <div className="aspect-square bg-gradient-to-br from-[#5b7ce8]/20 to-[#c044d8]/20 flex items-center justify-center relative">
                  {getThumbnailIcon(asset.type)}
                  {asset.type === 'video' && (
                    <div className="absolute bottom-2 right-2 px-2 py-1 bg-black/60 rounded text-xs text-white">{asset.duration}</div>
                  )}
                  <div className="absolute inset-0 bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-2">
                    <button className="p-2 bg-white/[0.1] rounded-lg hover:bg-white/[0.2] transition-all">
                      <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" /><circle cx="12" cy="12" r="3" /></svg>
                    </button>
                    <button className="p-2 bg-white/[0.1] rounded-lg hover:bg-white/[0.2] transition-all">
                      <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4" /><polyline points="7 10 12 15 17 10" /><line x1="12" y1="15" x2="12" y2="3" /></svg>
                    </button>
                    <button className="p-2 bg-white/[0.1] rounded-lg hover:bg-white/[0.2] transition-all">
                      <svg className="w-5 h-5 text-white" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2"><polyline points="3 6 5 6 21 6" /><path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2" /></svg>
                    </button>
                  </div>
                </div>

                {/* Info */}
                <div className="p-3">
                  <h3 className="text-sm text-white mb-1 truncate">{asset.name}</h3>
                  <div className="flex items-center justify-between text-xs text-[#5f5f68]">
                    <span>{asset.size}</span>
                    <span>{asset.createTime}</span>
                  </div>
                  {asset.resolution && <span className="text-xs text-[#5f5f68] mt-1 block">{asset.resolution}</span>}
                  {asset.words && <span className="text-xs text-[#5f5f68] mt-1 block">{asset.words}字</span>}
                </div>
              </div>
            ))}
          </div>

          {/* Pagination */}
          <div className="flex items-center justify-center gap-2 mt-8">
            <button className="px-3 py-2 bg-white/[0.05] rounded-lg text-sm text-[#9898a0] hover:bg-white/[0.08] transition-all">上一页</button>
            <button className="px-3 py-2 bg-gradient-to-r from-[#5b7ce8] to-[#c044d8] rounded-lg text-sm text-white">1</button>
            <button className="px-3 py-2 bg-white/[0.05] rounded-lg text-sm text-[#9898a0] hover:bg-white/[0.08] transition-all">2</button>
            <button className="px-3 py-2 bg-white/[0.05] rounded-lg text-sm text-[#9898a0] hover:bg-white/[0.08] transition-all">3</button>
            <button className="px-3 py-2 bg-white/[0.05] rounded-lg text-sm text-[#9898a0] hover:bg-white/[0.08] transition-all">下一页</button>
          </div>
        </div>
      </main>
    </div>
  );
}