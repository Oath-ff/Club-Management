import { createRouter, createWebHistory } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { useAuthStore } from "@/stores/modules/auth";
import { LOGIN_URL, ROUTER_WHITE_LIST } from "@/config";
import { initDynamicRouter } from "@/routers/modules/dynamicRouter";
import { staticRouter, errorRouter } from "@/routers/modules/staticRouter";
import NProgress from "@/config/nprogress";

// 创建 router 实例
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [...staticRouter, ...errorRouter],
  scrollBehavior: () => ({ left: 0, top: 0 })
});

// 路由守卫
router.beforeEach(async (to, from, next) => {
  const userStore = useUserStore();
  const authStore = useAuthStore();

  // 开始进度条
  NProgress.start();

  // 动态设置页面标题
  const title = import.meta.env.VITE_GLOB_APP_TITLE;
  document.title = to.meta.title ? `${to.meta.title} - ${title}` : title;

  // 判断是否访问登录页
  if (to.path.toLocaleLowerCase() === LOGIN_URL) {
    if (userStore.token) return next(from.fullPath);
    resetRouter();
    return next();
  }

  // 判断是否在路由白名单中
  if (ROUTER_WHITE_LIST.includes(to.path)) return next();

  // 判断是否有 token
  if (!userStore.token) return next({ path: LOGIN_URL, replace: true });

  // 如果没有菜单列表，就重新请求菜单列表并添加动态路由
  if (!authStore.authMenuListGet.length) {
    await initDynamicRouter();
    return next({ ...to, replace: true });
  }

  // 存储路由名称用于按钮权限筛选
  authStore.setRouteName(to.name);

  // 正常访问页面
  next();
});

// 重置路由
export const resetRouter = () => {
  const authStore = useAuthStore();
  authStore.flatMenuListGet.forEach(route => {
    const { name } = route;
    if (name && router.hasRoute(name)) router.removeRoute(name);
  });
};

// 路由错误处理
router.onError(error => {
  NProgress.done();
  console.warn("路由错误", error.message);
});

// 路由跳转结束
router.afterEach(() => {
  NProgress.done();
});

export default router;
