<template>
  <div class="dashboard-analysis">
    <!-- 顶部信息栏 -->
    <div class="dashboard-header">
      <div class="time-info">
        <div class="current-time">{{ formatTime(currentTime) }}</div>
        <div class="time-label">当前时间</div>
      </div>
      <div class="health-status">
        <div class="health-level" :class="getHealthClass(healthLevel)">
          {{ healthLevel }}
        </div>
        <div class="health-label">系统健康度</div>
      </div>
      <div class="health-score">
        <div class="score-value">{{ healthScore }}</div>
        <div class="score-label">健康评分</div>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-grid">
      <!-- 今日发帖数 -->
      <el-card shadow="hover" class="metric-card">
        <div class="metric-content">
          <div class="metric-icon" style="background-color: #409EFF;">
            📝
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ todayMetrics.posts }}</div>
            <div class="metric-label">今日发帖</div>
          </div>
          <div class="metric-trend">
            <div class="trend-indicator" :class="getTrendClass('posts')">
              {{ getTrendIndicator('posts') }}
            </div>
          </div>
        </div>
      </el-card>

      <!-- 今日评论数 -->
      <el-card shadow="hover" class="metric-card">
        <div class="metric-content">
          <div class="metric-icon" style="background-color: #67C23A;">
            💬
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ todayMetrics.comments }}</div>
            <div class="metric-label">今日评论</div>
          </div>
          <div class="metric-trend">
            <div class="trend-indicator" :class="getTrendClass('comments')">
              {{ getTrendIndicator('comments') }}
            </div>
          </div>
        </div>
      </el-card>

      <!-- 今日点赞数 -->
      <el-card shadow="hover" class="metric-card">
        <div class="metric-content">
          <div class="metric-icon" style="background-color: #E6A23C;">
            👍
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ todayMetrics.likes }}</div>
            <div class="metric-label">今日点赞</div>
          </div>
          <div class="metric-trend">
            <div class="trend-indicator" :class="getTrendClass('likes')">
              {{ getTrendIndicator('likes') }}
            </div>
          </div>
        </div>
      </el-card>

      <!-- 今日浏览数 -->
      <el-card shadow="hover" class="metric-card">
        <div class="metric-content">
          <div class="metric-icon" style="background-color: #F56C6C;">
            👁️
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ formatViews(todayMetrics.views) }}</div>
            <div class="metric-label">今日浏览</div>
          </div>
          <div class="metric-trend">
            <div class="trend-indicator" :class="getTrendClass('views')">
              {{ getTrendIndicator('views') }}
            </div>
          </div>
        </div>
      </el-card>

      <!-- 最近一小时发帖 -->
      <el-card shadow="hover" class="metric-card">
        <div class="metric-content">
          <div class="metric-icon" style="background-color: #909399;">
            ⏰
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ lastHourPosts }}</div>
            <div class="metric-label">最近1小时发帖</div>
          </div>
          <div class="metric-trend">
            <div class="trend-indicator" :class="getHourTrendClass()">
              {{ getHourTrendIndicator() }}
            </div>
          </div>
        </div>
      </el-card>

      <!-- 健康评分 -->
      <el-card shadow="hover" class="metric-card">
        <div class="metric-content">
          <div class="metric-icon" style="background: linear-gradient(135deg, #67C23A, #409EFF);">
            ⭐
          </div>
          <div class="metric-info">
            <div class="metric-value">{{ healthScore }}</div>
            <div class="metric-label">健康评分</div>
          </div>
          <div class="metric-trend">
            <div class="score-progress">
              <el-progress
                :percentage="healthScore"
                :color="getHealthScoreColor(healthScore)"
                :show-text="false"
                :stroke-width="8"
              />
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 洞察分析 -->
    <el-card v-if="insights.length > 0" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>系统洞察</span>
          <el-tag type="info">实时分析</el-tag>
        </div>
      </template>
      <div class="insights-list">
        <div v-for="(insight, index) in insights" :key="index" class="insight-item">
          <div class="insight-icon">
            <span v-if="insight.type === 'warning'" style="color: #E6A23C;">⚠️</span>
            <span v-else-if="insight.type === 'success'" style="color: #67C23A;">✅</span>
            <span v-else style="color: #409EFF;">ℹ️</span>
          </div>
          <div class="insight-content">
            <div class="insight-title">{{ insight.title }}</div>
            <div class="insight-description">{{ insight.description }}</div>
          </div>
          <div class="insight-time">{{ formatRelativeTime(insight.time) }}</div>
        </div>
      </div>
    </el-card>

    <!-- 无洞察时的提示 -->
    <el-card v-else shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>系统洞察</span>
          <el-tag type="success">运行正常</el-tag>
        </div>
      </template>
      <div class="no-insights">
        <div style="font-size: 48px; margin-bottom: 20px;">📊</div>
        <div style="font-size: 16px; color: #606266; margin-bottom: 10px;">
          系统运行正常，暂无异常洞察
        </div>
        <div style="font-size: 14px; color: #909399;">
          所有指标均在正常范围内，系统健康运行中
        </div>
      </div>
    </el-card>

    <!-- 系统状态说明 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>系统状态说明</span>
        </div>
      </template>
      <div class="status-explanation">
        <div class="status-item">
          <div class="status-indicator status-healthy"></div>
          <div class="status-info">
            <div class="status-title">健康</div>
            <div class="status-desc">所有核心指标正常，系统运行稳定</div>
          </div>
        </div>
        <div class="status-item">
          <div class="status-indicator status-warning"></div>
          <div class="status-info">
            <div class="status-title">警告</div>
            <div class="status-desc">部分指标异常，需要关注</div>
          </div>
        </div>
        <div class="status-item">
          <div class="status-indicator status-danger"></div>
          <div class="status-info">
            <div class="status-title">危险</div>
            <div class="status-desc">核心指标严重异常，需要立即处理</div>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { getDashboardAnalysisApi } from '@/api/postApi.js'

