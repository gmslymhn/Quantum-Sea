

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
  }
]

// 模拟获取动态路由数据
export function getDynamicRoutes(){
    return new Promise((resolve) => {
        resolve(routes)
    })
}
