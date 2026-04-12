#!/bin/bash
#
# 追梦 Dream 停止脚本
# 使用方法: ./script/shutdown.sh
#

RED='\033[0;31m'
GREEN='\033[0;32m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info() { echo -e "${CYAN}[INFO]${NC}  $1"; }
log_ok()  { echo -e "${GREEN}[OK]${NC}   $1"; }

echo ""
echo "========================================"
echo "  追梦 Dream - 停止服务"
echo "========================================"
echo ""

# 停止占用 48080 端口的进程（后端）
if lsof -ti:48080 &> /dev/null; then
    PID=$(lsof -ti:48080)
    kill -15 $PID 2>/dev/null
    sleep 1
    if ! lsof -ti:48080 &> /dev/null; then
        log_ok "后端 (PID: $PID) 已停止"
    else
        log_info "强制停止后端..."
        kill -9 $PID 2>/dev/null
        log_ok "后端已强制停止"
    fi
else
    log_ok "后端未运行"
fi

# 停止占用 8888 端口的进程（前端 Vite）
if lsof -ti:8888 &> /dev/null; then
    PID=$(lsof -ti:8888)
    kill -15 $PID 2>/dev/null
    sleep 1
    if ! lsof -ti:8888 &> /dev/null; then
        log_ok "前端 (PID: $PID) 已停止"
    else
        log_info "强制停止前端..."
        kill -9 $PID 2>/dev/null
        log_ok "前端已强制停止"
    fi
else
    log_ok "前端未运行"
fi

echo ""
log_ok "所有服务已停止"
echo ""
