<template>
  <el-dialog v-model="dialogVisible" title="个人信息" width="500px" draggable>
    <div class="user-info">
      <div class="avatar-wrapper">
        <img :src="userAvatar" alt="avatar" @click="triggerUpload" />
        <input type="file" ref="fileInput" @change="handleFileChange" style="display: none" />
        <p class="click-to-change">点击头像可以更换头像</p>
      </div>
      <div class="user-details">
        <p>用户名: {{ userInfo.username }}</p>
        <p>角色: {{ userInfo.role }}</p>

        <!-- 昵称：为空时显示文本框 -->
        <div v-if="!userInfo.nickname">
          <el-input v-model="editForm.nickname" placeholder="请输入姓名" class="input-inline"></el-input>
        </div>
        <p v-else>姓名: {{ userInfo.nickname }}</p>

        <!-- 联系电话：为空时显示文本框 -->
        <div v-if="!userInfo.phoneNumber">
          <el-input v-model="editForm.phoneNumber" placeholder="请输入11位联系电话" class="input-inline"></el-input>
        </div>
        <p v-else>联系电话: {{ userInfo.phoneNumber }}</p>

        <el-button v-if="!userInfo.nickname || !userInfo.phoneNumber" type="primary" class="save-button" @click="submitForm">
          保存
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, reactive, computed } from "vue";
import { getUserInfo, updateAvatar, updateInfo } from "@/api/modules/user";
import { ElMessage } from "element-plus";
import initAvatar from "@/assets/images/avatar.gif";

const dialogVisible = ref(false);
const fileInput = ref(null);
const userInfo = reactive({
  username: "",
  role: "",
  userPic: "",
  nickname: "",
  phoneNumber: ""
});
const editForm = reactive({
  nickname: "",
  phoneNumber: ""
});

const openDialog = async () => {
  dialogVisible.value = true;
  const response = await getUserInfo();
  const data = response.data;

  // 初始化用户信息
  userInfo.username = data.username;
  userInfo.role = data.role;
  userInfo.userPic = data.userPic;
  userInfo.nickname = data.nickname;
  userInfo.phoneNumber = data.phoneNumber;

  // 初始化表单
  editForm.nickname = data.nickname || "";
  editForm.phoneNumber = data.phoneNumber || "";
};

//头像生成
const userAvatar = computed(() => {
  return userInfo.userPic || initAvatar;
});

const triggerUpload = () => {
  fileInput.value.click();
};

const handleFileChange = async event => {
  const file = event.target.files[0];
  if (file) {
    const formData = new FormData();
    formData.append("userPic", file);

    try {
      const response = await updateAvatar(formData);
      ElMessage.success("头像更新成功！");
      userInfo.userPic = URL.createObjectURL(file); // 更新本地显示的头像 URL
    } catch (error) {
      ElMessage.error("头像更新失败！");
    }
  }
};

const validatePhoneNumber = phoneNumber => {
  const phoneRegex = /^[0-9]{11}$/; // 匹配11位数字的正则表达式
  return phoneRegex.test(phoneNumber);
};

const submitForm = async () => {
  if (!validatePhoneNumber(editForm.phoneNumber)) {
    ElMessage.error("联系电话格式不正确！");
    return; // 阻止保存操作
  }

  try {
    const response = await updateInfo(userInfo.username, editForm.nickname, editForm.phoneNumber);
    if (response.code === 0) {
      ElMessage.success("信息更新成功！");
      // 更新本地用户信息
      userInfo.nickname = editForm.nickname;
      userInfo.phoneNumber = editForm.phoneNumber;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("信息更新失败！");
  }
};

// 暴露 openDialog 方法给父组件
defineExpose({
  openDialog
});
</script>

<style scoped lang="scss">
.user-info {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px;

  .avatar-wrapper {
    position: relative;
    cursor: pointer;
    text-align: center;

    img {
      width: 100px;
      height: 100px;
      border-radius: 50%;
      object-fit: cover;
      border: 2px solid #ddd;
      transition: border-color 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }

    .click-to-change {
      margin-top: 5px;
      font-size: 12px;
      color: #666;
      text-align: center;
    }
  }

  .user-details {
    margin-top: 20px;
    text-align: center;

    p {
      margin: 5px 0;
      font-size: 14px;
      color: #333;
    }
  }

  .input-inline {
    margin: 5px 0;
    width: 200px;
  }

  .save-button {
    margin-top: 15px;
  }
}
</style>
