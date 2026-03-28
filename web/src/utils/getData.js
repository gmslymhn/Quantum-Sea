

const routes = [
  {
    path: '/user',
    id: 100,
    name: 'user',
    meta: {
      title: '用户管理'
    },
    children: [{
      path: 'userSelfMsg',
      id: 101,
      name: 'userSelfMsg',
      meta: {
        title: '个人信息',
        hidden: true
      }
    }, {
        path: 'userList',
        id: 102,
        name: 'userList',
        meta: {
          title: '用户列表',
        }
      }
    ]
  },
  {
    path: '/log',
    id: 200,
    name: 'log',
    meta: {
      title: '日志管理'
    },
    children: [{
      path: 'accessLogList',
      id: 201,
      name: 'accessLogList',
      meta: {
        title: '访问日志'
      }
    },{
      path: 'logiLogList',
      id: 202,
      name: 'loginLogList',
      meta: {
        title: '登录日志',
      }
    },{
      path: 'sysLogList',
      id: 203,
      name: 'sysLogList',
      meta: {
        title: '系统日志',
      }
    }
    ]
  },
  {
    path: '/resource',
    id: 300,
    name: 'resource',
    meta: {
      title: '资源管理'
    },
    children: [{
      path: 'resourceList',
      id: 301,
      name: 'resourceList',
      meta: {
        title: '资源列表'
      }
    },{
      path: 'imgCache',
      id: 302,
      name: 'imgCache',
      meta: {
        title: '图片缓存管理'
      }
    }
    ]
  },
  {
    path: '/lizard',
    id: 400,
    name: 'lizard',
    meta: {
      title: '爬取管理'
    },
    children: [{
      path: 'lizardLogList',
      id: 401,
      name: 'lizardLogList',
      meta: {
        title: '爬取日志'
      }
    },{
      path: 'newPostData',
      id: 402,
      name: 'newPostData',
      meta: {
        title: '最新爬取数据'
      }
    }
    ]
  },
  {
    path: '/post',
    id: 500,
    name: 'post',
    meta: {
      title: '舆情分析'
    },
    children: [{
      path: 'dashboardAnalysis',
      id: 501,
      name: 'dashboardAnalysis',
      meta: {
        title: '综合仪表盘'
      }
    },{
      path: 'postDataList',
      id: 502,
      name: 'postDataList',
      meta: {
        title: '论坛列表'
      }
    },{
      path: 'postTrendAnalysis',
      id: 503,
      name: 'postTrendAnalysis',
      meta: {
        title: '时间趋势分析'
      }
    },{
      path: 'userBehaviorAnalysis',
      id: 504,
      name: 'userBehaviorAnalysis',
      meta: {
        title: '用户行为分析'
      }
    },{
      path: 'contentQualityAnalysis',
      id: 505,
      name: 'contentQualityAnalysis',
      meta: {
        title: '内容质量分析'
      }
    },{
      path: 'interactionAnalysis',
      id: 506,
      name: 'interactionAnalysis',
      meta: {
        title: '互动关系分析'
      }
    },{
      path: 'predictionAnalysis',
      id: 507,
      name: 'predictionAnalysis',
      meta: {
        title: '预测分析'
      }
    }
    ]
  },
]

// 模拟获取动态路由数据
export function getDynamicRoutes(){
    return new Promise((resolve) => {
        resolve(routes)
    })
}
