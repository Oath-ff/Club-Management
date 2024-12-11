import { defineStore } from "pinia";
import piniaPersistConfig from "@/stores/helper/persist";

export const useUserStore = defineStore({
  id: "club-user",
  state: () => ({
    token: "",
    userInfo: { name: "Club User" }
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
    }
  },
  persist: piniaPersistConfig("club-user")
});
