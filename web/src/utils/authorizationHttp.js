// src/utils/authorizationHttp.js
import axios from 'axios';
import router from '@/router'; // 需要引入路由实例
import { ElMessage } from 'element-plus'

const authorizationHttp = axios.create({
  baseURL: 'http://8.137.83.173:8081',
  // baseURL: 'http://localhost:8081',
  timeout: 10000,
});

// 请求拦截器
authorizationHttp.interceptors.request.use(config => {
  // 添加安全检查
  config.headers.Authorization = sessionStorage.token;
  return config;
}, error => {
  return Promise.reject(error);
});

// 响应拦截器
authorizationHttp.interceptors.response.use(response => {
  const data = response.data;

  // 检查返回的实体类中的状态码
  if (data && data.code === 401) {
    // 清除 token
    sessionStorage.removeItem('token');


    //可以添加一个提示
    ElMessage.error(data.message || '请先登录！');
    // 跳转到登录页面
    router.push('/login');

  }

  // 其他成功的响应正常返回
  return data;
}, error => {

  return Promise.reject(error);
});

export default authorizationHttp;
