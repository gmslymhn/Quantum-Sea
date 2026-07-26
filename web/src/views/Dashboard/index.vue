<template>
  <div class="dashboard-container p-20">

    <!-- 核心数据统计 -->
    <div class="core-stats mb-30">
      <el-row :gutter="20">
        <el-col :span="6">
          <NormalCard
            :title="'总帖子数'"
            :value="summaryData.totalPosts || 0"
            :background="'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
            :width="'100%'"
            :height="'140px'"
          >
            <el-icon size="50" color="#fff">
              <Document />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="6">
          <NormalCard
            :title="'总浏览数'"
            :value="formatNumber(summaryData.totalViews) || 0"
            :background="'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
            :width="'100%'"
            :height="'140px'"
          >
            <el-icon size="50" color="#fff">
              <View />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="6">
          <NormalCard
            :title="'总评论数'"
            :value="formatNumber(summaryData.totalComments) || 0"
            :background="'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
            :width="'100%'"
            :height="'140px'"
          >
            <el-icon size="50" color="#fff">
              <ChatDotSquare />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="6">
          <NormalCard
            :title="'总点赞数'"
            :value="formatNumber(summaryData.totalLikes) || 0"
            :background="'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
            :width="'100%'"
            :height="'140px'"
          >
            <el-icon size="50" color="#fff">
              <Star />
            </el-icon>
          </NormalCard>
        </el-col>
      </el-row>
    </div>

    <!-- 第二行：今日数据和平均值 -->
    <div class="secondary-stats mb-30">
      <el-row :gutter="20">
        <el-col :span="8">
          <NormalCard
            :title="'今日新增'"
            :value="summaryData.todayPosts || 0"
            :background="'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
            :width="'100%'"
            :height="'120px'"
          >
            <el-icon size="40" color="#fff">
              <Sunny />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="8">
          <NormalCard
            :title="'昨日新增'"
            :value="summaryData.yesterdayPosts || 0"
            :background="'linear-gradient(135deg, #30cfd0 0%, #330867 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
            :width="'100%'"
            :height="'120px'"
          >
            <el-icon size="40" color="#fff">
              <Moon />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="8">
          <NormalCard
            :title="'平均浏览'"
            :value="summaryData.avgViews || '0.00'"
            :background="'linear-gradient(135deg, #a8edea 0%, #fed6e3 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
            :width="'100%'"
            :height="'120px'"
          >
            <el-icon size="40" color="#fff">
              <TrendCharts />
            </el-icon>
          </NormalCard>
        </el-col>
      </el-row>
    </div>

    <!-- 图表区域 -->
    <div class="charts-section mb-30">
      <el-row :gutter="20">
        <!-- 分类分布饼图 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">帖子分类分布</span>
              </div>
            </template>
            <div v-if="categoryData.length > 0" class="chart-container">
              <CustomChartPie
                :title="''"
                :data="categoryData"
                :color="categoryColors"
              />
            </div>
            <div v-else class="no-data">
              <el-empty description="暂无分类数据" />
            </div>
          </el-card>
        </el-col>

        <!-- 用户等级分布 -->
        <el-col :span="12">
          <el-card class="chart-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">用户等级分布</span>
              </div>
            </template>
            <div v-if="levelData.length > 0" class="chart-container">
              <CustomChartBar
                :title="''"
                :data="levelData"
                :total="10"
                :inverse="false"
              />
            </div>
            <div v-else class="no-data">
              <el-empty description="暂无等级数据" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>

