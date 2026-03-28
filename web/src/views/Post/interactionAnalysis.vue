<template>
  <div class="interaction-analysis">
    <!-- 高互动帖子分布 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>高互动帖子分布（按分类）</span>
        </div>
      </template>
      <div ref="highInteractionChartRef" style="width: 100%; height: 400px;"></div>
    </el-card>

    <!-- 互动相关性分析 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>互动相关性分析</span>
        </div>
      </template>
      <div class="correlation-cards">
        <div class="correlation-card">
          <div class="correlation-label">浏览-评论相关性</div>
          <div class="correlation-value">{{ (interactionCorrelation.view_comment_corr * 100).toFixed(2) }}%</div>
          <div class="correlation-bar">
            <div class="bar-fill" :style="{ width: (interactionCorrelation.view_comment_corr * 100) + '%' }"></div>
          </div>
        </div>
        <div class="correlation-card">
          <div class="correlation-label">浏览-点赞相关性</div>
          <div class="correlation-value">{{ (interactionCorrelation.view_like_corr * 100).toFixed(2) }}%</div>
          <div class="correlation-bar">
            <div class="bar-fill" :style="{ width: (interactionCorrelation.view_like_corr * 100) + '%' }"></div>
          </div>
        </div>
        <div class="correlation-card">
          <div class="correlation-label">评论-点赞相关性</div>
          <div class="correlation-value">{{ (interactionCorrelation.comment_like_corr * 100).toFixed(2) }}%</div>
          <div class="correlation-bar">
            <div class="bar-fill" :style="{ width: (interactionCorrelation.comment_like_corr * 100) + '%' }"></div>
          </div>
        </div>
      </div>
    </el-card>

    <div class="charts-row">
      <!-- 热度分布 -->
      <el-card shadow="hover" style="flex: 1; margin-right: 10px;">
        <template #header>
          <div class="card-header">
            <span>帖子热度分布</span>
          </div>
        </template>
        <div ref="heatChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>

      <!-- 点赞率分布 -->
      <el-card shadow="hover" style="flex: 1; margin-left: 10px;">
        <template #header>
          <div class="card-header">
            <span>点赞率分布</span>
          </div>
        </template>
        <div ref="likeRateChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>

    <!-- 评论率分布 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>评论率分布</span>
        </div>
      </template>
      <div ref="commentRateChartRef" style="width: 100%; height: 350px;"></div>
    </el-card>

    <!-- 高互动分类详细数据 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>高互动分类详细数据</span>
        </div>
      </template>
      <el-table :data="highInteraction" stripe style="width: 100%">
        <el-table-column prop="cate_name" label="分类" width="120" />
        <el-table-column prop="high_view_posts" label="高浏览帖子数" width="120" sortable>
          <template #default="{ row }">
            <el-tag type="info">{{ row.high_view_posts }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="high_like_posts" label="高点赞帖子数" width="120" sortable>
          <template #default="{ row }">
            <el-tag type="success">{{ row.high_like_posts }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="high_comment_posts" label="高评论帖子数" width="120" sortable>
          <template #default="{ row }">
            <el-tag type="warning">{{ row.high_comment_posts }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="总高互动帖子数" width="140" sortable>
          <template #default="{ row }">
            <el-tag type="danger">
              {{ row.high_view_posts + row.high_like_posts + row.high_comment_posts }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getInteractionAnalysisApi } from '@/api/postApi.js'

// 响应式数据
const highInteraction = ref([])
const interactionCorrelation = ref({
  view_comment_corr: 0,
  view_like_corr: 0,
  comment_like_corr: 0
})
const heatDistribution = ref([])
const likeRate = ref([])
const commentRate = ref([])

// 图表引用
const highInteractionChartRef = ref(null)
const heatChartRef = ref(null)
const likeRateChartRef = ref(null)
const commentRateChartRef = ref(null)

// 图表实例
let highInteractionChart = null
let heatChart = null
let likeRateChart = null
let commentRateChart = null

// 获取互动分析数据
const fetchInteractionData = async () => {
  try {
    const response = await getInteractionAnalysisApi()

    if (response.code === 200) {
      const data = response.data
      highInteraction.value = data.highInteraction
      interactionCorrelation.value = data.interactionCorrelation
      heatDistribution.value = data.heatDistribution
      likeRate.value = data.likeRate
      commentRate.value = data.commentRate

      // 渲染图表
      renderHighInteractionChart(data.highInteraction)
      renderHeatChart(data.heatDistribution)
      renderLikeRateChart(data.likeRate)
      renderCommentRateChart(data.commentRate)
    }
  } catch (error) {
    console.error('获取互动分析数据失败:', error)
  }
}

// 渲染高互动帖子分布图
const renderHighInteractionChart = (data) => {
  if (!highInteractionChartRef.value) return

  if (highInteractionChart) {
    highInteractionChart.dispose()
  }

  highInteractionChart = echarts.init(highInteractionChartRef.value)

  const categories = data.map(item => item.cate_name)
  const highViews = data.map(item => item.high_view_posts)
  const highLikes = data.map(item => item.high_like_posts)
  const highComments = data.map(item => item.high_comment_posts)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: '#999'
        }
      }
    },
    legend: {
      data: ['高浏览帖子', '高点赞帖子', '高评论帖子']
    },
    xAxis: [
      {
        type: 'category',
        data: categories,
        axisPointer: {
          type: 'shadow'
        },
        axisLabel: {
          rotate: 45
        }
      }
    ],
    yAxis: [
      {
        type: 'value',
        name: '帖子数',
        min: 0
      }
    ],
    series: [
      {
        name: '高浏览帖子',
        type: 'bar',
        data: highViews,
        itemStyle: {
          color: '#5470c6'
        }
      },
      {
        name: '高点赞帖子',
        type: 'bar',
        data: highLikes,
        itemStyle: {
          color: '#91cc75'
        }
      },
      {
        name: '高评论帖子',
        type: 'bar',
        data: highComments,
        itemStyle: {
          color: '#fac858'
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '15%',
      containLabel: true
    }
  }

  highInteractionChart.setOption(option)
}

// 渲染热度分布图
const renderHeatChart = (data) => {
  if (!heatChartRef.value) return

  if (heatChart) {
    heatChart.dispose()
  }

  heatChart = echarts.init(heatChartRef.value)

  const categories = data.map(item => item.heat_level)
  const postCounts = data.map(item => item.post_count)

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}<br/>帖子数: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      left: 'left',
      top: 'center'
    },
    series: [
      {
        name: '热度分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: data.map((item, index) => ({
          name: item.heat_level,
          value: item.post_count,
          itemStyle: {
            color: [
              '#91cc75',  // 低热度 - 绿色
              '#fac858',  // 中热度 - 黄色
              '#ee6666',  // 较高热度 - 红色
              '#c23531'   // 高热度 - 深红色
            ][index]
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

  heatChart.setOption(option)
}

// 渲染点赞率分布图
const renderLikeRateChart = (data) => {
  if (!likeRateChartRef.value) return

  if (likeRateChart) {
    likeRateChart.dispose()
  }

  likeRateChart = echarts.init(likeRateChartRef.value)

  const categories = data.map(item => item.like_rate_range)
  const postCounts = data.map(item => item.post_count)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>帖子数: {c}'
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '帖子数'
    },
    series: [
      {
        name: '帖子数',
        type: 'bar',
        data: postCounts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#83bff6' },
            { offset: 0.5, color: '#188df0' },
            { offset: 1, color: '#188df0' }
          ])
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    }
  }

  likeRateChart.setOption(option)
}

