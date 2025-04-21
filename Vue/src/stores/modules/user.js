import { defineStore } from "pinia";
import piniaPersistConfig from "@/stores/helper/persist";
export const useUserStore = defineStore({
  id: "club-user",
  state: () => ({
    token: "",
    userRole: [],
    avatar: "",
    userName: []
  }),
  getters: {},
  actions: {
    // 设置 Token
    setToken(newToken) {
      this.token = newToken;
    },
    // 设置用户信息
    setuserRole(role) {
      this.userRole = role;
    },
    // 设置用户头像
    setAvatar(avatar) {
      this.avatar = avatar;
    },
    // 设置用户名
    setUserName(name) {
      this.userName = name;
    },
    // 清除
    clearAuth() {
      this.token = "";
      this.userRole = [];
      this.avatar = "";
      this.userName = [];
    }
  },
  persist: piniaPersistConfig("club-user")
});
