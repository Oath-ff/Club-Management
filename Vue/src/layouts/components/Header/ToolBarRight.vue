<template>
  <div class="tool-bar-ri">
    <div class="header-icon">
      <SearchMenu id="searchMenu" />
      <Fullscreen id="fullscreen" />
    </div>
    <span class="username">{{ userName }}</span>
    <Avatar />
  </div>
</template>

<script setup>
import { computed } from "vue";
import { useUserStore } from "@/stores/modules/user";
import SearchMenu from "./components/SearchMenu.vue";
import Fullscreen from "./components/Fullscreen.vue";
import Avatar from "./components/Avatar.vue";

const userStore = useUserStore();
const userName = computed(() => {
  if (userStore.token) {
    return userStore.userName;
  } else {
    return "游客";
  }
});
</script>

<style scoped lang="scss">
.tool-bar-ri {
  display: flex;
  align-items: center;
  justify-content: center;
  padding-right: 25px;
  .header-icon {
    display: flex;
    align-items: center;
    & > * {
      margin-left: 21px;
      color: var(--el-header-text-color);
    }
  }
  .username {
    margin: 0 20px;
    font-size: 15px;
    color: var(--el-header-text-color);
  }
  button {
    margin-left: 20px;
    padding: 5px 10px;
    font-size: 14px;
    cursor: pointer;
  }
}
</style>
