// 表格配置
export const lizardLogConfig = [
  {
    prop: 'lizardId',
    label: 'ID'
  },
  {
    prop: 'lizardTime',
    label: '爬取时间'
  },
  {
    prop: 'postCount',
    label: '帖子数量',
    formatter: (value) => value || 0
  },
  {
    slot: true,
    label: '操作',
    slotName: 'handle'
  }
];
