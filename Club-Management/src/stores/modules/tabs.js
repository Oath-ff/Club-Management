// src/stores/modules/tabs.js
import router from "@/routers";
import { defineStore } from "pinia";
import { getUrlWithParams } from "@/utils";
import { useKeepAliveStore } from "./keepAlive";
import piniaPersistConfig from "@/stores/helper/persist";

const keepAliveStore = useKeepAliveStore();

export const useTabsStore = defineStore({
  id: "club-tabs",
  state: () => ({
    tabsMenuList: []
  }),
  actions: {
    // 添加 Tabs
    async addTabs(tabItem) {
      if (this.tabsMenuList.every(item => item.path !== tabItem.path)) {
        this.tabsMenuList.push(tabItem);
      }
      // 添加到缓存
      if (!keepAliveStore.keepAliveName.includes(tabItem.name) && tabItem.isKeepAlive) {
        keepAliveStore.addKeepAliveName(tabItem.path);
      }
    },
    // 移除 Tabs
    async removeTabs(tabPath, isCurrent = true) {
      if (isCurrent) {
        this.tabsMenuList.forEach((item, index) => {
          if (item.path !== tabPath) return;
          const nextTab = this.tabsMenuList[index + 1] || this.tabsMenuList[index - 1];
          if (!nextTab) return;
          router.push(nextTab.path);
        });
      }
      // 移除缓存
      const tabItem = this.tabsMenuList.find(item => item.path === tabPath);
      tabItem?.isKeepAlive && keepAliveStore.removeKeepAliveName(tabItem.path);
      // 设置标签页列表
      this.tabsMenuList = this.tabsMenuList.filter(item => item.path !== tabPath);
    },
    // 关闭侧边标签页
    async closeTabsOnSide(path, type) {
      const currentIndex = this.tabsMenuList.findIndex(item => item.path === path);
      if (currentIndex !== -1) {
        const range = type === "left" ? [0, currentIndex] : [currentIndex + 1, this.tabsMenuList.length];
        this.tabsMenuList = this.tabsMenuList.filter((item, index) => {
          return index < range[0] || index >= range[1] || !item.close;
        });
      }
      // 设置缓存组件
      const KeepAliveList = this.tabsMenuList.filter(item => item.isKeepAlive);
      keepAliveStore.setKeepAliveName(KeepAliveList.map(item => item.path));
    },
    // 关闭多个标签页
    async closeMultipleTab(tabsMenuValue) {
      this.tabsMenuList = this.tabsMenuList.filter(item => {
        return item.path === tabsMenuValue || !item.close;
      });
      // 设置缓存组件
      const KeepAliveList = this.tabsMenuList.filter(item => item.isKeepAlive);
      keepAliveStore.setKeepAliveName(KeepAliveList.map(item => item.path));
    },
    // 设置标签页列表
    async setTabs(tabsMenuList) {
      this.tabsMenuList = tabsMenuList;
    },
    // 设置标签页标题
    async setTabsTitle(title) {
      this.tabsMenuList.forEach(item => {
        if (item.path === getUrlWithParams()) item.title = title;
      });
    }
  },
  persist: piniaPersistConfig("club-tabs")
});
