import { defineStore } from "pinia";
import { getAuthListApi } from "@/api/modules/user";
import { getFlatMenuList, getShowMenuList, getAllBreadcrumbList } from "@/utils";

export const useAuthStore = defineStore({
  id: "club-auth",
  state: () => ({
    authButtonList: {}, // 按钮权限列表
    authMenuList: [], // 菜单权限列表
    routeName: "" // 当前页面的 router name，用来做按钮权限筛选
  }),
  getters: {
    authButtonListGet: state => state.authButtonList, // 获取按钮权限列表
    authMenuListGet: state => state.authMenuList, // 获取菜单权限列表
    showMenuListGet: state => getShowMenuList(state.authMenuList), // 获取显示的菜单列表
    flatMenuListGet: state => getFlatMenuList(state.authMenuList), // 获取扁平化的菜单列表
    breadcrumbListGet: state => getAllBreadcrumbList(state.authMenuList) // 获取所有面包屑导航列表
  },
  actions: {
    // 获取菜单列表
    async getAuthMenuList(role) {
      const { data } = await getAuthListApi(role);
      this.authMenuList = data.data.menu;
    },
    // 获取按钮列表
    async getAuthButtonList(role) {
      const { data } = await getAuthListApi(role);
      this.authButtonList = data.data.button;
    },
    // 设置路由名称
    async setRouteName(name) {
      this.routeName = name;
    }
  }
});
