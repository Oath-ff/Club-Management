import { defineStore } from "pinia";
import piniaPersistConfig from "@/stores/helper/persist";

export const useUserStore = defineStore({
  id: "club-user",
  state: () => ({
    token: "",
    userInfo: {} // 初始用户信息为空对象
  }),
  getters: {},
  actions: {
    // 设置 Token
    setToken(token) {
      this.token = token;
    },
    // 设置用户信息
    setUserInfo(userInfo) {
      this.userInfo = userInfo;
    },
    // 清除 Token 和用户信息
    clearAuth() {
      this.token = "";
      this.userInfo = {};
    }
  },
  persist: piniaPersistConfig("club-user", ["token"]) // 指定 paths 参数
});
