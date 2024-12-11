import { defineStore } from "pinia";

export const useKeepAliveStore = defineStore({
  id: "club-keepAlive",
  state: () => ({
    keepAliveName: []
  }),
  actions: {
    // 添加 KeepAliveName
    async addKeepAliveName(name) {
      !this.keepAliveName.includes(name) && this.keepAliveName.push(name);
    },
    // 移除 KeepAliveName
    async removeKeepAliveName(name) {
      this.keepAliveName = this.keepAliveName.filter(item => item !== name);
    },
    // 设置 KeepAliveName
    async setKeepAliveName(keepAliveName) {
      this.keepAliveName = keepAliveName;
    }
  }
});