// 响应式数据
const currentTime = ref('')
const healthLevel = ref('')
const lastHourPosts = ref(0)
const todayMetrics = ref({
  posts: 0,
  comments: 0,
  likes: 0,
  views: 0
})
const insights = ref([])
const healthScore = ref(0)

// 历史数据用于趋势计算
const previousMetrics = ref({
  posts: 0,
  comments: 0,
  likes: 0,
  views: 0
})

// 定时器
let refreshTimer = null

// 格式化时间显示
const formatTime = (timeStr) => {
  if (!timeStr) return ''
  const date = new Date(timeStr)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false
  })
}

// 格式化浏览数
const formatViews = (views) => {
  if (views >= 10000) {
    return (views / 10000).toFixed(1) + '万'
  }
  return views.toLocaleString()
}

// 获取健康状态样式
const getHealthClass = (level) => {
  switch (level) {
    case '健康': return 'health-healthy'
    case '警告': return 'health-warning'
    case '危险': return 'health-danger'
    default: return 'health-unknown'
  }
}

// 获取健康评分颜色
const getHealthScoreColor = (score) => {
  if (score >= 80) return '#67C23A'
  if (score >= 60) return '#E6A23C'
  return '#F56C6C'
}

// 获取趋势样式
const getTrendClass = (metric) => {
  const current = todayMetrics.value[metric]
  const previous = previousMetrics.value[metric]

  if (current > previous) return 'trend-up'
  if (current < previous) return 'trend-down'
  return 'trend-stable'
}

// 获取趋势指示器
const getTrendIndicator = (metric) => {
  const current = todayMetrics.value[metric]
  const previous = previousMetrics.value[metric]

  if (current > previous) return '↑'
  if (current < previous) return '↓'
  return '→'
}

// 获取小时趋势样式
const getHourTrendClass = () => {
  if (lastHourPosts.value > 20) return 'trend-up'
  if (lastHourPosts.value > 10) return 'trend-stable'
  return 'trend-down'
}

// 获取小时趋势指示器
const getHourTrendIndicator = () => {
  if (lastHourPosts.value > 20) return '↑'
  if (lastHourPosts.value > 10) return '→'
  return '↓'
}

