<template>
  <div class="container">
    <div class="header">
      <h1>活动签到</h1>
    </div>
    <div class="select-club" v-if="isAdmin">
      <el-form :model="clubForm" label-width="100px">
        <el-form-item label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchEvents">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="活动">
          <el-select v-model="selectedEventId" placeholder="请选择活动" @change="fetchParticipants">
            <el-option
              v-for="event in filteredClubEvents"
              :key="event.eventId"
              :label="event.name"
              :value="event.eventId"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="select-club" v-if="isLeader">
      <el-form :model="eventForm" label-width="100px">
        <el-form-item label="活动">
          <el-select v-model="selectedEventId" placeholder="请选择活动" @change="fetchParticipants">
            <el-option
              v-for="event in filteredClubEvents"
              :key="event.eventId"
              :label="event.name"
              :value="event.eventId"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <el-table :data="participants" stripe class="full-width-table">
        <el-table-column prop="membership.user.username" label="用户名" min-width="100"></el-table-column>
        <el-table-column prop="status" label="状态" min-width="100"></el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="scope">
            <el-button type="primary" size="mini" @click="confirmSignIn(scope.row)" :disabled="scope.row.status === '签到'">
              签到
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="participants.length === 0" class="no-participants">当前没有报名参加的成员。</div>
    </div>
    <!-- 签到确认弹窗 -->
    <el-dialog v-model="signInDialogVisible" title="确认签到">
      <span>确定要签到吗？</span>
      <template #footer>
        <el-button @click="signInDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="sign">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { listEventsByClubId } from "@/api/modules/event";
import { signIn, listParticipationsByEventId } from "@/api/modules/participation";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const clubEvents = ref([]);
const participants = ref([]);
const selectedClubId = ref(null);
const selectedEventId = ref(null);
const signInDialogVisible = ref(false);
const participantToSignIn = ref(null);

const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

// 过滤掉已过期的活动
const filteredClubEvents = computed(() => {
  const currentDate = new Date();
  return clubEvents.value.filter(event => new Date(event.date) > currentDate);
});

const fetchEvents = async () => {
  if (!selectedClubId.value) return;
  try {
    const response = await listEventsByClubId(selectedClubId.value);
    if (response.code === 0) {
      clubEvents.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取活动列表失败！");
  }
};

const fetchParticipants = async () => {
  if (!selectedEventId.value) return;

  try {
    const response = await listParticipationsByEventId(selectedEventId.value);
    if (response.code === 0) {
      participants.value = response.data;
      console.log(participants.value);
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取报名参加的成员列表失败！");
  }
};

const confirmSignIn = participant => {
  participantToSignIn.value = participant;
  signInDialogVisible.value = true;
};

const sign = async () => {
  if (!participantToSignIn.value) return;

  try {
    const response = await signIn(participantToSignIn.value.participationId);
    if (response.code === 0) {
      ElMessage.success("签到成功！");
      signInDialogVisible.value = false;
      fetchParticipants();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("签到失败！");
  }
};

onMounted(async () => {
  try {
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
        selectedClubId.value = response.data[0].clubId;
        fetchEvents();
      } else {
        ElMessage.error(response.message);
      }
    }
  } catch (error) {
    ElMessage.error("初始化失败！");
  }
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>

<style scoped lang="scss">
@import "./index.scss";
</style>
