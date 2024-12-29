import { defineStore } from "pinia";
import piniaPersistConfig from "@/stores/helper/persist";

export const useUserStore = defineStore({
  id: "club-user",
  state: () => ({
    token: ""
  }),
  getters: {},
  actions: {
    // 设置 Token
    setToken(newToken) {
      this.token = newToken;
    },
    // 清除 Token 和用户信息
    clearAuth() {
      this.token = "";
    }
  },
  persist: piniaPersistConfig("club-user")
});
