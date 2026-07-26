<template>
  <div class="sentiment-container p-20">
    <!-- 页面标题 -->
    <div class="page-header mb-30">
      <h1 class="page-title">舆情监测</h1>
      <p class="page-subtitle">实时监控校园热点话题，洞察学生关注焦点</p>
    </div>

    <!-- 统计卡片 -->
    <div class="stats-cards mb-30">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(totalPosts) }}</div>
              <div class="stat-label">热点帖子数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <el-icon><View /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(totalViews) }}</div>
              <div class="stat-label">总浏览数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <el-icon><ChatDotSquare /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(totalComments) }}</div>
              <div class="stat-label">总评论数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <el-icon><Star /></el-icon>
            </div>
            <div class="stat-content">
              <div class="stat-value">{{ formatNumber(totalLikes) }}</div>
              <div class="stat-label">总点赞数</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- 操作栏 -->
    <div class="action-bar mb-20">
      <el-row :gutter="20" justify="space-between" align="middle">
        <el-col :span="12">
          <div class="filter-section">
            <el-input
              v-model="searchKeyword"
              placeholder="搜索帖子标题或内容..."
              clearable
              style="width: 300px"
              @input="handleSearch"
            >
              <template #prefix>
                <el-icon><Search /></el-icon>
              </template>
            </el-input>
            <el-select
              v-model="selectedCategory"
              placeholder="选择分类"
              clearable
              style="width: 150px; margin-left: 10px"
              @change="handleCategoryChange"
            >
              <el-option
                v-for="category in categories"
                :key="category.value"
                :label="category.label"
                :value="category.value"
              />
            </el-select>
          </div>
        </el-col>
        <el-col :span="12" class="text-right">
          <el-button type="primary" @click="refreshData" :loading="loading">
            <el-icon><Refresh /></el-icon>
            刷新数据
          </el-button>
          <el-button @click="exportData">
            <el-icon><Download /></el-icon>
            导出数据
          </el-button>
        </el-col>
      </el-row>
    </div>

    <!-- 热点帖子列表 -->
    <div class="hot-posts-list">
      <el-card shadow="hover" class="posts-card">
        <template #header>
          <div class="card-header">
            <div class="header-left">
              <h3 class="card-title">
                <el-icon><TrendCharts /></el-icon>
                校园热点话题 Top 10
              </h3>
              <span class="update-time">最后更新: {{ lastUpdateTime }}</span>
            </div>
            <div class="header-right">
              <el-tag type="danger" size="large">实时监测</el-tag>
            </div>
          </div>
        </template>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <el-skeleton :rows="5" animated />
        </div>

        <!-- 数据列表 -->
        <div v-else-if="filteredPosts.length > 0" class="posts-container">
          <div v-for="(post, index) in filteredPosts" :key="post.threadId" class="post-item">
            <!-- 排名标识 -->
            <div class="post-rank">
              <span class="rank-number" :class="getRankClass(index)">{{ index + 1 }}</span>
            </div>

            <!-- 帖子内容 -->
            <div class="post-content">
              <!-- 标题和热度 -->
              <div class="post-header">
                <div class="post-title">
                  <span class="title-text">{{ post.title }}</span>
                  <el-tag :type="getCategoryTagType(post.cateId)" size="small">
                    {{ post.cateName }}
                  </el-tag>
                </div>
                <div class="post-hot">
                  <el-tooltip content="热度值" placement="top">
                    <span class="hot-value">
                      <el-icon><TrendCharts /></el-icon>
                      {{ post.hotVal }}
                    </span>
                  </el-tooltip>
                </div>
              </div>

              <!-- 内容预览 -->
              <div class="post-body">
                <div class="content-preview">
                  {{ truncateContent(post.content, 150) }}
                </div>
                <!-- 图片预览 -->
                <div v-if="hasImages(post)" class="image-preview">
                  <div class="image-count">
                    <el-icon><Picture /></el-icon>
                    {{ getImageCount(post) }} 张图片
                  </div>
                </div>
              </div>

              <!-- 用户信息和统计 -->
              <div class="post-footer">
                <div class="user-info">
                  <img
                    :src="post.headimgurl"
                    :alt="post.nickname"
                    class="user-avatar"
                    @error="handleImageError"
                  />
                  <div class="user-details">
                    <div class="user-name">{{ post.nickname }}</div>
                    <div class="user-level">
                      <el-tag size="small" :type="getLevelTagType(post.userLevel)">
                        {{ post.userLevelTitle }}
                      </el-tag>
                    </div>
                  </div>
                </div>
                <div class="post-stats">
                  <div class="stat-item">
                    <el-icon><View /></el-icon>
                    <span class="stat-value">{{ formatNumber(post.viewCount) }}</span>
                    <span class="stat-label">浏览</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><ChatDotSquare /></el-icon>
                    <span class="stat-value">{{ post.ccount || 0 }}</span>
                    <span class="stat-label">评论</span>
                  </div>
                  <div class="stat-item">
                    <el-icon><Star /></el-icon>
                    <span class="stat-value">{{ post.lcount || 0 }}</span>
                    <span class="stat-label">点赞</span>
                  </div>
                </div>
                <div class="post-actions">
                  <el-button size="small" type="primary" @click="viewPostDetail(post)">
                    查看详情
                  </el-button>
                  <el-button size="small" @click="analyzeSentiment(post)">
                    舆情分析
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-else class="empty-container">
          <el-empty description="暂无热点数据" />
        </div>
      </el-card>
    </div>

    <!-- 分类分布 -->
    <div class="category-distribution mt-30">
      <el-card shadow="hover">
        <template #header>
          <h3 class="card-title">
            <el-icon><DataAnalysis /></el-icon>
            热点话题分类分布
          </h3>
        </template>
        <div class="category-chart">
          <div v-if="categoryStats.length > 0" class="chart-container">
            <div class="category-list">
              <div v-for="category in categoryStats" :key="category.name" class="category-item">
                <div class="category-info">
                  <span class="category-name">{{ category.name }}</span>
                  <span class="category-count">{{ category.count }} 条</span>
                </div>
                <el-progress
                  :percentage="category.percentage"
                  :color="getCategoryColor(category.name)"
                  :show-text="false"
                />
              </div>
            </div>
          </div>
          <div v-else class="no-data">
            <el-empty description="暂无分类数据" />
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  TrendCharts,
  View,
  ChatDotSquare,
  Star,
  Search,
  Refresh,
  Download,
  Picture,
  DataAnalysis
} from '@element-plus/icons-vue'
import { getHotPostDataApi } from '@/api/postApi'

