<template>
  <div class="p-20">
    <div class="header mb-30">
      <h1 class="page-title">最新帖子数据</h1>
      <p class="page-subtitle">实时展示爬取的最新10条帖子信息</p>
    </div>

    <div class="stats-cards mb-30">
      <div class="stats-row">
        <NormalCard
          :title="'总帖子数'"
          :value="totalPosts"
          :background="'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'"
          :titleColor="'#fff'"
          :valueColor="'#fff'"
        >
          <el-icon size="40" color="#fff">
            <Document />
          </el-icon>
        </NormalCard>

        <NormalCard
          :title="'平均浏览数'"
          :value="avgViewCount"
          :background="'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'"
          :titleColor="'#fff'"
          :valueColor="'#fff'"
        >
          <el-icon size="40" color="#fff">
            <View />
          </el-icon>
        </NormalCard>

        <NormalCard
          :title="'平均评论数'"
          :value="avgCommentCount"
          :background="'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'"
          :titleColor="'#fff'"
          :valueColor="'#fff'"
        >
          <el-icon size="40" color="#fff">
            <ChatDotSquare />
          </el-icon>
        </NormalCard>

        <NormalCard
          :title="'平均点赞数'"
          :value="avgLikeCount"
          :background="'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'"
          :titleColor="'#fff'"
          :valueColor="'#fff'"
        >
          <el-icon size="40" color="#fff">
            <Star />
          </el-icon>
        </NormalCard>
      </div>
    </div>

    <div class="refresh-section mb-20">
      <el-button type="primary" @click="fetchNewPostData" :loading="loading">
        <el-icon class="mr-10">
          <Refresh />
        </el-icon>
        刷新数据
      </el-button>
      <span class="last-update ml-20">最后更新: {{ lastUpdateTime }}</span>
    </div>

    <div class="posts-grid">
      <el-row :gutter="20">
        <el-col
          v-for="(post, index) in postData"
          :key="post.threadId"
          :xs="24"
          :sm="12"
          :md="12"
          :lg="8"
          :xl="6"
          class="mb-20"
        >
          <el-card class="post-card" shadow="hover">
            <template #header>
              <div class="post-header">
                <div class="post-title">{{ post.title }}</div>
                <div class="post-meta">
                  <el-tag size="small" type="info">{{ post.cateName }}</el-tag>
                  <span class="post-time">{{ formatTimestamp(post.ptime) }}</span>
                </div>
              </div>
            </template>

            <div class="post-content">
              <div class="user-info mb-15">
                <div class="user-avatar">
                  <img
                    :src="post.headimgurl"
                    :alt="post.nickname"
                    class="avatar-img"
                    @error="handleImageError"
                  />
                </div>
                <div class="user-details">
                  <div class="user-name">{{ post.nickname }}</div>
                  <div class="user-level">
                    <el-tag size="small" :type="getLevelType(post.userLevel)">
                      {{ post.userLevelTitle }}
                    </el-tag>
                  </div>
                </div>
              </div>

              <div class="post-text mb-15">
                {{ post.content }}
              </div>

              <div class="post-stats">
                <div class="stat-item">
                  <el-icon><View /></el-icon>
                  <span>{{ post.viewCount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><ChatDotSquare /></el-icon>
                  <span>{{ post.ccount || 0 }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><Star /></el-icon>
                  <span>{{ post.lcount || 0 }}</span>
                </div>
              </div>

              <div class="post-id mt-10">
                <span class="id-label">帖子ID:</span>
                <span class="id-value">{{ post.threadId }}</span>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { getNewPostDataApi } from "@/api/postApi";
import { ElMessage } from "element-plus";
import NormalCard from "@/components/CustomCard/NormalCard.vue";
import {
  Document,
  View,
  ChatDotSquare,
  Star,
  Refresh
} from "@element-plus/icons-vue";

// 帖子数据
const postData = ref([]);
const loading = ref(false);
const lastUpdateTime = ref('');

// 获取最新帖子数据
const fetchNewPostData = async () => {
  try {
    loading.value = true;
    const res = await getNewPostDataApi();
    if (res.code === 200) {
      postData.value = res.data;
      lastUpdateTime.value = new Date().toLocaleString('zh-CN');
      ElMessage.success('数据刷新成功');
    } else {
      ElMessage.error(res.message || '获取最新帖子失败');
    }
  } catch (error) {
    console.error('获取最新帖子出错:', error);
    ElMessage.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 计算属性
const totalPosts = computed(() => postData.value.length);

const avgViewCount = computed(() => {
  if (postData.value.length === 0) return 0;
  const total = postData.value.reduce((sum, post) => sum + (post.viewCount || 0), 0);
  return Math.round(total / postData.value.length);
});

const avgCommentCount = computed(() => {
  if (postData.value.length === 0) return 0;
  const total = postData.value.reduce((sum, post) => sum + (post.ccount || 0), 0);
  return (total / postData.value.length).toFixed(1);
});

const avgLikeCount = computed(() => {
  if (postData.value.length === 0) return 0;
  const total = postData.value.reduce((sum, post) => sum + (post.lcount || 0), 0);
  return (total / postData.value.length).toFixed(1);
});

// 格式化时间戳
const formatTimestamp = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp * 1000);
  return date.toLocaleString('zh-CN', {
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

// 获取等级标签类型
const getLevelType = (level) => {
  if (level >= 8) return 'success';
  if (level >= 5) return 'warning';
  if (level >= 3) return 'primary';
  return 'info';
};

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
};

// 组件挂载时加载数据
onMounted(() => {
  fetchNewPostData();
});
</script>

<style lang="scss" scoped>
.header {
  text-align: center;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
}

.page-subtitle {
  font-size: 16px;
  color: #909399;
}

.stats-row {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.refresh-section {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: #fff;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.last-update {
  color: #909399;
  font-size: 14px;
}

.post-card {
  height: 100%;
  transition: all 0.3s ease;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 18px rgba(0, 0, 0, 0.1);
  }
}

.post-header {
  .post-title {
    font-size: 16px;
    font-weight: bold;
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
    justify-content: space-between;
  }

  .post-time {
    font-size: 12px;
    color: #909399;
  }
}

.post-content {
  .user-info {
    display: flex;
    align-items: center;
  }

  .user-avatar {
    width: 40px;
    height: 40px;
    border-radius: 50%;
    overflow: hidden;
    margin-right: 12px;
    flex-shrink: 0;

    .avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }
  }

  .user-details {
    flex: 1;

    .user-name {
      font-size: 14px;
      font-weight: 500;
      color: #303133;
      margin-bottom: 4px;
    }
  }

  .post-text {
    font-size: 14px;
    color: #606266;
    line-height: 1.5;
    display: -webkit-box;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;
    overflow: hidden;
  }

  .post-stats {
    display: flex;
    align-items: center;
    gap: 20px;
    padding: 10px 0;
    border-top: 1px solid #f0f0f0;
    border-bottom: 1px solid #f0f0f0;

    .stat-item {
      display: flex;
      align-items: center;
      gap: 6px;
      color: #909399;
      font-size: 14px;

      .el-icon {
        font-size: 16px;
      }
    }
  }

  .post-id {
    font-size: 12px;
    color: #909399;

    .id-label {
      margin-right: 8px;
    }

    .id-value {
      font-family: monospace;
      color: #303133;
    }
  }
}

.mr-10 {
  margin-right: 10px;
}

.ml-20 {
  margin-left: 20px;
}

.mb-15 {
  margin-bottom: 15px;
}

.mb-20 {
  margin-bottom: 20px;
}

.mb-30 {
  margin-bottom: 30px;
}

.mt-10 {
  margin-top: 10px;
}

@media (max-width: 768px) {
  .stats-row {
    grid-template-columns: 1fr;
  }

  .refresh-section {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .last-update {
    margin-left: 0;
  }
}
</style>
