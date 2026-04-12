#!/bin/bash
#
# 追梦 Dream 前后端一键启动脚本
# 使用方法: ./script/startup.sh
#

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -f "$SCRIPT_DIR/pom.xml" ] && [ -d "$SCRIPT_DIR/shuang-server" ]; then
    PROJECT_DIR="$SCRIPT_DIR"
else
    PROJECT_DIR="$(dirname "$SCRIPT_DIR")"
fi
BACKEND_DIR="$PROJECT_DIR/shuang-server"
FRONTEND_DIR="$PROJECT_DIR/shuang-ui/zhuimeng-dream"
JAR_FILE="$BACKEND_DIR/target/shuang-server.jar"

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info()  { echo -e "${CYAN}[INFO]${NC}  $1"; }
log_ok()   { echo -e "${GREEN}[OK]${NC}   $1"; }
log_warn() { echo -e "${YELLOW}[WARN]${NC}  $1"; }
log_error(){ echo -e "${RED}[ERROR]${NC} $1"; }

echo ""
echo "========================================"
echo "  追梦 Dream - 前后端一键启动"
echo "========================================"
echo ""

# ==================== 依赖检查 ====================
log_info "检查依赖..."

check_command() {
    if ! command -v "$1" &> /dev/null; then
        log_error "缺少命令: $1，请先安装"
        exit 1
    fi
}

check_command "java"
check_command "node"
check_command "npm"
check_command "mysql"
check_command "redis-cli"

log_ok "依赖检查通过"

# ==================== 数据库检查 ====================
log_info "检查 MySQL..."