// 响应式数据
const posts = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const selectedCategory = ref('')
const lastUpdateTime = ref('')

// 获取数据
const fetchHotPosts = async () => {
  try {
    loading.value = true
    const res = await getHotPostDataApi()
    if (res.code === 200) {
      posts.value = res.data
      lastUpdateTime.value = new Date().toLocaleString()
      ElMessage.success('数据更新成功')
    } else {
      ElMessage.error(res.message || '获取数据失败')
    }
  } catch (error) {
    console.error('获取热点数据出错:', error)
    ElMessage.error('获取数据失败')
  } finally {
    loading.value = false
  }
}

// 计算属性
const totalPosts = computed(() => posts.value.length)
const totalViews = computed(() => posts.value.reduce((sum, post) => sum + (post.viewCount || 0), 0))
const totalComments = computed(() => posts.value.reduce((sum, post) => sum + (post.ccount || 0), 0))
const totalLikes = computed(() => posts.value.reduce((sum, post) => sum + (post.lcount || 0), 0))

// 分类统计
const categories = computed(() => {
  const uniqueCategories = [...new Set(posts.value.map(post => post.cateName))]
  return uniqueCategories.map(name => ({
    label: name,
    value: name
  }))
})

const categoryStats = computed(() => {
  const stats = {}
  posts.value.forEach(post => {
    if (!stats[post.cateName]) {
      stats[post.cateName] = { name: post.cateName, count: 0 }
    }
    stats[post.cateName].count++
  })

  return Object.values(stats).map(item => ({
    ...item,
    percentage: Math.round((item.count / totalPosts.value) * 100)
  })).sort((a, b) => b.count - a.count)
})

// 过滤帖子
const filteredPosts = computed(() => {
  let filtered = [...posts.value]

  // 按关键词搜索
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    filtered = filtered.filter(post =>
      post.title.toLowerCase().includes(keyword) ||
      post.content.toLowerCase().includes(keyword)
    )
  }

  // 按分类筛选
  if (selectedCategory.value) {
    filtered = filtered.filter(post => post.cateName === selectedCategory.value)
  }

  return filtered
})

// 工具函数
const formatNumber = (num) => {
  if (!num) return '0'
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}

const truncateContent = (content, maxLength) => {
  if (!content) return ''
  if (content.length <= maxLength) return content
  return content.substring(0, maxLength) + '...'
}

