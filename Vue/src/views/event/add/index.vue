<template>
  <div class="container">
    <div class="header">
      <h1>添加活动</h1>
    </div>
    <el-form :model="eventForm" ref="eventFormRef" label-width="100px">
      <el-form-item label="活动名称" prop="name">
        <el-input v-model="eventForm.name" placeholder="请输入活动名称"></el-input>
      </el-form-item>
      <el-form-item label="活动日期" prop="date">
        <el-date-picker v-model="eventForm.date" type="datetime" placeholder="选择活动日期" :disabled-date="disabledDate">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="活动地点" prop="location">
        <el-input v-model="eventForm.location" placeholder="请输入活动地点"></el-input>
      </el-form-item>
      <el-form-item label="活动描述" prop="description">
        <el-input type="textarea" v-model="eventForm.description" placeholder="请输入活动描述"></el-input>
      </el-form-item>
      <el-form-item v-if="isAdmin" label="所属社团" prop="club">
        <el-select v-model="eventForm.club.clubId" placeholder="请选择社团">
          <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitEvent">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { addEvent } from "@/api/modules/event";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const eventFormRef = ref(null);
const eventForm = ref({
  name: "",
  date: "",
  location: "",
  description: "",
  club: { clubId: null },
  status: "待审核"
});

const disabledDate = time => {
  return time.getTime() < Date.now() - 86400000;
};

const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

const fetchClubInfo = async () => {
  if (isAdmin) {
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } else if (isLeader) {
    const response = await getClubByLeaderID();
    if (response.code === 0) {
      eventForm.value.club.clubId = response.data[0].clubId;
    } else {
      ElMessage.error(response.message);
    }
  }
};

const submitEvent = async () => {
  try {
    await eventFormRef.value.validate();
    const response = await addEvent(eventForm.value);
    if (response.code === 0) {
      ElMessage.success("活动添加成功，请等待管理员审核！");
      eventForm.value = {
        name: "",
        date: "",
        location: "",
        description: "",
        club: { clubId: null }
      };
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("提交活动失败！");
  }
};

onMounted(fetchClubInfo);
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
