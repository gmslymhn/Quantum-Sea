// 表格配置
export const postDataConfig = [
  {
    prop: 'threadId',
    label: '帖子ID',
    width: '120'
  },
  {
    prop: 'title',
    label: '标题',
    width: '200'
  },
  {
    prop: 'cateName',
    label: '分类',
    width: '100'
  },
  {
    prop: 'nickname',
    label: '用户',
    width: '120'
  },
  {
    prop: 'userLevelTitle',
    label: '等级',
    width: '80'
  },
  {
    prop: 'viewCount',
    label: '浏览',
    width: '80',
    align: 'center'
  },
  {
    prop: 'ccount',
    label: '评论',
    width: '80',
    align: 'center'
  },
  {
    prop: 'lcount',
    label: '点赞',
    width: '80',
    align: 'center'
  },
  {
    prop: 'ptime',
    label: '发布时间',
    width: '140',
    formatter: (value) => {
      if (!value) return '';
      const date = new Date(value * 1000);
      return date.toLocaleString('zh-CN', {
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
      });
    }
  },
  {
    slot: true,
    label: '操作',
    slotName: 'handle',
    width: '150'
  }
];

// 搜索配置
export const searchConfig = [
  {
    type: 'input',
    id: 'searchContent',
    placeholder: '请输入标题或内容关键词',
    title: '搜索内容'
  },
  {
    type: 'select',
    id: 'cateName',
    placeholder: '请选择分类',
    title: '分类名称',
    options: [
      { label: '二手闲置', value: '二手闲置' },
      { label: '打听求助', value: '打听求助' }
    ],
    label: 'label',
    value: 'value'
  },
  {
    type: 'datetime',
    id: 'pTime',
    placeholder: '请选择日期',
    title: '发布日期',
    format: 'YYYY-MM-DD'
  }
];