const hasImages = (post) => {
  try {
    const imgPaths = JSON.parse(post.imgPaths || '[]')
    return imgPaths.length > 0
  } catch {
    return false
  }
}

const getImageCount = (post) => {
  try {
    const imgPaths = JSON.parse(post.imgPaths || '[]')
    return imgPaths.length
  } catch {
    return 0
  }
}

const getRankClass = (index) => {
  if (index === 0) return 'rank-first'
  if (index === 1) return 'rank-second'
  if (index === 2) return 'rank-third'
  return 'rank-other'
}

const getCategoryTagType = (cateId) => {
  const typeMap = {
    '101': 'success', // 二手闲置
    '105': 'warning', // 打听求助
    '108': 'danger'   // 校园趣事
  }
  return typeMap[cateId] || 'info'
}

const getLevelTagType = (level) => {
  if (level >= 5) return 'success'  // 本科生及以上
  if (level >= 4) return 'warning'  // 高中生
  if (level >= 3) return 'info'     // 初中生
  return ''                         // 小学生
}

const getCategoryColor = (categoryName) => {
  const colorMap = {
    '校园趣事': '#FF6384',
    '打听求助': '#36A2EB',
    '二手闲置': '#FFCE56'
  }
  return colorMap[categoryName] || '#4BC0C0'
}

// 事件处理
const handleSearch = () => {
  // 搜索逻辑已在计算属性中处理
}

const handleCategoryChange = () => {
  // 分类筛选逻辑已在计算属性中处理
}

const refreshData = () => {
  fetchHotPosts()
}

const exportData = () => {
  ElMessageBox.confirm('确认导出当前热点数据？', '导出确认', {
    confirmButtonText: '确认',
    cancelButtonText: '取消',
    type: 'info'
  }).then(() => {
    // 这里可以添加导出逻辑
    ElMessage.success('导出功能开发中...')
  })
}

const viewPostDetail = (post) => {
  ElMessageBox.alert(post.content, `帖子详情: ${post.title}`, {
    confirmButtonText: '关闭',
    customClass: 'post-detail-dialog'
  })
}

const analyzeSentiment = (post) => {
  ElMessage.info(`正在分析帖子 "${post.title}" 的舆情...`)
  // 这里可以添加舆情分析逻辑
}

const handleImageError = (event) => {
  event.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'
}

// 生命周期
onMounted(() => {
  fetchHotPosts()
})
</script>

