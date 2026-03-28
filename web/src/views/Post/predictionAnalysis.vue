<template>
  <div class="prediction-analysis">
    <!-- 预测活跃用户数 -->
    <div class="prediction-summary">
      <el-card shadow="hover" style="margin-bottom: 20px;">
        <div style="display: flex; align-items: center; justify-content: space-between;">
          <div>
            <div style="font-size: 14px; color: #909399;">预测活跃用户数</div>
            <div style="font-size: 32px; font-weight: bold; margin-top: 8px; color: #409EFF;">
              {{ predictedActiveUsers }}
            </div>
            <div style="font-size: 12px; color: #909399; margin-top: 4px;">
              未来7天平均日活跃用户
            </div>
          </div>
          <div style="font-size: 40px; color: #409EFF;">
            👥
          </div>
        </div>
      </el-card>
    </div>

    <!-- 帖子数量预测 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>帖子数量预测（未来7天）</span>
        </div>
      </template>
      <div ref="predictionChartRef" style="width: 100%; height: 400px;"></div>
    </el-card>

    <div class="charts-row">
      <!-- 高峰时段分析 -->
      <el-card shadow="hover" style="flex: 1; margin-right: 10px;">
        <template #header>
          <div class="card-header">
            <span>高峰时段分析</span>
          </div>
        </template>
        <div class="peak-hours">
          <div v-for="(item, index) in peakHours" :key="item.hour" class="peak-hour-card">
            <div class="peak-rank">
              <div class="rank-badge" :class="getRankClass(index)">
                {{ index + 1 }}
              </div>
            </div>
            <div class="peak-info">
              <div class="peak-time">{{ formatHour(item.hour) }}</div>
              <div class="peak-count">{{ item.count }} 帖</div>
            </div>
            <div class="peak-bar">
              <div
                class="bar-fill"
                :style="{ width: calculateBarWidth(item.count) + '%' }"
                :class="getRankColor(index)"
              ></div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 预测数据表格 -->
      <el-card shadow="hover" style="flex: 1; margin-left: 10px;">
        <template #header>
          <div class="card-header">
            <span>详细预测数据</span>
          </div>
        </template>
        <el-table :data="postPredictions" stripe style="width: 100%; height: 300px;">
          <el-table-column prop="date" label="日期" width="120" />
          <el-table-column prop="predicted_count" label="预测发帖数" width="120" sortable>
            <template #default="{ row }">
              <el-tag :type="getCountTagType(row.predicted_count)">
                {{ row.predicted_count }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="confidence" label="置信度" width="100">
            <template #default="{ row }">
              <el-tag :type="getConfidenceTagType(row.confidence)" effect="dark">
                {{ row.confidence }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="趋势" width="100">
            <template #default="{ row }">
              <div style="display: flex; align-items: center;">
                <span style="margin-right: 5px;">→</span>
                <span style="color: #909399;">平稳</span>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </div>

    <!-- 趋势分类（如果有数据） -->
    <el-card v-if="trendingCategories.length > 0" shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>趋势分类预测</span>
        </div>
      </template>
      <div class="trending-categories">
        <div v-for="category in trendingCategories" :key="category.name" class="category-card">
          <div class="category-name">{{ category.name }}</div>
          <div class="category-growth">
            <span :class="category.growth >= 0 ? 'growth-up' : 'growth-down'">
              {{ category.growth >= 0 ? '+' : '' }}{{ category.growth }}%
            </span>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 预测说明 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>预测说明</span>
        </div>
      </template>
      <div style="color: #606266; line-height: 1.6;">
        <p>📊 <strong>数据说明：</strong></p>
        <ul>
          <li>预测基于历史发帖数据进行时间序列分析</li>
          <li>置信度反映预测的可靠性（高/中/低）</li>
          <li>高峰时段基于历史活跃时间分布计算</li>
          <li>活跃用户数预测为未来7天平均值</li>
        </ul>
        <p style="margin-top: 10px; color: #909399; font-size: 12px;">
          注：预测数据仅供参考，实际数据可能受多种因素影响
        </p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getPredictionAnalysisApi } from '@/api/postApi.js'

// 响应式数据
const postPredictions = ref([])
const peakHours = ref([])
const predictedActiveUsers = ref(0)
const trendingCategories = ref([])

// 图表引用
const predictionChartRef = ref(null)

// 图表实例
let predictionChart = null

// 格式化小时显示
const formatHour = (hour) => {
  return `${hour}:00 - ${hour + 1}:00`
}

// 计算柱状图宽度
const calculateBarWidth = (count) => {
  if (peakHours.value.length === 0) return 0
  const maxCount = Math.max(...peakHours.value.map(item => item.count))
  return (count / maxCount) * 100
}

// 获取排名样式
const getRankClass = (index) => {
  switch (index) {
    case 0: return 'rank-first'
    case 1: return 'rank-second'
    case 2: return 'rank-third'
    default: return ''
  }
}

// 获取排名颜色
const getRankColor = (index) => {
  switch (index) {
    case 0: return 'bar-first'
    case 1: return 'bar-second'
    case 2: return 'bar-third'
    default: return ''
  }
}

// 获取数量标签类型
const getCountTagType = (count) => {
  if (count >= 300) return 'danger'
  if (count >= 250) return 'warning'
  if (count >= 200) return 'primary'
  return 'info'
}

// 获取置信度标签类型
const getConfidenceTagType = (confidence) => {
  switch (confidence) {
    case '高': return 'success'
    case '中': return 'warning'
    case '低': return 'danger'
    default: return 'info'
  }
}

// 获取预测分析数据
const fetchPredictionData = async () => {
  try {
    const response = await getPredictionAnalysisApi()

    if (response.code === 200) {
      const data = response.data
      postPredictions.value = data.postPredictions
      peakHours.value = data.peakHours
      predictedActiveUsers.value = data.predictedActiveUsers
      trendingCategories.value = data.trendingCategories || []

      // 渲染预测图表
      renderPredictionChart(data.postPredictions)
    }
  } catch (error) {
    console.error('获取预测分析数据失败:', error)
  }
}

// 渲染预测图表
const renderPredictionChart = (data) => {
  if (!predictionChartRef.value) return

  if (predictionChart) {
    predictionChart.dispose()
  }

  predictionChart = echarts.init(predictionChartRef.value)

  const dates = data.map(item => item.date)
  const predictedCounts = data.map(item => item.predicted_count)
  const confidenceLevels = data.map(item => item.confidence)

  // 根据置信度设置颜色
  const colors = confidenceLevels.map(level => {
    switch (level) {
      case '高': return '#67C23A'
      case '中': return '#E6A23C'
      case '低': return '#F56C6C'
      default: return '#909399'
    }
  })

  const option = {
    tooltip: {
      trigger: 'axis',
      formatter: function(params) {
        const dataIndex = params[0].dataIndex
        return `
          ${dates[dataIndex]}<br/>
          预测发帖数: ${predictedCounts[dataIndex]}<br/>
          置信度: ${confidenceLevels[dataIndex]}
        `
      }
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
      name: '发帖数'
    },
    series: [
      {
        name: '预测发帖数',
        type: 'bar',
        data: predictedCounts.map((count, index) => ({
          value: count,
          itemStyle: {
            color: colors[index]
          }
        })),
        markLine: {
          data: [
            {
              type: 'average',
              name: '平均值',
              label: {
                formatter: '平均: {c}'
              }
            }
          ]
        }
      },
      {
        name: '趋势线',
        type: 'line',
        data: predictedCounts,
        smooth: true,
        itemStyle: {
          color: '#409EFF'
        },
        lineStyle: {
          width: 3,
          type: 'dashed'
        }
      }
    ],
    grid: {
      left: '3%',
      right: '4%',
      bottom: '15%',
      top: '10%',
      containLabel: true
    },
    legend: {
      data: ['预测发帖数', '趋势线']
    }
  }

  predictionChart.setOption(option)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchPredictionData()

  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (predictionChart) predictionChart.resize()
  })
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (predictionChart) predictionChart.dispose()
  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.prediction-analysis {
  padding: 20px;
}

