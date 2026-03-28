/**
 * 根据用户菜单权限设置路由
 * @param menuData
 */
/* -------------------------------------分割线-------------------------------------------- */
import layout from '../layout/index.vue'
import userSelfMsg from "@/views/User/userSelfMsg.vue";
import userList from "@/views/User/userList.vue";
import accessLogList from "@/views/Log/accessLogList.vue";
import loginLogList from "@/views/Log/loginLogList.vue";
import sysLogList from "@/views/Log/sysLogList.vue";
import resourceList from "@/views/Resource/resourceList.vue";
import imgCache from "@/views/Resource/imgCache.vue";
import lizardLogList from "@/views/Lizard/lizardLogList.vue";
import newPostData from "@/views/Lizard/newPostData.vue";
import postDataList from "@/views/Post/postDataList.vue";
import postTrendAnalysis from "@/views/Post/postTrendAnalysis.vue";
import userBehaviorAnalysis from "@/views/Post/userBehaviorAnalysis.vue";
import contentQualityAnalysis from "@/views/Post/contentQualityAnalysis.vue";
import interactionAnalysis from "@/views/Post/interactionAnalysis.vue";
import predictionAnalysis from "@/views/Post/predictionAnalysis.vue";
import dashboardAnalysis from "@/views/Post/dashboardAnalysis.vue";



const componentObj = {
  layout,
  userSelfMsg,
  userList,
  accessLogList,
  loginLogList,
  sysLogList,
  resourceList,
  imgCache,
  lizardLogList,
  newPostData,
  postDataList,
  postTrendAnalysis,
  userBehaviorAnalysis,
  contentQualityAnalysis,
  interactionAnalysis,
  predictionAnalysis,
  dashboardAnalysis


}
const iconList = {
  formTemplate: 'Tickets',
  tableTemplate: 'CopyDocument',
  cardTemplate: 'Files',
  systems: 'Tools',
  pdfTemplate:'Document',
  user:'User',
  log:'MessageBox',
  resource:"UploadFilled",
  lizard:"WindPower",
  post:"SwitchFilled",
}
/**
 * 动态添加用户路由权限
 * @param menuData json格式的字符串
 * @returns {[]}
 */
export function getAuthRouters(menuData) {
  const routerList = []
  let obj, children
  menuData && menuData.map(item => {
    obj = {
      path: item.path,
      component: layout,
      redirect: 'noRedirect',
      name: item.name,
      meta: {
        breadcrumbName: item.meta.title,
        icon: iconList[item.name]
      }
    }
    // 设置子菜单
    children = []
    item['children'].map(it => {
      children.push({
        path: it.path,
        component: componentObj[it.name],
        name: it.name, // 如果该组件需要缓存,这里的name属性请与组件的name保持一致
        meta: {
          breadcrumbName: it.meta.title,
          cached: true, // 是否需要缓存
          keepAlive: true,
          hidden: it.meta?.hidden || false
        }
      })
    })
    obj.children = children
    routerList.push(obj)
  })
  return routerList
}


