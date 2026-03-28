<template>
  <div class="p-20">

    <!-- 搜索区域 -->
    <el-card class="search-card mb-20" shadow="never">
      <CustomSearch
        :searchConfig="searchConfig"
        @updateQueryData="handleSearchUpdate"
      />
    </el-card>

    <!-- 统计信息 - 只保留四个主要统计 -->
    <div class="stats-cards mb-20">
      <el-row :gutter="20">
        <el-col :span="6">
          <NormalCard
            :title="'总帖子数'"
            :value="summaryData.totalPosts || 0"
            :background="'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
          >
            <el-icon size="40" color="#fff">
              <Document />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="6">
          <NormalCard
            :title="'总浏览数'"
            :value="summaryData.totalViews || 0"
            :background="'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
          >
            <el-icon size="40" color="#fff">
              <View />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="6">
          <NormalCard
            :title="'总评论数'"
            :value="summaryData.totalComments || 0"
            :background="'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
          >
            <el-icon size="40" color="#fff">
              <ChatDotSquare />
            </el-icon>
          </NormalCard>
        </el-col>
        <el-col :span="6">
          <NormalCard
            :title="'总点赞数'"
            :value="summaryData.totalLikes || 0"
            :background="'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'"
            :titleColor="'#fff'"
            :valueColor="'#fff'"
          >
            <el-icon size="40" color="#fff">
              <Star />
            </el-icon>
          </NormalCard>
        </el-col>
      </el-row>
    </div>

    <!-- 帖子列表 -->
    <el-card class="list-card" shadow="never">
      <CustomTable
        :tableConfig="postDataConfig"
        :tableData="tableData"
        :total="total"
        @updateQueryData="updateQueryData"
      >
        <!-- 操作列的插槽 -->
        <template #handle="{ row }">
          <el-button type="primary" plain size="small" @click="handleView(row)">
            查看
          </el-button>
          <el-button type="info" plain size="small" @click="handleImages(row)">
            图片
          </el-button>
        </template>
      </CustomTable>
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="帖子详情" width="800px">
      <div v-if="currentPost" class="post-detail">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="user-info">
              <img
                :src="currentPost.headimgurl"
                :alt="currentPost.nickname"
                class="user-avatar"
                @error="handleImageError"
              />
              <div class="user-name">{{ currentPost.nickname }}</div>
              <div class="user-level">{{ currentPost.userLevelTitle }}</div>
            </div>
          </el-col>
          <el-col :span="16">
            <el-descriptions :column="1" border>
              <el-descriptions-item label="帖子ID">{{ currentPost.threadId }}</el-descriptions-item>
              <el-descriptions-item label="分类">{{ currentPost.cateName }}</el-descriptions-item>
              <el-descriptions-item label="标题">{{ currentPost.title }}</el-descriptions-item>
              <el-descriptions-item label="浏览数">{{ currentPost.viewCount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="评论数">{{ currentPost.ccount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="点赞数">{{ currentPost.lcount || 0 }}</el-descriptions-item>
              <el-descriptions-item label="发布时间">{{ formatTimestamp(currentPost.ptime) }}</el-descriptions-item>
              <el-descriptions-item label="创建时间">{{ currentPost.createTime }}</el-descriptions-item>
              <el-descriptions-item label="更新时间">{{ currentPost.updateTime }}</el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>

        <div class="post-content mt-20">
          <h4>帖子内容</h4>
          <div class="content-text">{{ currentPost.content }}</div>
        </div>
      </div>

      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>

    <!-- 图片查看对话框 -->
    <el-dialog v-model="imagesVisible" title="帖子图片" width="700px">
      <div v-if="currentPost" class="images-container">
        <div v-if="hasImages" class="images-grid">
          <div v-for="(imgPath, index) in parsedImages" :key="index" class="image-item">
            <el-image
              :src="getImageUrl(imgPath)"
              :preview-src-list="previewImageList"
              fit="cover"
              class="post-image"
              @error="handleImageError"
            />
            <div class="image-name">{{ getImageName(imgPath) }}</div>
          </div>
        </div>
        <div v-else class="no-images">
          <el-empty description="该帖子暂无图片" />
        </div>
      </div>

      <template #footer>
        <el-button type="primary" @click="imagesVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { getPostDataListApi, getSummaryDataApi } from "@/api/postApi";
import { ElMessage } from "element-plus";
import CustomTable from "@/components/CustomTable/index.vue";
import CustomSearch from "@/components/CustomSearch/index.vue";
import NormalCard from "@/components/CustomCard/NormalCard.vue";
import {
  Document,
  View,
  ChatDotSquare,
  Star,
  Refresh
} from "@element-plus/icons-vue";
import { postDataConfig, searchConfig } from "./postDataListConfig.js";

// 表格数据
const tableData = ref([]);
const total = ref(0);
const loading = ref(false);

// 汇总数据
const summaryData = ref({});
const summaryLoading = ref(false);

// 搜索参数
const searchParams = ref({
  searchContent: '',
  cateName: '',
  pTime: null
});

// 分页参数
const queryParams = ref({
  pageNum: 1,
  pageSize: 10
});

// 当前选中的帖子
const currentPost = ref(null);
const detailVisible = ref(false);
const imagesVisible = ref(false);

// 获取汇总数据
const fetchSummaryData = async () => {
  try {
    summaryLoading.value = true;
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
    summaryLoading.value = false;
  }
};

// 获取帖子列表
const fetchPostDataList = async () => {
  try {
    loading.value = true;

    // 合并搜索参数和分页参数
    const params = {
      ...queryParams.value,
      ...searchParams.value
    };

    const res = await getPostDataListApi(params);
    if (res.code === 200) {
      tableData.value = res.data.records;
      total.value = res.data.total;
    } else {
      ElMessage.error(res.message || '获取帖子列表失败');
    }
  } catch (error) {
    console.error('获取帖子列表出错:', error);
    ElMessage.error('获取数据失败');
  } finally {
    loading.value = false;
  }
};

// 处理搜索更新
const handleSearchUpdate = (params, shouldFetch = true) => {
  // 更新搜索参数
  Object.assign(searchParams.value, params);

  // 重置到第一页
  queryParams.value.pageNum = 1;

  if (shouldFetch) {
    fetchPostDataList();
  }
};

// 更新查询参数（分页）
const updateQueryData = (params) => {
  if (params.pageNum !== undefined) {
    queryParams.value.pageNum = params.pageNum;
  }
  if (params.pageSize !== undefined) {
    queryParams.value.pageSize = params.pageSize;
  }
  fetchPostDataList();
};

// 查看详情
const handleView = (row) => {
  currentPost.value = row;
  detailVisible.value = true;
};

// 查看图片
const handleImages = (row) => {
  currentPost.value = row;
  imagesVisible.value = true;
};

// 解析图片路径
const parsedImages = computed(() => {
  if (!currentPost.value || !currentPost.value.imgPaths) return [];
  try {
    const imgPaths = JSON.parse(currentPost.value.imgPaths);
    return Array.isArray(imgPaths) ? imgPaths : [];
  } catch (error) {
    console.error('解析图片路径出错:', error);
    return [];
  }
});

// 预览图片列表
const previewImageList = computed(() => {
  return parsedImages.value.map(imgPath => getImageUrl(imgPath));
});

// 是否有图片
const hasImages = computed(() => {
  return parsedImages.value.length > 0;
});

// 获取图片完整URL
const getImageUrl = (imgPath) => {
  if (imgPath.startsWith('http')) {
    return imgPath;
  } else if (imgPath.startsWith('upload/')) {
    return `/api/${imgPath}`;
  }
  return imgPath;
};

// 获取图片名称
const getImageName = (imgPath) => {
  const parts = imgPath.split('/');
  return parts[parts.length - 1];
};

// 格式化时间戳
const formatTimestamp = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp * 1000);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

// 处理图片加载错误
const handleImageError = (event) => {
  event.target.src = 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
};

// 组件挂载时加载数据
onMounted(() => {
  fetchSummaryData();
  fetchPostDataList();
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

.search-card {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}

.stats-cards {
  .el-col {
    margin-bottom: 20px;
  }
}

.list-card {
  border: 1px solid #e9ecef;
}

.post-detail {
  .user-info {
    text-align: center;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 8px;

    .user-avatar {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      margin-bottom: 15px;
      object-fit: cover;
      border: 3px solid #fff;
      box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    }

    .user-name {
      font-size: 18px;
      font-weight: bold;
      color: #303133;
      margin-bottom: 8px;
    }

    .user-level {
      color: #909399;
      font-size: 14px;
    }
  }

  .post-content {
    margin-top: 20px;

    h4 {
      margin-bottom: 10px;
      color: #303133;
    }

    .content-text {
      padding: 15px;
      background: #f8f9fa;
      border-radius: 4px;
      line-height: 1.6;
      white-space: pre-wrap;
      max-height: 200px;
      overflow-y: auto;
    }
  }
}

.images-container {
  .images-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 20px;

    .image-item {
      text-align: center;

      .post-image {
        width: 100%;
        height: 150px;
        border-radius: 8px;
        object-fit: cover;
        cursor: pointer;
        transition: transform 0.3s;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

        &:hover {
          transform: scale(1.05);
          box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
        }
      }

      .image-name {
        margin-top: 8px;
        font-size: 12px;
        color: #909399;
        word-break: break-all;
      }
    }
  }

  .no-images {
    text-align: center;
    padding: 40px 0;
  }
}

.mt-20 {
  margin-top: 20px;
}

.mb-20 {
  margin-bottom: 20px;
}

.mb-30 {
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .stats-cards {
    .el-col {
      margin-bottom: 15px;
    }
  }

  .post-detail {
    .el-col {
      margin-bottom: 20px;
    }
  }
}
</style>