// 格式化相对时间
const formatRelativeTime = (timeStr) => {
  if (!timeStr) return ''
  const now = new Date()
  const time = new Date(timeStr)
  const diffMinutes = Math.floor((now - time) / (1000 * 60))

  if (diffMinutes < 1) return '刚刚'
  if (diffMinutes < 60) return `${diffMinutes}分钟前`
  if (diffMinutes < 1440) return `${Math.floor(diffMinutes / 60)}小时前`
  return `${Math.floor(diffMinutes / 1440)}天前`
}

// 获取仪表板分析数据
const fetchDashboardData = async () => {
  try {
    const response = await getDashboardAnalysisApi()

    if (response.code === 200) {
      const data = response.data

      // 保存当前数据到历史数据
      previousMetrics.value = { ...todayMetrics.value }

      // 更新响应式数据
      currentTime.value = data.currentTime
      healthLevel.value = data.healthLevel
      lastHourPosts.value = data.lastHourPosts
      todayMetrics.value = data.todayMetrics
      insights.value = data.insights || []
      healthScore.value = data.healthScore
    }
  } catch (error) {
    console.error('获取仪表板分析数据失败:', error)
  }
}

// 组件挂载时获取数据
onMounted(() => {
  fetchDashboardData()

  // 设置定时刷新（每5分钟刷新一次）
  refreshTimer = setInterval(() => {
    fetchDashboardData()
  }, 5 * 60 * 1000)
})

// 组件卸载时清理
onUnmounted(() => {
  if (refreshTimer) {
    clearInterval(refreshTimer)
  }
})
</script>

<style scoped>
.dashboard-analysis {
  padding: 20px;
}

.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: white;
}

.time-info, .health-status, .health-score {
  text-align: center;
  flex: 1;
}

.current-time {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.time-label, .health-label, .score-label {
  font-size: 14px;
  opacity: 0.9;
}

.health-level {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
  padding: 4px 16px;
  border-radius: 20px;
  display: inline-block;
}

.health-healthy {
  background-color: rgba(103, 194, 58, 0.2);
  color: #67C23A;
}

.health-warning {
  background-color: rgba(230, 162, 60, 0.2);
  color: #E6A23C;
}

.health-danger {
  background-color: rgba(245, 108, 108, 0.2);
  color: #F56C6C;
}

.health-unknown {
  background-color: rgba(144, 147, 153, 0.2);
  color: #909399;
}

.score-value {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 8px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 20px;
}

.metric-card {
  transition: transform 0.3s ease;
}

.metric-card:hover {
  transform: translateY(-5px);
}

.metric-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.metric-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  margin-right: 20px;
  color: white;
}

.metric-info {
  flex: 1;
}

.metric-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.metric-label {
  font-size: 14px;
  color: #909399;
}

.metric-trend {
  margin-left: 10px;
}

.trend-indicator {
  font-size: 24px;
  font-weight: bold;
}

.trend-up {
  color: #67C23A;
}

.trend-down {
  color: #F56C6C;
}

.trend-stable {
  color: #909399;
}

.score-progress {
  width: 80px;
}

.insights-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.insight-item {
  display: flex;
  align-items: center;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.insight-item:hover {
  background-color: #f8f9fa;
  border-color: #409EFF;
}

.insight-icon {
  font-size: 24px;
  margin-right: 15px;
}

.insight-content {
  flex: 1;
}

.insight-title {
  font-weight: bold;
  margin-bottom: 4px;
  color: #303133;
}

.insight-description {
  font-size: 14px;
  color: #606266;
}

.insight-time {
  font-size: 12px;
  color: #909399;
}

.no-insights {
  text-align: center;
  padding: 40px 20px;
}

.status-explanation {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
}

.status-item {
  display: flex;
  align-items: center;
  flex: 1;
  min-width: 200px;
}

.status-indicator {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  margin-right: 12px;
}

.status-healthy {
  background-color: #67C23A;
}

.status-warning {
  background-color: #E6A23C;
}

.status-danger {
  background-color: #F56C6C;
}

.status-info {
  flex: 1;
}

.status-title {
  font-weight: bold;
  margin-bottom: 4px;
  color: #303133;
}

.status-desc {
  font-size: 12px;
  color: #909399;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 20px;
    text-align: center;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .status-explanation {
    flex-direction: column;
  }
}
</style>
