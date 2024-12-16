<template>
  <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
    <el-form-item prop="username">
      <el-input v-model="loginForm.username" placeholder="请输入学号">
        <template #prefix>
          <el-icon class="el-input__icon">
            <user />
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
    <el-form-item prop="password">
      <el-input v-model="loginForm.password" type="password" placeholder="请输入密码" show-password autocomplete="new-password">
        <template #prefix>
          <el-icon class="el-input__icon">
            <lock />
          </el-icon>
        </template>
      </el-input>
    </el-form-item>
  </el-form>
  <div class="login-btn">
    <el-button :icon="CircleClose" round size="large" @click="resetForm(loginFormRef)"> 重置</el-button>
    <el-button
      :icon="UserFilled"
      round
      size="large"
      type="primary"
      :loading="loading"
      @click="login(loginFormRef)"
      :disabled="!isLoginEnabled"
    >
      登录
    </el-button>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount } from "vue";
import { useRouter } from "vue-router";
import { HOME_URL } from "@/config";
import { LOGIN_URL } from "@/config";
import { ElNotification } from "element-plus";
import { loginUser } from "@/api/modules/user";
import { useUserStore } from "@/stores/modules/user";
import { useTabsStore } from "@/stores/modules/tabs";
import { useKeepAliveStore } from "@/stores/modules/keepAlive";
import { initDynamicRouter } from "@/routers/modules/dynamicRouter";
import { CircleClose, UserFilled } from "@element-plus/icons-vue";
import md5 from "md5";
import { getTimeState } from "@/utils/index.js";

const router = useRouter();
const userStore = useUserStore();
const tabsStore = useTabsStore();
const keepAliveStore = useKeepAliveStore();

const loginFormRef = ref();
const loginRules = reactive({
  username: [
    { required: true, message: "请输入学号", trigger: "blur" },
    { pattern: /^(admin|[A-Z]{3}\d{5})$/, message: "请输入正确的学号格式", trigger: "blur" }
  ],
  password: [{ required: true, message: "请输入密码", trigger: "blur" }]
});

const loading = ref(false);
const loginForm = reactive({
  username: "",
  password: ""
});

// 计算属性用于启用登录按钮
const isLoginEnabled = computed(() => {
  return loginForm.password.length > 5;
});

// 登录
const login = formEl => {
  if (!formEl) return;
  formEl.validate(async valid => {
    if (!valid) return;
    loading.value = true;
    try {
      // 1.执行登录接口
      const response = await loginUser({
        username: loginForm.username,
        password: md5(loginForm.password)
      });
      const data = response.data;
      // 设置Token
      userStore.setToken(data);

      // 2.添加动态路由并获取用户信息和角色
      await initDynamicRouter();
      await authStore.fetchUserInfo();

      // 3.清空 tabs、keepAlive 数据
      await tabsStore.setTabs([]);
      await keepAliveStore.setKeepAliveName([]);

      // 4.跳转到首页
      setTimeout(() => {
        router.push(HOME_URL);
      }, 1000);
      ElNotification({
        title: getTimeState(),
        message: "欢迎登录校园社团管理系统",
        type: "success",
        duration: 3000
      });
    } catch (error) {
      ElNotification({
        title: "登录失败",
        message: error.response?.data?.message || "学号或密码错误，请重新尝试",
        type: "error",
        duration: 3000
      });
      useUserStore.clearAuth();
      router.replace(LOGIN_URL);
    } finally {
      loading.value = false;
    }
  });
};

// 重置表单
const resetForm = formEl => {
  if (!formEl) return;
  formEl.resetFields();
};

onMounted(() => {
  // 监听 enter 事件（调用登录）
  document.onkeydown = e => {
    if (e.code === "Enter" || e.code === "enter" || e.code === "NumpadEnter") {
      if (loading.value) return;
      login(loginFormRef.value);
    }
  };
});

onBeforeUnmount(() => {
  document.onkeydown = null;
});
</script>

<style scoped lang="scss">
@import "../index";
</style>