<style lang="scss" scoped>
.sentiment-container {
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  text-align: center;
  .page-title {
    font-size: 32px;
    font-weight: bold;
    color: #303133;
    margin-bottom: 10px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  .page-subtitle {
    font-size: 16px;
    color: #909399;
  }
}

.stats-cards {
  .stat-card {
    display: flex;
    align-items: center;
    padding: 20px;
    background: #fff;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.1);
    }

    .stat-icon {
      width: 60px;
      height: 60px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-right: 20px;

      .el-icon {
        font-size: 28px;
        color: #fff;
      }
    }

    .stat-content {
      .stat-value {
        font-size: 24px;
        font-weight: bold;
        color: #303133;
        margin-bottom: 5px;
      }

      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

.action-bar {
  .filter-section {
    display: flex;
    align-items: center;
  }
}

.posts-card {
  border-radius: 12px;
  border: none;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;

    .header-left {
      .card-title {
        display: flex;
        align-items: center;
        gap: 8px;
        font-size: 18px;
        font-weight: 600;
        color: #303133;
        margin: 0;

        .el-icon {
          color: #f56c6c;
        }
      }

      .update-time {
        font-size: 12px;
        color: #909399;
        margin-top: 4px;
      }
    }
  }
}

.posts-container {
  .post-item {
    display: flex;
    padding: 20px;
    margin-bottom: 16px;
    background: #fff;
    border-radius: 8px;
    border: 1px solid #f0f0f0;
    transition: all 0.3s;

    &:hover {
      border-color: #409eff;
      box-shadow: 0 4px 12px rgba(64, 158, 255, 0.1);
      transform: translateX(5px);
    }

    .post-rank {
      margin-right: 20px;
      flex-shrink: 0;

      .rank-number {
        display: inline-block;
        width: 36px;
        height: 36px;
        line-height: 36px;
        text-align: center;
        border-radius: 50%;
        font-weight: bold;
        font-size: 16px;

        &.rank-first {
          background: linear-gradient(135deg, #ffd700 0%, #ffa500 100%);
          color: #fff;
          box-shadow: 0 4px 8px rgba(255, 165, 0, 0.3);
        }

        &.rank-second {
          background: linear-gradient(135deg, #c0c0c0 0%, #a9a9a9 100%);
          color: #fff;
          box-shadow: 0 4px 8px rgba(169, 169, 169, 0.3);
        }

        &.rank-third {
          background: linear-gradient(135deg, #cd7f32 0%, #a0522d 100%);
          color: #fff;
          box-shadow: 0 4px 8px rgba(205, 127, 50, 0.3);
        }

        &.rank-other {
          background: #f5f7fa;
          color: #909399;
          border: 1px solid #e4e7ed;
        }
      }
    }

    .post-content {
      flex: 1;

      .post-header {
        display: flex;
        justify-content: space-between;
        align-items: flex-start;
        margin-bottom: 12px;

        .post-title {
          display: flex;
          align-items: center;
          gap: 10px;

          .title-text {
            font-size: 16px;
            font-weight: 600;
            color: #303133;
            line-height: 1.4;
          }
        }

        .post-hot {
          .hot-value {
            display: flex;
            align-items: center;
            gap: 4px;
            color: #f56c6c;
            font-weight: 500;

            .el-icon {
              font-size: 14px;
            }
          }
        }
      }

      .post-body {
        margin-bottom: 16px;

        .content-preview {
          font-size: 14px;
          color: #606266;
          line-height: 1.6;
          margin-bottom: 10px;
          display: -webkit-box;
          -webkit-line-clamp: 3;
          -webkit-box-orient: vertical;
          overflow: hidden;
        }

        .image-preview {
          .image-count {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            padding: 4px 8px;
            background: #f5f7fa;
            border-radius: 4px;
            font-size: 12px;
            color: #909399;

            .el-icon {
              font-size: 12px;
            }
          }
        }
      }

      .post-footer {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding-top: 16px;
        border-top: 1px solid #f0f0f0;

        .user-info {
          display: flex;
          align-items: center;
          gap: 12px;

          .user-avatar {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            object-fit: cover;
            border: 2px solid #fff;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          }

          .user-details {
            .user-name {
              font-size: 14px;
              font-weight: 500;
              color: #303133;
              margin-bottom: 4px;
            }
          }
        }

        .post-stats {
          display: flex;
          gap: 20px;

          .stat-item {
            display: flex;
            align-items: center;
            gap: 6px;
            color: #606266;
            font-size: 13px;

            .el-icon {
              font-size: 14px;
              color: #909399;
            }

            .stat-value {
              font-weight: 500;
              color: #303133;
            }

            .stat-label {
              color: #909399;
            }
          }
        }

        .post-actions {
          display: flex;
          gap: 8px;
        }
      }
    }
  }
}

.category-distribution {
  .category-chart {
    .category-list {
      .category-item {
        margin-bottom: 16px;

        .category-info {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .category-name {
            font-size: 14px;
            font-weight: 500;
            color: #303133;
          }

          .category-count {
            font-size: 13px;
            color: #909399;
          }
        }

        :deep(.el-progress-bar) {
          padding-right: 0;
        }
      }
    }
  }
}

.loading-container {
  padding: 40px 0;
}

.empty-container {
  padding: 60px 0;
}

// 响应式设计
@media (max-width: 1200px) {
  .stats-cards {
    .el-col {
      margin-bottom: 20px;
    }
  }

  .post-footer {
    flex-wrap: wrap;
    gap: 16px;

    .post-stats {
      order: 1;
      width: 100%;
      justify-content: space-around;
    }

    .post-actions {
      order: 2;
    }
  }
}

@media (max-width: 768px) {
  .sentiment-container {
    padding: 15px;
  }

  .action-bar {
    .el-col {
      margin-bottom: 15px;
    }

    .filter-section {
      flex-direction: column;
      gap: 10px;

      .el-input,
      .el-select {
        width: 100% !important;
        margin-left: 0 !important;
      }
    }
  }

  .post-item {
    flex-direction: column;

    .post-rank {
      margin-right: 0;
      margin-bottom: 15px;
      align-self: flex-start;
    }

    .post-header {
      flex-direction: column;
      align-items: flex-start !important;
      gap: 10px;
    }

    .post-footer {
      flex-direction: column;
      align-items: flex-start !important;
      gap: 16px;

      .post-stats {
        width: 100%;
        justify-content: space-between;
      }

      .post-actions {
        width: 100%;
        justify-content: flex-end;
      }
    }
  }
}

.mb-20 { margin-bottom: 20px; }
.mb-30 { margin-bottom: 30px; }
.mt-30 { margin-top: 30px; }
.text-right { text-align: right; }
</style>

