<template>
  <div class="p-20">
    <CustomTable
      :tableConfig="lizardLogConfig"
      :tableData="tableData"
      :total="total"
      @updateQueryData="updateQueryData"
    >
      <!-- 操作列的插槽 -->
      <template #handle="{ row }">
        <el-button type="primary" plain @click="handleDetail(row)">详情</el-button>
      </template>
    </CustomTable>

    <!-- 详情对话框 -->
    <el-dialog v-model="detailVisible" title="爬取日志详情" width="900px">
      <div class="detail-container">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="日志ID">{{ detailData.lizardId }}</el-descriptions-item>
          <el-descriptions-item label="爬取时间">{{ detailData.lizardTime }}</el-descriptions-item>
          <el-descriptions-item label="帖子数量" :span="2">
            {{ parsedPosts.length }}
          </el-descriptions-item>
        </el-descriptions>

        <div class="post-list mt-20">
          <h3>爬取的帖子列表 (共 {{ parsedPosts.length }} 条)</h3>
          <el-table :data="parsedPosts" border style="width: 100%" height="400">
            <el-table-column prop="threadId" label="帖子ID" width="120" />
            <el-table-column prop="title" label="标题" width="300" />
            <el-table-column prop="cateName" label="分类" width="100" />
            <el-table-column prop="nickname" label="用户" width="120" />
            <el-table-column prop="userLevelTitle" label="等级" width="80" />
          </el-table>
        </div>
      </div>

      <template #footer>
        <el-button type="primary" @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import CustomTable from "@/components/CustomTable/index.vue";
import { ref, onMounted, computed } from "vue";
import { getLizardLogListApi } from "@/api/postApi";
import { ElMessage } from "element-plus";
import { lizardLogConfig } from "./lizardLogListConfig.js";

// 表格数据
const tableData = ref([]);
const total = ref(0);
const queryParams = ref({
  pageNum: 1,
  pageSize: 10
});

// 详情对话框
const detailVisible = ref(false);
const detailData = ref({
  lizardId: null,
  lizardTime: null,
  lizardData: null
});

const loading = ref(false);

// 改进的解析函数
const parseLizardData = (lizardData) => {
  if (!lizardData) return [];

  try {
    console.log('原始数据:', lizardData);

    // 移除开头的'['和结尾的']'
    const dataStr = lizardData.trim();
    if (!dataStr.startsWith('[') || !dataStr.endsWith(']')) {
      console.log('格式不正确，不是数组格式');
      return [];
    }

    // 提取PostEntity对象字符串
    const content = dataStr.substring(1, dataStr.length - 1);
    console.log('提取的内容:', content);

    // 使用更灵活的正则表达式匹配PostEntity
    const postMatches = content.match(/PostEntity\{[^}]*\}(?=,|$)/g);
    console.log('匹配到的帖子:', postMatches);

    if (!postMatches || postMatches.length === 0) {
      console.log('未匹配到帖子');
      return [];
    }

    const posts = postMatches.map(post => {
      console.log('处理帖子:', post);

      // 提取字段名和值
      const fields = {};

      // 匹配所有字段：字段名='值'
      const fieldRegex = /(\w+)='([^']*)'/g;
      let match;

      while ((match = fieldRegex.exec(post)) !== null) {
        const key = match[1];
        let value = match[2];

        // 只保留需要的字段
        if (['threadId', 'title', 'cateName', 'nickname', 'userLevelTitle'].includes(key)) {
          fields[key] = value;
        }
      }

      console.log('解析后的字段:', fields);
      return fields;
    });

    console.log('最终解析结果:', posts);
    return posts;
  } catch (error) {
    console.error('解析lizardData出错:', error, lizardData);
    return [];
  }
};

// 计算属性：解析后的帖子列表
const parsedPosts = computed(() => {
  const posts = parseLizardData(detailData.value.lizardData);
  console.log('计算属性解析结果:', posts);
  return posts;
});

// 获取爬取日志列表
const fetchLizardLogList = async () => {
  try {
    loading.value = true;
    const res = await getLizardLogListApi(queryParams.value);
    if (res.code === 200) {
      // 处理数据，添加帖子数量字段
      tableData.value = res.data.records.map(item => {
        const posts = parseLizardData(item.lizardData);
        console.log('列表项解析结果:', item.lizardId, posts.length);
        return {
          ...item,
          postCount: posts.length
        };
      });
      total.value = res.data.total;
    } else {
      ElMessage.error(res.message || '获取爬取日志失败');
    }
  } catch (error) {
    console.error('获取爬取日志出错:', error);
    ElMessage.error('权限不足！');
  } finally {
    loading.value = false;
  }
};

// 更新查询参数并重新加载数据
const updateQueryData = (params, shouldFetch = true) => {
  if (params.pageNum !== undefined) {
    queryParams.value.pageNum = params.pageNum;
  }
  if (params.pageSize !== undefined) {
    queryParams.value.pageSize = params.pageSize;
  }

  Object.assign(queryParams.value, params);

  if (shouldFetch) {
    fetchLizardLogList();
  }
};

// 查看详情
const handleDetail = (row) => {
  console.log('查看详情:', row);
  detailData.value = {
    lizardId: row.lizardId,
    lizardTime: row.lizardTime,
    lizardData: row.lizardData
  };
  console.log('详情数据:', detailData.value);
  detailVisible.value = true;
};

// 组件挂载时加载数据
onMounted(() => {
  fetchLizardLogList();
});
</script>

<style scoped>
.detail-container {
  max-height: 600px;
  overflow-y: auto;
}

.post-list {
  background: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.post-list h3 {
  margin: 0 0 15px 0;
  color: #303133;
}

.mt-20 {
  margin-top: 20px;
}

.el-tag {
  margin-right: 10px;
}
</style>