MYSQL_HOST="127.0.0.1"
MYSQL_PORT="3306"
MYSQL_USER="root"
MYSQL_PASSWORD="yaok@123"
MYSQL_ARGS=(-h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" "-p$MYSQL_PASSWORD")

if ! mysql "${MYSQL_ARGS[@]}" -e "SELECT 1" &> /dev/null; then
    log_warn "MySQL 未运行，正在尝试启动..."
    if command -v mysql.server &> /dev/null; then
        mysql.server start 2>/dev/null || mysqld_safe --user=mysql &
        sleep 5
    fi
fi

if ! mysql "${MYSQL_ARGS[@]}" -e "SELECT 1" &> /dev/null; then
    log_error "MySQL 启动失败，请手动启动后重试"
    exit 1
fi

# 创建数据库（如果不存在）
mysql "${MYSQL_ARGS[@]}" -e "CREATE DATABASE IF NOT EXISTS zhuimeng CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null
log_ok "MySQL 就绪"

# ==================== Redis 检查 ====================
log_info "检查 Redis..."

if ! redis-cli ping &> /dev/null; then
    log_warn "Redis 未运行，正在尝试启动..."
    brew services start redis 2>/dev/null || redis-server --daemonize yes 2>/dev/null
    sleep 2
fi

if redis-cli ping &> /dev/null; then
    log_ok "Redis 就绪"
else
    log_error "Redis 启动失败，请手动启动后重试"
    exit 1
fi

# ==================== 表结构初始化 ====================
log_info "检查数据库表结构..."

TABLE_COUNT=$(mysql "${MYSQL_ARGS[@]}" zhuimeng -N -e "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='zhuimeng';" 2>/dev/null)

if [ "$TABLE_COUNT" -lt 10 ]; then
    log_warn "数据库表结构不完整，正在初始化..."
    SQL_DIR="$PROJECT_DIR/sql/mysql"

    for f in "$SQL_DIR/zhuimeng-init.sql" \
             "$SQL_DIR/shuang-pro-init.sql" \
             "$SQL_DIR/ai-image-table.sql" \
             "$SQL_DIR/ai-video-extension.sql" \
             "$SQL_DIR/ai-model-table.sql" \
             "$SQL_DIR/ai-tutorial-video-tables.sql" \
             "$SQL_DIR/ai-inspiration-tables.sql" \
             "$SQL_DIR/ai-inspiration-init-data.sql" \
             "$SQL_DIR/shuang-pro-agency.sql" \
             "$SQL_DIR/agency-user-table.sql" \
             "$SQL_DIR/ai-user-asset-tables.sql"; do
        if [ -f "$f" ]; then
            mysql "${MYSQL_ARGS[@]}" zhuimeng < "$f" 2>/dev/null || true
        fi
    done

    log_ok "数据库初始化完成"
else
    log_ok "数据库表结构完整 ($TABLE_COUNT 张表)"
fi

# ==================== 后端编译 ====================
log_info "检查后端 JAR..."

if [ ! -f "$JAR_FILE" ]; then
    log_warn "后端未编译，正在编译..."
    cd "$PROJECT_DIR"
    mvn clean package -DskipTests -q
    log_ok "后端编译完成"
else
    JAR_SIZE=$(du -h "$JAR_FILE" | cut -f1)
    log_ok "后端 JAR 已就绪 ($JAR_SIZE)"
fi

# ==================== 端口检查 ====================
check_port() {
    local port=$1
    local name=$2
    if lsof -ti:$port &> /dev/null; then
        log_warn "$name 端口 $port 已被占用 (PID: $(lsof -ti:$port | head -1))"
        if [ -t 0 ]; then
            read -p "是否停止旧进程? [Y/n]: " -n 1 -r
            echo
        else
            REPLY="Y"
            log_info "检测到非交互环境，默认停止旧进程"
        fi
        if [[ ! ${REPLY:-Y} =~ ^[Nn]$ ]]; then
            lsof -ti:$port | xargs kill -9 2>/dev/null || true
            sleep 1
            log_info "已停止旧进程"
        else
            log_warn "跳过，启动可能失败"
        fi
    fi
}

check_port 48080 "后端"
check_port 8888 "前端"

# ==================== 启动后端 ====================
log_info "启动后端 Spring Boot (端口 48080)..."

cd "$PROJECT_DIR"
nohup java -jar "$JAR_FILE" --spring.profiles.active=local > /tmp/shuang-backend.log 2>&1 &
BACKEND_PID=$!

log_ok "后端启动中，PID: $BACKEND_PID"
log_info "等待后端启动..."

# 等待后端就绪（最多 60 秒）
for i in $(seq 1 30); do
    if curl -s http://localhost:48080/v3/api-docs > /dev/null 2>&1; then
        break
    fi
    sleep 2
done

if curl -s http://localhost:48080/v3/api-docs > /dev/null 2>&1; then
    log_ok "后端启动成功 ✓ (PID: $BACKEND_PID)"
else
    log_error "后端启动超时，请检查日志: tail -f /tmp/shuang-backend.log"
fi

# ==================== 启动前端 ====================
log_info "启动前端 Vite 开发服务器 (端口 8888)..."

cd "$FRONTEND_DIR"
nohup npm run dev -- --port 8888 > /tmp/shuang-frontend.log 2>&1 &
FRONTEND_PID=$!

log_ok "前端启动中，PID: $FRONTEND_PID"
sleep 5

if curl -s http://localhost:8888 > /dev/null 2>&1; then
    log_ok "前端启动成功 ✓ (PID: $FRONTEND_PID)"
else
    log_error "前端启动超时，请检查日志: tail -f /tmp/shuang-frontend.log"
fi

# ==================== 完成 ====================
echo ""
echo "========================================"
echo "  启动完成！"
echo "========================================"
echo ""
echo -e "  前端地址:     ${CYAN}http://localhost:8888${NC}"
echo -e "  后端地址:     ${CYAN}http://localhost:48080${NC}"
echo -e "  接口文档:     ${CYAN}http://localhost:48080/swagger-ui${NC}"
echo -e "  爆款拆解:    ${CYAN}http://localhost:8888/ai/video/analyze${NC}"
echo ""
echo -e "  后端日志: tail -f /tmp/shuang-backend.log"
echo -e "  前端日志: tail -f /tmp/shuang-frontend.log"
echo ""
echo -e "  关闭服务: kill $BACKEND_PID $FRONTEND_PID"
echo ""