.charts-row {
  display: flex;
  margin-bottom: 20px;
}

.peak-hours {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.peak-hour-card {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 8px;
}

.peak-rank {
  margin-right: 15px;
}

.rank-badge {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  color: white;
}

.rank-first {
  background: linear-gradient(135deg, #FFD700, #FFA500);
}

.rank-second {
  background: linear-gradient(135deg, #C0C0C0, #A9A9A9);
}

.rank-third {
  background: linear-gradient(135deg, #CD7F32, #8B4513);
}

.peak-info {
  flex: 1;
}

.peak-time {
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.peak-count {
  font-size: 12px;
  color: #909399;
}

.peak-bar {
  width: 150px;
  height: 8px;
  background-color: #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 1s ease-in-out;
}

.bar-first {
  background: linear-gradient(90deg, #FFD700, #FFA500);
}

.bar-second {
  background: linear-gradient(90deg, #C0C0C0, #A9A9A9);
}

.bar-third {
  background: linear-gradient(90deg, #CD7F32, #8B4513);
}

.trending-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}

.category-card {
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  text-align: center;
  min-width: 120px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.category-name {
  font-weight: bold;
  margin-bottom: 8px;
  color: #303133;
}

.category-growth {
  font-size: 18px;
  font-weight: bold;
}

.growth-up {
  color: #67C23A;
}

.growth-down {
  color: #F56C6C;
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

  .peak-bar {
    width: 100px;
  }
}
</style>
