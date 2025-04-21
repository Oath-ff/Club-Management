<template>
  <div>
    <!--登录表单-->
    <el-form v-if="isRegister" ref="loginFormRef" :model="loginForm" :rules="loginRules" size="large">
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
      <el-form-item class="register-link">
        <el-link type="info" :underline="false" @click="isRegister = false">还未注册，前往注册 →</el-link>
      </el-form-item>
    </el-form>
    <!--注册表单-->
    <el-form v-else ref="registerFormRef" :model="registerForm" :rules="registerRules" size="large">
      <el-form-item prop="username">
        <el-input v-model="registerForm.username" placeholder="请输入学号">
          <template #prefix>
            <el-icon class="el-input__icon">
              <user />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          type="password"
          placeholder="请输入密码"
          show-password
          autocomplete="new-password"
        >
          <template #prefix>
            <el-icon class="el-input__icon">
              <lock />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <el-form-item prop="repassword">
        <el-input
          v-model="registerForm.repassword"
          type="password"
          placeholder="请再次输入密码"
          show-password
          autocomplete="new-password"
        >
          <template #prefix>
            <el-icon class="el-input__icon">
              <lock />
            </el-icon>
          </template>
        </el-input>
      </el-form-item>
      <div class="login-btn">
        <el-button :icon="CircleClose" round size="large" @click="resetForm(registerFormRef)"> 重置</el-button>
        <el-button :icon="UserFilled" round size="large" type="primary" :loading="loading" @click="register(registerFormRef)">
          注册
        </el-button>
      </div>
      <el-form-item class="register-link">
        <el-link type="info" :underline="false" @click="isRegister = true">已注册，前往登录 →</el-link>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, computed } from "vue";
import { useRouter } from "vue-router";
import { HOME_URL, LOGIN_URL } from "@/config";
import { ElNotification } from "element-plus";
import { loginUser, registerUser } from "@/api/modules/user";
import { useUserStore } from "@/stores/modules/user";
import { useTabsStore } from "@/stores/modules/tabs";
import { useKeepAliveStore } from "@/stores/modules/keepAlive";
import { initDynamicRouter } from "@/routers/modules/dynamicRouter";
import { CircleClose, UserFilled } from "@element-plus/icons-vue";
import { getTimeState } from "@/utils/index";

//控制注册与登录表单的显示， 默认显示登录表单
const isRegister = ref(true);
const router = useRouter();
const userStore = useUserStore();
const tabsStore = useTabsStore();
const keepAliveStore = useKeepAliveStore();

const loading = ref(false);
const loginFormRef = ref();
const loginRules = reactive({
  username: [
    { required: true, message: "请输入学号", trigger: "blur" },
    { pattern: /^(admin|[A-Z]{3}\d{5})$/, message: "请输入正确的学号格式", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 5, max: 16, message: "长度为5~16位非空字符", trigger: "blur" }
  ]
});
const loginForm = reactive({
  username: "",
  password: ""
});

const registerFormRef = ref();
const registerRules = reactive({
  username: [
    { required: true, message: "请输入学号", trigger: "blur" },
    { pattern: /^(admin|[A-Z]{3}\d{5})$/, message: "请输入正确的学号格式", trigger: "blur" }
  ],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 5, max: 16, message: "长度为5~16位非空字符", trigger: "blur" }
  ],
  repassword: [
    { required: true, message: "请再次输入密码", trigger: "blur" },
    { min: 5, max: 16, message: "长度为5~16位非空字符", trigger: "blur" }
  ]
});
const registerForm = reactive({
  username: "",
  password: "",
  repassword: ""
});
// 计算属性用于启用登录按钮
const isLoginEnabled = computed(() => {
  return loginForm.password.length >= 5;
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
        password: loginForm.password
      });
      // 确认响应数据
      const { code, data, message } = response;
      if (code === 0) {
        const token = data;
        await userStore.setToken(token);
        // 创建动态路由
        await initDynamicRouter();
        // 清空 tabs、keepAlive 数据
        await tabsStore.setTabs([]);
        await keepAliveStore.setKeepAliveName([]);
        // 跳转到首页
        setTimeout(() => {
          router.push(HOME_URL);
        }, 1000);
        ElNotification({
          title: getTimeState(),
          message: "欢迎登录校园社团管理系统",
          type: "success",
          duration: 3000
        });
      } else {
        throw new Error(message || "登录失败");
      }
    } catch (error) {
      ElNotification({
        title: "登录失败",
        message: error.response?.data?.message || "学号或密码错误，请重新尝试",
        type: "error",
        duration: 3000
      });
      userStore.clearAuth();
      router.replace(LOGIN_URL);
    } finally {
      loading.value = false;
    }
  });
};

const register = formEl => {
  if (!formEl) return;
  formEl.validate(async valid => {
    if (!valid) return;
    loading.value = true;
    try {
      await registerUser({
        username: registerForm.username,
        password: registerForm.password
      });
      // 注册成功后切换到登录表单
      isRegister.value = true;
      ElNotification({
        title: "注册成功",
        message: "请使用您的账户信息登录",
        type: "success",
        duration: 3000
      });
    } catch (error) {
      ElNotification({
        title: "注册失败",
        message: error.response?.data?.message || "学号或密码错误，请重新尝试",
        type: "error",
        duration: 3000
      });
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
  // 监听 enter 事件（调用注册）
  document.onkeydown = e => {
    if (e.code === "Enter" || e.code === "enter" || e.code === "NumpadEnter") {
      if (loading.value) return;
      register(registerFormRef.value);
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