// 渲染评论率分布图
const renderCommentRateChart = (data) => {
  if (!commentRateChartRef.value) return

  if (commentRateChart) {
    commentRateChart.dispose()
  }

  commentRateChart = echarts.init(commentRateChartRef.value)

  const categories = data.map(item => item.comment_rate_range)
  const postCounts = data.map(item => item.post_count)

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: '{b}<br/>帖子数: {c}'
    },
    xAxis: {
      type: 'category',
      data: categories,
      axisLabel: {
        rotate: 45
      }
    },
    yAxis: {
      type: 'value',
      name: '帖子数'
    },
    series: [
      {
        name: '帖子数',
        type: 'bar',
        data: postCounts,
        itemStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: '#73c0de' },
            { offset: 0.5, color: '#3ba272' },
            { offset: 1, color: '#3ba272' }
          ])
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    }
  }

  commentRateChart.setOption(option)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchInteractionData()

  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (highInteractionChart) highInteractionChart.resize()
    if (heatChart) heatChart.resize()
    if (likeRateChart) likeRateChart.resize()
    if (commentRateChart) commentRateChart.resize()
  })
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (highInteractionChart) highInteractionChart.dispose()
  if (heatChart) heatChart.dispose()
  if (likeRateChart) likeRateChart.dispose()
  if (commentRateChart) commentRateChart.dispose()

  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.interaction-analysis {
  padding: 20px;
}

.charts-row {
  display: flex;
  margin-bottom: 20px;
}

.correlation-cards {
  display: flex;
  justify-content: space-around;
  flex-wrap: wrap;
  gap: 20px;
}

.correlation-card {
  flex: 1;
  min-width: 200px;
  padding: 20px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  text-align: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.correlation-label {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.correlation-value {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 15px;
}

.correlation-bar {
  height: 8px;
  background-color: #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #409EFF, #67C23A);
  border-radius: 4px;
  transition: width 1s ease-in-out;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

@media (max-width: 768px) {
  .charts-row {
    flex-direction: column;
  }

  .charts-row > * {
    margin: 0 0 20px 0 !important;
  }

  .correlation-cards {
    flex-direction: column;
  }
}
</style>
