<template>
  <div class="container">
    <div class="header">
      <h1>发送通知</h1>
    </div>
    <div class="form-container">
      <el-form ref="notificationFormRef" :model="notificationForm" label-width="120px">
        <el-form-item label="通知标题">
          <el-input v-model="notificationForm.title" placeholder="请输入通知标题"></el-input>
        </el-form-item>
        <el-form-item label="通知内容">
          <el-input type="textarea" v-model="notificationForm.content" placeholder="请输入通知内容"></el-input>
        </el-form-item>
        <el-form-item label="通知时间">
          <el-date-picker
            v-model="notificationForm.time"
            type="datetime"
            placeholder="请选择通知时间"
            :default-value="defaultTime"
          ></el-date-picker>
        </el-form-item>
        <el-form-item v-if="isAdmin" label="所属社团">
          <el-select v-model="notificationForm.clubId" placeholder="请选择社团" @change="fetchMembers">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-else label="所属社团">
          <el-input v-model="currentClub.name" disabled></el-input>
        </el-form-item>
        <el-form-item label="发送者">
          <el-input v-model="userStore.userName" disabled></el-input>
        </el-form-item>
        <el-form-item label="接收者">
          <el-select v-model="notificationForm.recipient" placeholder="请选择接收者">
            <el-option label="全体成员" value="all"></el-option>
            <el-option
              v-for="member in allMembers"
              :key="member.userId"
              :label="member.username"
              :value="member.userId"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="sendNotification">发送通知</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { sendMsg } from "@/api/modules/notification";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { listMembers } from "@/api/modules/membership";
import { getUserInfoByName } from "@/api/modules/user";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const allMembers = ref([]);
const notificationForm = ref({
  title: "",
  content: "",
  time: new Date(),
  clubId: "",
  sender: {
    userId: ""
  },
  recipient: ""
});
const currentClub = ref({});
const defaultTime = ref(new Date());

const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

const fetchMembers = async () => {
  try {
    const clubId = isAdmin ? notificationForm.value.clubId : currentClub.value.clubId;
    const response = await listMembers(clubId);
    if (response.code === 0) {
      allMembers.value = response.data.map(member => ({
        userId: member.user.userId,
        username: member.user.username
      }));
    } else {
      allMembers.value = [];
      ElMessage.error(response.message);
    }
  } catch (error) {
    allMembers.value = [];
    ElMessage.error("获取成员列表失败！");
  }
};

const sendNotification = async () => {
  try {
    const senderResponse = await getUserInfoByName({ username: userStore.userName });
    if (senderResponse.code === 0) {
      const sender = senderResponse.data;
      notificationForm.value.sender.userId = sender.userId;
      if (notificationForm.value.recipient === "all") {
        for (const member of allMembers.value) {
          const individualNotification = {
            title: notificationForm.value.title,
            content: notificationForm.value.content,
            time: new Date().toISOString(), // 确保时间格式正确
            clubId: notificationForm.value.clubId,
            recipient: { userId: member.userId },
            sender: { userId: sender.userId }
          };
          const response = await sendMsg(individualNotification);
          if (response.code !== 0) {
            ElMessage.error(response.message);
            return;
          }
        }
        ElMessage.success("通知发送成功！");
      } else {
        const notification = {
          title: notificationForm.value.title,
          content: notificationForm.value.content,
          time: new Date().toISOString(), // 确保时间格式正确
          clubId: notificationForm.value.clubId,
          recipient: { userId: notificationForm.value.recipient },
          sender: { userId: sender.userId }
        };
        console.log(notification);
        const response = await sendMsg(notification);
        if (response.code === 0) {
          ElMessage.success("通知发送成功！");
        } else {
          ElMessage.error(response.message);
        }
      }
    } else {
      ElMessage.error(senderResponse.message);
    }
    // 重置表单
    notificationForm.value.title = "";
    notificationForm.value.content = "";
    notificationForm.value.recipient = "";
  } catch (error) {
    ElMessage.error("发送通知失败！");
  }
};

onMounted(async () => {
  const senderResponse = await getUserInfoByName({ username: userStore.userName });
  if (senderResponse.code === 0) {
    notificationForm.value.sender.userId = senderResponse.data.userId;
  } else {
    ElMessage.error(senderResponse.message);
  }

  if (isAdmin) {
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
      if (allClubs.value.length > 0) {
        notificationForm.value.clubId = allClubs.value[0].clubId;
        fetchMembers();
      }
    } else {
      ElMessage.error(response.message);
    }
  } else if (isLeader) {
    const response = await getClubByLeaderID();
    if (response.code === 0) {
      currentClub.value = response.data;
      notificationForm.value.clubId = currentClub.value.clubId;
      fetchMembers();
    } else {
      ElMessage.error(response.message);
    }
  }
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
