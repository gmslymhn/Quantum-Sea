<template>
  <div class="content-quality-analysis">
    <!-- 分类详细数据 -->
    <el-card shadow="hover" style="margin-top: 20px;">
      <template #header>
        <div class="card-header">
          <span>分类详细数据</span>
        </div>
      </template>
      <el-table :data="categoryQuality" stripe style="width: 100%">
        <el-table-column prop="cate_name" label="分类" width="120" />
        <el-table-column prop="post_count" label="帖子数" width="100" sortable />
        <el-table-column prop="avg_quality_score" label="平均质量分" width="120" sortable>
          <template #default="{ row }">
            {{ row.avg_quality_score.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="avg_views" label="平均浏览" width="120" sortable>
          <template #default="{ row }">
            {{ row.avg_views.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="avg_likes" label="平均点赞" width="120" sortable>
          <template #default="{ row }">
            {{ row.avg_likes.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="avg_comments" label="平均评论" width="120" sortable>
          <template #default="{ row }">
            {{ row.avg_comments.toFixed(2) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <div class="charts-row">
      <!-- 分类质量分析 -->
      <el-card shadow="hover" style="flex: 1; margin-right: 10px;">
        <template #header>
          <div class="card-header">
            <span>分类质量分析</span>
          </div>
        </template>
        <div ref="categoryChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>

      <!-- 图片使用分析 -->
      <el-card shadow="hover" style="flex: 1; margin-left: 10px;">
        <template #header>
          <div class="card-header">
            <span>图片使用分析</span>
          </div>
        </template>
        <div ref="imageChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>

    <div class="charts-row" style="margin-top: 20px;">
      <!-- 标题长度分析 -->
      <el-card shadow="hover" style="flex: 1; margin-right: 10px;">
        <template #header>
          <div class="card-header">
            <span>标题长度分析</span>
          </div>
        </template>
        <div ref="titleChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>

      <!-- 内容长度分布 -->
      <el-card shadow="hover" style="flex: 1; margin-left: 10px;">
        <template #header>
          <div class="card-header">
            <span>内容长度分布</span>
          </div>
        </template>
        <div ref="contentChartRef" style="width: 100%; height: 300px;"></div>
      </el-card>
    </div>

    <!-- 高质量帖子排行榜 -->
    <el-card shadow="hover" style="margin-bottom: 20px;">
      <template #header>
        <div class="card-header">
          <span>高质量帖子排行榜</span>
        </div>
      </template>
      <el-table :data="qualityPosts" stripe style="width: 100%">
        <el-table-column prop="title" label="帖子标题" width="250">
          <template #default="{ row }">
            <div class="post-title" :title="row.title">{{ row.title }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="nickname" label="作者" width="120" />
        <el-table-column prop="cate_name" label="分类" width="100" />
        <el-table-column prop="quality_score" label="质量分" width="100" sortable>
          <template #default="{ row }">
            {{ row.quality_score.toFixed(1) }}
          </template>
        </el-table-column>
        <el-table-column prop="view_count" label="浏览数" width="100" sortable>
          <template #default="{ row }">
            {{ row.view_count.toLocaleString() }}
          </template>
        </el-table-column>
        <el-table-column prop="c_count" label="评论数" width="100" sortable />
        <el-table-column prop="l_count" label="点赞数" width="100" sortable />
        <el-table-column label="操作" width="80">
          <template #default="{ row }">
            <el-button type="primary" link @click="viewPostDetail(row.thread_id)">
              查看
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { getContentQualityAnalysisApi } from '@/api/postApi.js'

// 响应式数据
const qualityPosts = ref([])
const categoryQuality = ref([])
const imageUsage = ref([])
const titleAnalysis = ref([])
const contentLength = ref([])

// 图表引用
const categoryChartRef = ref(null)
const imageChartRef = ref(null)
const titleChartRef = ref(null)
const contentChartRef = ref(null)

// 图表实例
let categoryChart = null
let imageChart = null
let titleChart = null
let contentChart = null

// 查看帖子详情
const viewPostDetail = (threadId) => {
  // 这里可以根据实际需求实现跳转或弹窗显示详情
  console.log('查看帖子详情:', threadId)
  // 示例：window.open(`/post/${threadId}`)
}

// 获取内容质量分析数据
const fetchContentQualityData = async () => {
  try {
    const response = await getContentQualityAnalysisApi()

    if (response.code === 200) {
      const data = response.data
      qualityPosts.value = data.qualityPosts
      categoryQuality.value = data.categoryQuality
      imageUsage.value = data.imageUsage
      titleAnalysis.value = data.titleAnalysis
      contentLength.value = data.contentLength

      // 渲染图表
      renderCategoryChart(data.categoryQuality)
      renderImageChart(data.imageUsage)
      renderTitleChart(data.titleAnalysis)
      renderContentChart(data.contentLength)
    }
  } catch (error) {
    console.error('获取内容质量分析数据失败:', error)
  }
}

// 渲染分类质量分析图
const renderCategoryChart = (data) => {
  if (!categoryChartRef.value) return

  if (categoryChart) {
    categoryChart.dispose()
  }

  categoryChart = echarts.init(categoryChartRef.value)

  const categories = data.map(item => item.cate_name)
  const qualityScores = data.map(item => item.avg_quality_score)
  const postCounts = data.map(item => item.post_count)

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
      data: ['平均质量分', '帖子数']
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
        name: '平均质量分',
        min: 0,
        axisLabel: {
          formatter: '{value}'
        }
      },
      {
        type: 'value',
        name: '帖子数',
        min: 0,
        axisLabel: {
          formatter: '{value}'
        }
      }
    ],
    series: [
      {
        name: '平均质量分',
        type: 'bar',
        yAxisIndex: 0,
        data: qualityScores,
        itemStyle: {
          color: '#5470c6'
        }
      },
      {
        name: '帖子数',
        type: 'line',
        yAxisIndex: 1,
        data: postCounts,
        smooth: true,
        itemStyle: {
          color: '#91cc75'
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

  categoryChart.setOption(option)
}

// 渲染图片使用分析图
const renderImageChart = (data) => {
  if (!imageChartRef.value) return

  if (imageChart) {
    imageChart.dispose()
  }

  imageChart = echarts.init(imageChartRef.value)

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
        name: '图片使用',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['60%', '50%'],
        data: data.map((item, index) => ({
          name: item.has_images,
          value: item.post_count,
          itemStyle: {
            color: index === 0 ? '#ee6666' : '#73c0de'
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

  imageChart.setOption(option)
}

// 渲染标题长度分析图
const renderTitleChart = (data) => {
  if (!titleChartRef.value) return

  if (titleChart) {
    titleChart.dispose()
  }

  titleChart = echarts.init(titleChartRef.value)

  const categories = data.map(item => item.title_length_range)
  const avgViews = data.map(item => item.avg_views)
  const avgComments = data.map(item => item.avg_comments)
  const avgLikes = data.map(item => item.avg_likes)

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
      data: ['平均浏览', '平均评论', '平均点赞']
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
      name: '数值'
    },
    series: [
      {
        name: '平均浏览',
        type: 'bar',
        data: avgViews,
        itemStyle: {
          color: '#5470c6'
        }
      },
      {
        name: '平均评论',
        type: 'bar',
        data: avgComments,
        itemStyle: {
          color: '#91cc75'
        }
      },
      {
        name: '平均点赞',
        type: 'bar',
        data: avgLikes,
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

  titleChart.setOption(option)
}

// 渲染内容长度分布图
const renderContentChart = (data) => {
  if (!contentChartRef.value) return

  if (contentChart) {
    contentChart.dispose()
  }

  contentChart = echarts.init(contentChartRef.value)

  const categories = data.map(item => item.content_length_range)
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

  contentChart.setOption(option)
}

// 组件挂载时获取数据
onMounted(() => {
  fetchContentQualityData()

  // 监听窗口大小变化，重新渲染图表
  window.addEventListener('resize', () => {
    if (categoryChart) categoryChart.resize()
    if (imageChart) imageChart.resize()
    if (titleChart) titleChart.resize()
    if (contentChart) contentChart.resize()
  })
})

// 组件卸载时清理
import { onUnmounted } from 'vue'
onUnmounted(() => {
  if (categoryChart) categoryChart.dispose()
  if (imageChart) imageChart.dispose()
  if (titleChart) titleChart.dispose()
  if (contentChart) contentChart.dispose()

  window.removeEventListener('resize', () => {})
})
</script>

<style scoped>
.content-quality-analysis {
  padding: 20px;
}

.charts-row {
  display: flex;
  margin-bottom: 20px;
}

.post-title {
  max-width: 250px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  cursor: help;
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
}
</style>