<!--    &lt;!&ndash; 第三行：趋势和热门 &ndash;&gt;-->
<!--    <div class="trend-section mb-30">-->
<!--      <el-row :gutter="20">-->
<!--        &lt;!&ndash; 发帖趋势 &ndash;&gt;-->
<!--        <el-col :span="12">-->
<!--          <el-card class="trend-card" shadow="hover">-->
<!--            <template #header>-->
<!--              <div class="chart-header">-->
<!--                <span class="chart-title">最近7天发帖趋势</span>-->
<!--              </div>-->
<!--            </template>-->
<!--            <div v-if="trendData.xAxisData && trendData.xAxisData.length > 0" class="trend-container">-->
<!--              <CustomChartLine-->
<!--                :title="''"-->
<!--                :configData="trendData"-->
<!--              />-->
<!--            </div>-->
<!--            <div v-else class="no-data">-->
<!--              <el-empty description="暂无趋势数据" />-->
<!--            </div>-->
<!--          </el-card>-->
<!--        </el-col>-->

<!--        &lt;!&ndash; 热门帖子 &ndash;&gt;-->
<!--        <el-col :span="12">-->
<!--          <el-card class="hot-posts-card" shadow="hover">-->
<!--            <template #header>-->
<!--              <div class="chart-header">-->
<!--                <span class="chart-title">热门帖子 Top 10</span>-->
<!--              </div>-->
<!--            </template>-->
<!--            <div v-if="hotPosts.length > 0" class="hot-posts-list">-->
<!--              <div v-for="(post, index) in hotPosts" :key="post.thread_id" class="hot-post-item">-->
<!--                <div class="post-rank">-->
<!--                  <span class="rank-number" :class="getRankClass(index)">{{ index + 1 }}</span>-->
<!--                </div>-->
<!--                <div class="post-content">-->
<!--                  <div class="post-title">{{ post.title }}</div>-->
<!--                  <div class="post-meta">-->
<!--                    <span class="post-category">{{ post.cate_name }}</span>-->
<!--                    <span class="post-author">{{ post.nickname }}</span>-->
<!--                  </div>-->
<!--                  <div class="post-stats">-->
<!--                    <span class="stat-item">-->
<!--                      <el-icon><View /></el-icon>-->
<!--                      {{ formatNumber(post.view_count) }}-->
<!--                    </span>-->
<!--                    <span class="stat-item">-->
<!--                      <el-icon><ChatDotSquare /></el-icon>-->
<!--                      {{ post.c_count || 0 }}-->
<!--                    </span>-->
<!--                    <span class="stat-item">-->
<!--                      <el-icon><Star /></el-icon>-->
<!--                      {{ post.l_count || 0 }}-->
<!--                    </span>-->
<!--                  </div>-->
<!--                </div>-->
<!--              </div>-->
<!--            </div>-->
<!--            <div v-else class="no-data">-->
<!--              <el-empty description="暂无热门帖子" />-->
<!--            </div>-->
<!--          </el-card>-->
<!--        </el-col>-->
<!--      </el-row>-->
<!--    </div>-->

    <!-- 第四行：活跃用户和状态统计 -->
    <div class="bottom-section">
      <el-row :gutter="20">
        <!-- 活跃用户 -->
        <el-col :span="12">
          <el-card class="active-users-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">活跃用户 Top 10</span>
              </div>
            </template>
            <div v-if="activeUsers.length > 0" class="active-users-list">
              <div v-for="(user, index) in activeUsers" :key="user.nickname" class="active-user-item">
                <div class="user-rank">
                  <span class="rank-number" :class="getRankClass(index)">{{ index + 1 }}</span>
                </div>
                <div class="user-avatar">
                  <img
                    :src="user.headimgurl"
                    :alt="user.nickname"
                    class="avatar-img"
                    @error="handleImageError"
                  />
                </div>
                <div class="user-info">
                  <div class="user-name">{{ user.nickname }}</div>
                  <div class="user-stats">
                    <span class="stat-item">发帖: {{ user.post_count }}</span>
                    <span class="stat-item">浏览: {{ formatNumber(user.total_views) }}</span>
                  </div>
                </div>
              </div>
            </div>
            <div v-else class="no-data">
              <el-empty description="暂无活跃用户数据" />
            </div>
          </el-card>
        </el-col>

        <!-- 帖子状态统计 -->
        <el-col :span="12">
          <el-card class="status-card" shadow="hover">
            <template #header>
              <div class="chart-header">
                <span class="chart-title">帖子状态统计</span>
              </div>
            </template>
            <div v-if="statusStats.length > 0" class="status-container">
              <div class="status-list">
                <div v-for="status in statusStats" :key="status.finish_status" class="status-item">
                  <div class="status-label">
                    {{ getStatusLabel(status.finish_status) }}
                  </div>
                  <div class="status-progress">
                    <el-progress
                      :percentage="calculateStatusPercentage(status.count)"
                      :color="getStatusColor(status.finish_status)"
                      :show-text="false"
                    />
                  </div>
                  <div class="status-count">
                    {{ status.count }} 条
                  </div>
                </div>
              </div>
              <div class="status-summary">
                <div class="summary-item">
                  <span class="summary-label">正常帖子</span>
                  <span class="summary-value">{{ getStatusCount(10) || 0 }} 条</span>
                </div>
                <div class="summary-item">
                  <span class="summary-label">已完成帖子</span>
                  <span class="summary-value">{{ getStatusCount(20) || 0 }} 条</span>
                </div>
              </div>
            </div>
            <div v-else class="no-data">
              <el-empty description="暂无状态数据" />
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { getSummaryDataApi } from "@/api/postApi";
import { ElMessage } from "element-plus";
import NormalCard from "@/components/CustomCard/NormalCard.vue";
import CustomChartPie from "@/components/CustomChartPie/index.vue";
import CustomChartBar from "@/components/CustomChartBar/index.vue";
import CustomChartLine from "@/components/CustomChartLine/index.vue";
import {
  Document,
  View,
  ChatDotSquare,
  Star,
  Sunny,
  Moon,
  TrendCharts
} from "@element-plus/icons-vue";

