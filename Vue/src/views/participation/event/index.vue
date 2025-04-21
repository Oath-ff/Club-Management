<template>
  <div class="container">
    <div class="header">
      <h1>活动参与记录</h1>
    </div>
    <div class="search-select-box">
      <el-form ref="searchFormRef" :model="searchForm" label-width="100px">
        <el-form-item v-if="isAdmin" label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchEvents">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="搜索活动" class="search-event">
          <el-input v-model="searchForm.query" placeholder="请输入活动ID或名称">
            <template #append>
              <el-button type="primary" @click="fetchEvents(true)">搜索</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <el-table :data="paginatedEvents" stripe class="full-width-table">
        <el-table-column prop="name" label="活动名称" min-width="150"></el-table-column>
        <el-table-column prop="club.name" label="所属社团" min-width="100"></el-table-column>
        <el-table-column prop="time" label="活动时间" min-width="150" :formatter="formatTime"></el-table-column>
        <el-table-column prop="location" label="活动地点" min-width="100"></el-table-column>
        <el-table-column prop="status" label="活动状态" min-width="100" :formatter="formatStatus"></el-table-column>
        <el-table-column prop="description" label="活动描述" min-width="200"></el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="scope">
            <el-button type="primary" size="mini" @click="viewParticipationInfo(scope.row)">查看参与信息</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="events.length === 0" class="no-events">当前没有活动。</div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="events.length"
        />
      </div>
    </div>
    <!-- 查看参与信息弹窗 -->
    <el-dialog v-model="participationInfoDialogVisible" title="参与信息">
      <div class="participation-info">
        <p><strong>活动名称：</strong>{{ participationInfo.eventName }}</p>
        <p><strong>活动状态：</strong>{{ participationInfo.eventStatus }}</p>
        <p><strong>报名数：</strong>{{ participationInfo.signUpCount }}</p>
        <p><strong>签到数：</strong>{{ participationInfo.signInCount }}</p>
      </div>
      <template #footer>
        <el-button @click="participationInfoDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { listParticipationsByEventId } from "@/api/modules/participation";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { listEventsByClubId, findEvent } from "@/api/modules/event";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const events = ref([]);
const participationInfo = ref({});
const selectedClubId = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const searchForm = ref({ query: "" });
const participationInfoDialogVisible = ref(false);

const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

const formatTime = (row, column, cellValue) => {
  return cellValue ? new Date(cellValue.replace(" ", "T")).toLocaleString() : "";
};

const formatStatus = (row, column, cellValue) => {
  const eventDate = new Date(row.time);
  const currentDate = new Date();
  if (eventDate > currentDate) {
    return "活动还未开始";
  } else {
    return "活动已结束";
  }
};

const fetchEvents = async (isSearch = false) => {
  try {
    const params = {};
    if (isSearch && searchForm.value.query && !isNaN(searchForm.value.query)) {
      params.eventId = parseInt(searchForm.value.query, 10);
    } else if (isSearch && searchForm.value.query) {
      params.name = searchForm.value.query;
    }

    const clubParams = {};
    if (isAdmin && selectedClubId.value) {
      clubParams.clubId = selectedClubId.value;
    } else if (isLeader) {
      const response = await getClubByLeaderID();
      if (response.code === 0) {
        clubParams.clubId = response.data[0].clubId;
      } else {
        ElMessage.error(response.message);
        return;
      }
    }
    if (isSearch && Object.keys(params).length > 0) {
      const response = await findEvent(params);
      if (response.code === 0) {
        events.value = [response.data];
      } else {
        events.value = [];
        ElMessage.error(response.message);
      }
    } else if (Object.keys(clubParams).length > 0) {
      const response = await listEventsByClubId(clubParams.clubId);
      if (response.code === 0) {
        events.value = response.data.map(event => ({
          ...event,
          status: new Date(event.time) > new Date() ? "活动还未开始" : "活动已结束",
          time: event.date
        }));
      } else {
        events.value = [];
        ElMessage.error(response.message);
      }
    } else {
      events.value = [];
      ElMessage.error("请提供搜索条件或选择一个社团");
    }
  } catch (error) {
    events.value = [];
    ElMessage.error("获取活动列表失败！");
  }
};

const viewParticipationInfo = async event => {
  try {
    const response = await listParticipationsByEventId(event.eventId);
    if (response.code === 0) {
      participationInfo.value = {
        eventName: event.name,
        eventStatus: event.status,
        signUpCount: response.data.length,
        signInCount: response.data.filter(p => p.status === "签到").length
      };
      participationInfoDialogVisible.value = true;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取参与信息失败！");
  }
};

// 计算属性，当前页显示的活动列表
const paginatedEvents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return events.value.slice(start, end);
});

// 处理页码改变的函数
const handlePageChange = page => {
  currentPage.value = page;
};

onMounted(async () => {
  fetchEvents();
  if (isAdmin) {
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
      if (allClubs.value.length > 0) {
        selectedClubId.value = allClubs.value[0].clubId;
      }
    } else {
      ElMessage.error(response.message);
    }
  }
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
