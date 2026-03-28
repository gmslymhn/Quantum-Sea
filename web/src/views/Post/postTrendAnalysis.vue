<template>
  <div class="post-trend-analysis">
    <!-- 天数选择器 -->
    <div class="days-filter">
      <el-select
        v-model="selectedDays"
        placeholder="选择天数"
        @change="handleDaysChange"
        style="width: 200px; margin-bottom: 20px;"
      >
        <el-option label="最近3天" :value="3" />
        <el-option label="最近7天" :value="7" />
        <el-option label="最近15天" :value="15" />
        <el-option label="最近30天" :value="30" />
      </el-select>
    </div>

    <!-- 增长率卡片 -->
    <div class="growth-rate-card" style="margin-bottom: 20px;">
      <el-card shadow="hover">
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <div>
            <div style="font-size: 14px; color: #909399;">帖子增长率</div>
            <div style="font-size: 24px; font-weight: bold; margin-top: 8px;">
              {{ growthRate }}%
            </div>
          </div>
          <div v-if="growthRate >= 0" style="color: #67c23a; font-size: 40px;">
            ↑
          </div>
          <div v-else style="color: #f56c6c; font-size: 40px;">
            ↓
          </div>
        </div>
      </el-card>
    </div>

    <!-- 图表容器 -->
    <div class="charts-container">
      <!-- 帖子趋势图 -->
      <el-card shadow="hover" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>帖子发布趋势</span>
          </div>
        </template>
        <div ref="postChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>

      <!-- 点赞趋势图 -->
      <el-card shadow="hover" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>点赞趋势</span>
          </div>
        </template>
        <div ref="likeChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>

      <!-- 浏览趋势图 -->
      <el-card shadow="hover" style="margin-bottom: 20px;">
        <template #header>
          <div class="card-header">
            <span>浏览趋势</span>
          </div>
        </template>
        <div ref="viewChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>

      <!-- 评论趋势图 -->
      <el-card shadow="hover">
        <template #header>
          <div class="card-header">
            <span>评论趋势</span>
          </div>
        </template>
        <div ref="commentChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getPostTrendAnalysisApi } from '@/api/postApi.js'

// 响应式数据
const selectedDays = ref(7) // 默认选择最近7天
const growthRate = ref('0.00')

// 图表引用
const postChartRef = ref(null)
const likeChartRef = ref(null)
const viewChartRef = ref(null)
const commentChartRef = ref(null)

// 图表实例
let postChart = null
let likeChart = null
let viewChart = null
let commentChart = null

// 处理天数变化
const handleDaysChange = () => {
  fetchPostTrendData()
}

// 获取帖子趋势数据
const fetchPostTrendData = async () => {
  try {
    const response = await getPostTrendAnalysisApi({
      days: selectedDays.value
    })

    if (response.code === 200) {
      const data = response.data
      growthRate.value = data.growthRate

      // 渲染图表
      renderPostChart(data.postTrend)
      renderLikeChart(data.likeTrend)
      renderViewChart(data.viewTrend)
      renderCommentChart(data.commentTrend)
    }
  } catch (error) {
    console.error('获取帖子趋势数据失败:', error)
  }
}

// 渲染帖子趋势图
const renderPostChart = (data) => {
  if (!postChartRef.value) return

  if (postChart) {
    postChart.dispose()
  }

  postChart = echarts.init(postChartRef.value)

  const dates = data.map(item => item.date)
  const counts = data.map(item => item.count)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '帖子数量'
    },
    series: [{
      name: '帖子数量',
      type: 'line',
      data: counts,
      smooth: true,
      itemStyle: {
        color: '#409EFF'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(64, 158, 255, 0.3)' },
          { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
        ])
      }
    }],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    }
  }

  postChart.setOption(option)
}

// 渲染点赞趋势图
const renderLikeChart = (data) => {
  if (!likeChartRef.value) return

  if (likeChart) {
    likeChart.dispose()
  }

  likeChart = echarts.init(likeChartRef.value)

  const dates = data.map(item => item.date)
  const likes = data.map(item => item.total_likes)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '点赞数'
    },
    series: [{
      name: '点赞数',
      type: 'line',
      data: likes,
      smooth: true,
      itemStyle: {
        color: '#67C23A'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(103, 194, 58, 0.3)' },
          { offset: 1, color: 'rgba(103, 194, 58, 0.1)' }
        ])
      }
    }],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    }
  }

  likeChart.setOption(option)
}

// 渲染浏览趋势图
const renderViewChart = (data) => {
  if (!viewChartRef.value) return

  if (viewChart) {
    viewChart.dispose()
  }

  viewChart = echarts.init(viewChartRef.value)

  const dates = data.map(item => item.date)
  const views = data.map(item => item.total_views)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '浏览数'
    },
    series: [{
      name: '浏览数',
      type: 'line',
      data: views,
      smooth: true,
      itemStyle: {
        color: '#E6A23C'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(230, 162, 60, 0.3)' },
          { offset: 1, color: 'rgba(230, 162, 60, 0.1)' }
        ])
      }
    }],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    }
  }

  viewChart.setOption(option)
}

// 渲染评论趋势图
const renderCommentChart = (data) => {
  if (!commentChartRef.value) return

  if (commentChart) {
    commentChart.dispose()
  }

  commentChart = echarts.init(commentChartRef.value)

  const dates = data.map(item => item.date)
  const comments = data.map(item => item.total_comments)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>{a}: {c}'
    },
    xAxis: {
      type: 'category',
      data: dates,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '评论数'
    },
    series: [{
      name: '评论数',
      type: 'line',
      data: comments,
      smooth: true,
      itemStyle: {
        color: '#F56C6C'
      },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(245, 108, 108, 0.3)' },
          { offset: 1, color: 'rgba(245, 108, 108, 0.1)' }
        ])
      }
    }],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    }
  }

  commentChart.setOption(option)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchPostTrendData()

  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (postChart) postChart.resize()
    if (likeChart) likeChart.resize()
    if (viewChart) viewChart.resize()
    if (commentChart) commentChart.resize()
  })
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (postChart) postChart.dispose()
  if (likeChart) likeChart.dispose()
  if (viewChart) viewChart.dispose()
  if (commentChart) commentChart.dispose()

  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.post-trend-analysis {
  padding: 20px;
}

.charts-container {
  display: grid;
  grid-template-columns: 1fr;
  gap: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (min-width: 1200px) {
  .charts-container {
    grid-template-columns: 1fr 1fr;
  }
}
</style>
