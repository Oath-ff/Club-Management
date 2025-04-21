<template>
  <el-dialog v-model="dialogVisible" title="修改密码" width="500px" draggable @close="closeDialog">
    <div class="password-form">
      <el-form :model="passwordForm" :rules="rules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="旧密码" prop="oldPwd">
          <el-input v-model="passwordForm.oldPwd" type="password" show-password autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="新密码" prop="newPwd">
          <el-input v-model="passwordForm.newPwd" type="password" show-password autocomplete="off"></el-input>
        </el-form-item>
        <el-form-item label="确认新密码" prop="rePwd">
          <el-input v-model="passwordForm.rePwd" type="password" show-password autocomplete="off"></el-input>
        </el-form-item>
      </el-form>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="closeDialog">取消</el-button>
        <el-button type="primary" @click="handleConfirm">确认</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, reactive } from "vue";
import { updatePassword } from "@/api/modules/user";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";
import { LOGIN_URL } from "@/config";

const router = useRouter();
const userStore = useUserStore();
const dialogVisible = ref(false);
const passwordForm = reactive({
  oldPwd: "",
  newPwd: "",
  rePwd: ""
});
const passwordFormRef = ref(null);

const rules = {
  oldPwd: [{ required: true, message: "请输入旧密码", trigger: "blur" }],
  newPwd: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 5, max: 16, message: "新密码格式不正确，应为5到16个非空字符", trigger: "blur" }
  ],
  rePwd: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    {
      validator: (rule, value, callback) => {
        if (value !== passwordForm.newPwd) {
          callback(new Error("两次填写的密码不一致"));
        } else {
          callback();
        }
      },
      trigger: "blur"
    }
  ]
};

const openDialog = () => {
  dialogVisible.value = true;
};

const closeDialog = () => {
  dialogVisible.value = false;
  passwordFormRef.value.resetFields(); // 重置表单
};

const handleConfirm = () => {
  passwordFormRef.value.validate(async valid => {
    if (valid) {
      try {
        await ElMessageBox.confirm("您是否确认修改密码?", "温馨提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        });
        await updatePassword({
          old_pwd: passwordForm.oldPwd,
          new_pwd: passwordForm.newPwd,
          re_pwd: passwordForm.rePwd
        });
        ElMessage.success("密码修改成功，请重新登录！");

        // 清除用户登录状态
        userStore.setToken("");

        closeDialog();
        // 重定向到登录页面
        router.replace(LOGIN_URL);
      } catch (error) {
        ElMessage.error("密码修改失败！");
      }
    }
  });
};

defineExpose({ openDialog });
</script>

<style scoped lang="scss">
.password-form {
  padding: 10px;
}

.el-form-item {
  margin-bottom: 10px;
}

.dialog-footer {
  padding: 10px 20px;
}
</style>
