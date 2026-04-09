import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // API代理配置，解决跨域问题
  async rewrites() {
    return [
      {
        source: '/app-api/:path*',
        destination: 'http://127.0.0.1:48080/app-api/:path*',
      },
      {
        source: '/admin-api/:path*',
        destination: 'http://127.0.0.1:48080/admin-api/:path*',
      },
    ];
  },
};

export default nextConfig;