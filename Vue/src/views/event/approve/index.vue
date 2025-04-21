<template>
  <div class="container">
    <!-- 页面标题 -->
    <div class="header">
      <h1>审核活动列表</h1>
    </div>
    <!-- 社团选择 -->
    <div v-if="isAdmin" class="select-club">
      <el-form :model="clubForm" label-width="100px">
        <el-form-item label="社团：" class="centered-form-item">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchPendingEvents">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!-- 活动列表 -->
    <div class="content">
      <el-table :data="paginatedEvents" stripe>
        <el-table-column prop="name" label="活动名称" min-width="150"></el-table-column>
        <el-table-column prop="club.name" label="所属社团" min-width="150"></el-table-column>
        <el-table-column prop="club.leader.username" label="社团团长" min-width="150"></el-table-column>
        <el-table-column prop="date" label="活动时间" min-width="150"></el-table-column>
        <el-table-column prop="location" label="活动地点" min-width="150"></el-table-column>
        <el-table-column prop="description" label="活动描述" min-width="200">
          <template #default="scope">
            <el-tooltip class="item" effect="dark" :content="scope.row.description" placement="top">
              <span>{{ scope.row.description }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column v-if="isAdmin" label="操作" min-width="200">
          <template #default="scope">
            <el-button type="success" size="mini" @click="approveEventConfirm(scope.row.eventId)">审核通过</el-button>
            <el-button type="danger" size="mini" @click="rejectEventConfirm(scope.row.eventId)">审核不通过</el-button>
          </template>
        </el-table-column>
        <el-table-column v-if="isLeader" prop="status" label="活动状态" min-width="150"></el-table-column>
      </el-table>
      <div v-if="paginatedEvents.length === 0" class="no-records">当前没有活动记录。</div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="events.length"
        ></el-pagination>
      </div>
    </div>
    <!-- 审核确认弹窗 -->
    <el-dialog title="确认审核" v-model="approveDialogVisible">
      <p>确定要通过此活动的审核吗？</p>
      <template #footer>
        <el-button @click="approveDialogVisible = false">取消</el-button>
        <el-button type="success" @click="approveEventAction">通过</el-button>
      </template>
    </el-dialog>
    <el-dialog title="确认拒绝审核" v-model="rejectDialogVisible">
      <p>确定要拒绝此活动的审核吗？</p>
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="rejectEventAction">拒绝</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { getEventList, getpendingEvent, approveEvent, rejectEvent } from "@/api/modules/event";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { getUserInfo } from "@/api/modules/user";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";
const allClubs = ref([]);
const events = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const selectedClubId = ref(null);
const approveDialogVisible = ref(false);
const rejectDialogVisible = ref(false);
const selectedEventId = ref(null);
const clubId = ref(null);

// 获取待审核活动或所有活动
const fetchPendingEvents = async () => {
  try {
    if (isLeader && !clubId.value) {
      ElMessage.error("无法获取当前社团，请检查初始化！");
      return;
    }
    const response = isAdmin ? await getpendingEvent(selectedClubId.value) : await getEventList(clubId.value);
    if (response.code === 0) {
      events.value = response.data;
      console.log(events.value);
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取活动列表失败！");
  }
};

// 审核通过确认
const approveEventConfirm = eventId => {
  selectedEventId.value = eventId;
  approveDialogVisible.value = true;
};

// 审核通过
const approveEventAction = async () => {
  try {
    const response = await approveEvent(selectedEventId.value);
    if (response.code === 0) {
      ElMessage.success("活动审核通过！");
      approveDialogVisible.value = false;
      fetchPendingEvents();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("活动审核失败！");
  }
};

// 拒绝审核确认
const rejectEventConfirm = eventId => {
  selectedEventId.value = eventId;
  rejectDialogVisible.value = true;
};

// 拒绝审核
const rejectEventAction = async () => {
  try {
    const response = await rejectEvent(selectedEventId.value);
    if (response.code === 0) {
      ElMessage.success("活动审核拒绝成功！");
      rejectDialogVisible.value = false;
      fetchPendingEvents();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("活动审核失败！");
  }
};

// 计算当前页的数据
const paginatedEvents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return events.value.slice(start, end);
});

// 改变页码
const handlePageChange = page => {
  currentPage.value = page;
};

// 初始化
onMounted(async () => {
  if (isAdmin) {
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
      selectedClubId.value = allClubs.value[0]?.clubId || null;
      fetchPendingEvents();
    } else {
      ElMessage.error(response.message);
    }
  } else if (isLeader) {
    try {
      const userResponse = await getUserInfo(userStore.userName);
      const userId = userResponse.data.userId;
      const clubResponse = await getClubByLeaderID(userId);
      if (clubResponse.code === 0) {
        clubId.value = clubResponse.data[0].clubId;
        fetchPendingEvents();
      } else {
        ElMessage.error("未能获取团长所属的社团信息！");
      }
    } catch (error) {
      ElMessage.error("初始化失败！");
    }
  }
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
