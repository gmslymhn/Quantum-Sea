<template>
  <div class="user-behavior-analysis">

    <div class="charts-row">
      <!-- 用户活跃时间分布 -->
      <el-card shadow="hover" style="flex: 1; margin-right: 10px;">
        <template #header>
          <div class="card-header">
            <span>用户活跃时间分布</span>
          </div>
        </template>
        <div ref="activeTimeChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>

      <!-- 用户发帖量分布 -->
      <el-card shadow="hover" style="flex: 1; margin-left: 10px;">
        <template #header>
          <div class="card-header">
            <span>用户发帖量分布</span>
          </div>
        </template>
        <div ref="postDistributionChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>

    <!-- 用户年龄分析 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>用户年龄分析</span>
        </div>
      </template>
      <div class="user-age-analysis">
        <div v-for="item in userAgeAnalysis" :key="item.user_type" class="age-card">
          <div class="age-type">{{ item.user_type }}</div>
          <div class="age-stats">
            <div class="stat-item">
              <div class="stat-label">用户数</div>
              <div class="stat-value">{{ item.user_count.toLocaleString() }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">总发帖</div>
              <div class="stat-value">{{ item.total_posts.toLocaleString() }}</div>
            </div>
            <div class="stat-item">
              <div class="stat-label">人均发帖</div>
              <div class="stat-value">{{ item.avg_posts.toFixed(1) }}</div>
            </div>
          </div>
        </div>
      </div>
    </el-card>
    <!-- 用户互动排行榜 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>用户互动排行榜</span>
        </div>
      </template>
      <el-table :data="userInteractions" stripe style="width: 100%">
        <el-table-column prop="nickname" label="用户昵称" width="150" />
        <el-table-column prop="post_count" label="发帖数" width="100" sortable />
        <el-table-column prop="total_views" label="总浏览" width="120" sortable>
          <template #default="{ row }">
            {{ row.total_views.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="total_likes" label="总点赞" width="100" sortable />
        <el-table-column prop="total_comments" label="总评论" width="100" sortable />
        <el-table-column prop="avg_views" label="均浏览" width="120" sortable>
          <template #default="{ row }">
            {{ row.avg_views.toFixed(0) }}
          </template>
        </el-table-column>
        <el-table-column prop="avg_likes" label="均点赞" width="100" sortable>
          <template #default="{ row }">
            {{ row.avg_likes.toFixed(1) }}
          </template>
        </el-table-column>
        <el-table-column prop="avg_comments" label="均评论" width="100" sortable>
          <template #default="{ row }">
            {{ row.avg_comments.toFixed(1) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getUserBehaviorAnalysisApi } from '@/api/postApi.js'

// 响应式数据
const userInteractions = ref([])
const userAgeAnalysis = ref([])

// 图表引用
const activeTimeChartRef = ref(null)
const postDistributionChartRef = ref(null)

// 图表实例
let activeTimeChart = null
let postDistributionChart = null

// 获取用户行为分析数据
const fetchUserBehaviorData = async () => {
  try {
    const response = await getUserBehaviorAnalysisApi()

    if (response.code === 200) {
      const data = response.data
      userInteractions.value = data.userInteractions
      userAgeAnalysis.value = data.userAgeAnalysis

      // 渲染图表
      renderActiveTimeChart(data.activeTime)
      renderPostDistributionChart(data.postDistribution)
    }
  } catch (error) {
    console.error('获取用户行为分析数据失败:', error)
  }
}

// 渲染活跃时间分布图
const renderActiveTimeChart = (data) => {
  if (!activeTimeChartRef.value) return

  if (activeTimeChart) {
    activeTimeChart.dispose()
  }

  activeTimeChart = echarts.init(activeTimeChartRef.value)

  // 按小时排序
  const sortedData = [...data].sort((a, b) => a.hour - b.hour)
  const hours = sortedData.map(item => `${item.hour}:00`)
  const counts = sortedData.map(item => item.count)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>发帖数: {c}'
    },
    xAxis: {
      type: 'category',
      data: hours,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '发帖数'
    },
    series: [{
      name: '发帖数',
      type: 'bar',
      data: counts,
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#83bff6' },
          { offset: 0.5, color: '#188df0' },
          { offset: 1, color: '#188df0' }
        ])
      },
      emphasis: {
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#2378f7' },
            { offset: 0.7, color: '#2378f7' },
            { offset: 1, color: '#83bff6' }
          ])
        }
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

  activeTimeChart.setOption(option)
}

// 渲染发帖量分布图
const renderPostDistributionChart = (data) => {
  if (!postDistributionChartRef.value) return

  if (postDistributionChart) {
    postDistributionChart.dispose()
  }

  postDistributionChart = echarts.init(postDistributionChartRef.value)

  const categories = data.map(item => item.post_range)
  const userCounts = data.map(item => item.user_count)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>用户数: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [
      {
        name: '发帖量分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: data.map((item, index) => ({
          name: item.post_range,
          value: item.user_count,
          itemStyle: {
            color: [
              '#5470c6',
              '#91cc75',
              '#fac858',
              '#ee6666',
              '#73c0de'
            ][index % 5]
          }
        })),
        emphasis: {
          itemStyle: {
            shadowBlur: 10,
            shadowOffsetX: 0,
            shadowColor: 'rgba(0, 0, 0, 0.5)'
          }
        }
      }
    ]
  }

  postDistributionChart.setOption(option)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchUserBehaviorData()

  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (activeTimeChart) activeTimeChart.resize()
    if (postDistributionChart) postDistributionChart.resize()
  })
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (activeTimeChart) activeTimeChart.dispose()
  if (postDistributionChart) postDistributionChart.dispose()

  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.user-behavior-analysis {
  padding: 20px;
}

.charts-row {
  display: flex;
  margin-bottom: 20px;
}

.user-age-analysis {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
}

.age-card {
  flex: 1;
  min-width: 200px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  text-align: center;
}

.age-type {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #409EFF;
}

.age-stats {
  display: flex;
  justify-content: space-around;
}

.stat-item {
  text-align: center;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.stat-value {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

@media (max-width: 768px) {
  .charts-row {
    flex-direction: column;
  }

  .charts-row > * {
    margin: 0 0 20px 0 !important;
  }

  .user-age-analysis {
    flex-direction: column;
  }
}
</style>