// 汇总数据
const summaryData = ref({});
const loading = ref(false);

// 获取汇总数据
const fetchSummaryData = async () => {
  try {
    loading.value = true;
    const res = await getSummaryDataApi();
    if (res.code === 200) {
      summaryData.value = res.data;
    } else {
      ElMessage.error(res.message || '获取汇总数据失败');
    }
  } catch (error) {
    console.error('获取汇总数据出错:', error);
    ElMessage.error('获取汇总数据失败');
  } finally {
    loading.value = false;
  }
};

// 格式化数字（添加千位分隔符）
const formatNumber = (num) => {
  if (!num) return '0';
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

// 分类数据
const categoryData = computed(() => {
  if (!summaryData.value.categoryStats) return [];
  return summaryData.value.categoryStats.map(item => ({
    name: item.cate_name,
    value: item.count
  }));
});

// 分类颜色
const categoryColors = computed(() => {
  const colors = [
    '#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0',
    '#9966FF', '#FF9F40', '#FF6384', '#C9CBCF'
  ];
  return colors.slice(0, categoryData.value.length);
});

// 等级数据
const levelData = computed(() => {
  if (!summaryData.value.levelDistribution) return [];
  return summaryData.value.levelDistribution.map(item => ({
    name: item.user_level_title,
    value: item.count
  }));
});

// 趋势数据
const trendData = computed(() => {
  if (!summaryData.value.postTrend) {
    return {
      xAxisData: [],
      series: [],
      yAxisName: '发帖数量'
    };
  }

  const dates = summaryData.value.postTrend.map(item => item.date);
  const counts = summaryData.value.postTrend.map(item => item.count);

  return {
    xAxisData: dates,
    series: [{
      name: '发帖数量',
      data: counts,
      color: '64, 158, 255' // 蓝色
    }],
    yAxisName: '发帖数量'
  };
});

// 热门帖子
const hotPosts = computed(() => {
  return summaryData.value.hotPosts || [];
});

// 活跃用户
const activeUsers = computed(() => {
  return summaryData.value.activeUsers || [];
});

// 状态统计
const statusStats = computed(() => {
  return summaryData.value.statusStats || [];
});

// 获取排名样式
const getRankClass = (index) => {
  if (index === 0) return 'rank-first';
  if (index === 1) return 'rank-second';
  if (index === 2) return 'rank-third';
  return 'rank-other';
};

// 获取状态标签
const getStatusLabel = (status) => {
  const statusMap = {
    10: '正常帖子',
    20: '已完成',
    30: '已关闭',
    40: '已删除'
  };
  return statusMap[status] || `状态 ${status}`;
};

// 获取状态颜色
const getStatusColor = (status) => {
  const colorMap = {
    10: '#67c23a', // 绿色 - 正常
    20: '#409eff', // 蓝色 - 已完成
    30: '#e6a23c', // 黄色 - 已关闭
    40: '#f56c6c'  // 红色 - 已删除
  };
  return colorMap[status] || '#909399';
};

// 计算状态百分比
const calculateStatusPercentage = (count) => {
  const total = summaryData.value.totalPosts || 0;
  if (total === 0) return 0;
  return Math.round((count / total) * 100);
};

// 获取特定状态的数量
const getStatusCount = (status) => {
  const statusItem = statusStats.value.find(item => item.finish_status === status);
  return statusItem ? statusItem.count : 0;
};

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
};

