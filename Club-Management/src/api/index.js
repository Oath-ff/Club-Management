// src/api/index.js
import axios from "axios";
import { ElMessage } from "element-plus";
import { useUserStore } from "@/stores/modules/user";
import router from "@/routers";
import apiConfig from "@/config/apiConfig"; // 引入配置文件

// 创建 Axios 实例
const apiClient = axios.create({
  baseURL: apiConfig.baseURL, // 使用基础URL
  timeout: 30000,
  withCredentials: true
});

// 请求拦截器
apiClient.interceptors.request.use(
  config => {
    const userStore = useUserStore();
    // 在请求头中添加 token
    if (config.headers && typeof config.headers.set === "function") {
      config.headers.set("Authorization", `Bearer ${userStore.token}`);
    }
    return config;
  },
  error => {
    // 请求错误处理
    return Promise.reject(error);
  }
);

// 响应拦截器
apiClient.interceptors.response.use(
  response => {
    // 响应数据处理
    return response.data;
  },
  error => {
    // 响应错误处理
    const { response } = error;
    if (response) {
      switch (response.status) {
        case 400:
          ElMessage.error("请求失败！请您稍后重试");
          break;
        case 401:
          ElMessage.error("登录失效！请您重新登录");
          router.replace("/login");
          break;
        case 403:
          ElMessage.error("当前账号无权限访问！");
          break;
        case 404:
          ElMessage.error("你所访问的资源不存在！");
          break;
        case 500:
          ElMessage.error("服务异常！");
          break;
        default:
          ElMessage.error("请求失败！");
      }
    } else if (error.message.includes("timeout")) {
      ElMessage.error("请求超时！请您稍后重试");
    } else if (error.message.includes("Network Error")) {
      ElMessage.error("网络错误！请您稍后重试");
    }
    return Promise.reject(error);
  }
);

export default apiClient;
