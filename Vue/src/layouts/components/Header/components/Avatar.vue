<template>
  <el-dropdown trigger="click">
    <div class="avatar">
      <img :src="userAvatar" alt="avatar" />
    </div>
    <template #dropdown>
      <el-dropdown-menu>
        <el-dropdown-item v-if="isLoggedIn" @click="openDialog('infoRef')">
          <el-icon><User /></el-icon>个人信息
        </el-dropdown-item>
        <el-dropdown-item v-if="isLoggedIn" @click="openDialog('passwordRef')">
          <el-icon><Edit /></el-icon>修改密码
        </el-dropdown-item>
        <el-dropdown-item v-if="isLoggedIn" divided @click="logout">
          <el-icon><SwitchButton /></el-icon>注销
        </el-dropdown-item>
        <el-dropdown-item v-else @click="redirectToLogin">
          <el-icon><SwitchButton /></el-icon>未登录，请前往登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
  <!-- infoDialog -->
  <InfoDialog ref="infoRef"></InfoDialog>
  <!-- passwordDialog -->
  <PasswordDialog ref="passwordRef"></PasswordDialog>
</template>

<script setup>
import { ref, computed } from "vue";
import { HOME_URL, LOGIN_URL } from "@/config";
import { useRouter } from "vue-router";
import { logoutUser } from "@/api/modules/user";
import { useUserStore } from "@/stores/modules/user";
import { ElMessageBox, ElMessage } from "element-plus";
import InfoDialog from "./InfoDialog.vue";
import PasswordDialog from "./PasswordDialog.vue";
import initAvatar from "@/assets/images/avatar.gif";

const router = useRouter();
const userStore = useUserStore();
const isLoggedIn = computed(() => !!userStore.token);

//头像生成
const userAvatar = computed(() => {
  // if (userStore.avatar) {
  //   return userStore.avatar;
  // } else {
  //   return initAvatar;
  // }
  return initAvatar;
});

// 退出登录
const logout = () => {
  ElMessageBox.confirm("您是否确认退出登录?", "温馨提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning"
  }).then(async () => {
    // 执行退出登录接口
    await logoutUser();
    // 清除 Token 和用户信息
    userStore.clearAuth();
    // 设置角色为游客
    userStore.setUserRole("游客");
    // 刷新页面
    await router.replace(HOME_URL);
    window.location.reload();
    ElMessage.success("退出登录成功！");
  });
};

// 跳转到登录页面
const redirectToLogin = () => {
  router.push({ path: LOGIN_URL });
};

// 打开修改密码和个人信息弹窗
const infoRef = ref(null);
const passwordRef = ref(null);
const openDialog = ref => {
  if (ref === "infoRef") infoRef.value?.openDialog();
  if (ref === "passwordRef") passwordRef.value?.openDialog();
};
</script>

<style scoped lang="scss">
.avatar {
  width: 40px;
  height: 40px;
  overflow: hidden;
  cursor: pointer;
  border-radius: 50%;
  img {
    width: 100%;
    height: 100%;
  }
}
</style>