// 组件挂载时加载数据
onMounted(() => {
  fetchSummaryData();
});
</script>

<style lang="scss" scoped>
.dashboard-container {
  background: #f5f7fa;
  min-height: 100vh;
}

.header {
  text-align: center;
}

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

.core-stats,
.secondary-stats {
  .el-col {
    margin-bottom: 20px;
  }
}

.chart-card,
.trend-card,
.hot-posts-card,
.active-users-card,
.status-card {
  height: 100%;
  border-radius: 12px;
  border: none;

  .el-card__header {
    border-bottom: 1px solid #f0f0f0;
    padding: 15px 20px;
    background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
    border-radius: 12px 12px 0 0;
  }

  .el-card__body {
    padding: 20px;
    height: calc(100% - 61px);
  }
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  position: relative;
  padding-left: 10px;

  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 50%;
    transform: translateY(-50%);
    width: 4px;
    height: 16px;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    border-radius: 2px;
  }
}

.chart-container {
  height: 350px;
}

.no-data {
  height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 热门帖子样式 */
.hot-posts-list {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;

  .hot-post-item {
    display: flex;
    align-items: flex-start;
    padding: 15px;
    margin-bottom: 12px;
    background: #f8f9fa;
    border-radius: 8px;
    transition: all 0.3s;
    border-left: 4px solid transparent;

    &:hover {
      background: #e9ecef;
      transform: translateX(5px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .post-rank {
      margin-right: 15px;
      flex-shrink: 0;

      .rank-number {
        display: inline-block;
        width: 28px;
        height: 28px;
        line-height: 28px;
        text-align: center;
        border-radius: 50%;
        font-weight: bold;
        font-size: 14px;

        &.rank-first {
          background: linear-gradient(135deg, #ffd700 0%, #ffa500 100%);
          color: #fff;
        }

        &.rank-second {
          background: linear-gradient(135deg, #c0c0c0 0%, #a9a9a9 100%);
          color: #fff;
        }

        &.rank-third {
          background: linear-gradient(135deg, #cd7f32 0%, #a0522d 100%);
          color: #fff;
        }

        &.rank-other {
          background: #e9ecef;
          color: #909399;
        }
      }
    }

    .post-content {
      flex: 1;

      .post-title {
        font-size: 15px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 8px;
        line-height: 1.4;
        display: -webkit-box;
        -webkit-line-clamp: 2;
        -webkit-box-orient: vertical;
        overflow: hidden;
      }

      .post-meta {
        display: flex;
        align-items: center;
        margin-bottom: 8px;
        font-size: 12px;

        .post-category {
          background: #e6f7ff;
          color: #1890ff;
          padding: 2px 8px;
          border-radius: 12px;
          margin-right: 10px;
        }

        .post-author {
          color: #909399;
        }
      }

      .post-stats {
        display: flex;
        align-items: center;
        gap: 15px;

        .stat-item {
          display: flex;
          align-items: center;
          gap: 5px;
          color: #606266;
          font-size: 12px;

          .el-icon {
            font-size: 14px;
          }
        }
      }
    }
  }
}

/* 活跃用户样式 */
.active-users-list {
  max-height: 400px;
  overflow-y: auto;
  padding-right: 10px;

  .active-user-item {
    display: flex;
    align-items: center;
    padding: 12px 15px;
    margin-bottom: 10px;
    background: #f8f9fa;
    border-radius: 8px;
    transition: all 0.3s;

    &:hover {
      background: #e9ecef;
      transform: translateX(5px);
    }

    .user-rank {
      margin-right: 15px;
      flex-shrink: 0;

      .rank-number {
        display: inline-block;
        width: 24px;
        height: 24px;
        line-height: 24px;
        text-align: center;
        border-radius: 50%;
        font-weight: bold;
        font-size: 12px;
        background: #e9ecef;
        color: #909399;

        &.rank-first {
          background: linear-gradient(135deg, #ffd700 0%, #ffa500 100%);
          color: #fff;
        }

        &.rank-second {
          background: linear-gradient(135deg, #c0c0c0 0%, #a9a9a9 100%);
          color: #fff;
        }

        &.rank-third {
          background: linear-gradient(135deg, #cd7f32 0%, #a0522d 100%);
          color: #fff;
        }
      }
    }

    .user-avatar {
      width: 40px;
      height: 40px;
      border-radius: 50%;
      overflow: hidden;
      margin-right: 15px;
      flex-shrink: 0;

      .avatar-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }
    }

    .user-info {
      flex: 1;

      .user-name {
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        margin-bottom: 4px;
      }

      .user-stats {
        display: flex;
        align-items: center;
        gap: 15px;
        font-size: 12px;

        .stat-item {
          color: #606266;

          &:first-child {
            color: #1890ff;
          }

          &:last-child {
            color: #52c41a;
          }
        }
      }
    }
  }
}

/* 状态统计样式 */
.status-container {
  .status-list {
    margin-bottom: 20px;

    .status-item {
      display: flex;
      align-items: center;
      margin-bottom: 15px;

      .status-label {
        width: 100px;
        font-size: 14px;
        color: #606266;
        flex-shrink: 0;
      }

      .status-progress {
        flex: 1;
        margin: 0 15px;

        :deep(.el-progress-bar) {
          padding-right: 0;
        }
      }

      .status-count {
        width: 80px;
        text-align: right;
        font-size: 14px;
        font-weight: 500;
        color: #303133;
        flex-shrink: 0;
      }
    }
  }

  .status-summary {
    display: flex;
    justify-content: space-around;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;

    .summary-item {
      text-align: center;

      .summary-label {
        display: block;
        font-size: 14px;
        color: #909399;
        margin-bottom: 5px;
      }

      .summary-value {
        display: block;
        font-size: 18px;
        font-weight: bold;
        color: #303133;
      }
    }
  }
}

/* 趋势图表容器 */
.trend-container {
  height: 300px;
}

/* 滚动条样式 */
.hot-posts-list::-webkit-scrollbar,
.active-users-list::-webkit-scrollbar {
  width: 6px;
}

.hot-posts-list::-webkit-scrollbar-track,
.active-users-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.hot-posts-list::-webkit-scrollbar-thumb,
.active-users-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.hot-posts-list::-webkit-scrollbar-thumb:hover,
.active-users-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 响应式调整 */
@media (max-width: 1200px) {
  .core-stats,
  .secondary-stats {
    .el-col {
      margin-bottom: 15px;
    }
  }

  .charts-section,
  .trend-section,
  .bottom-section {
    .el-col {
      margin-bottom: 20px;
    }
  }
}

.mb-30 {
  margin-bottom: 30px;
}
</style>
