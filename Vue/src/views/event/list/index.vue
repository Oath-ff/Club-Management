<template>
  <div class="container">
    <div class="header">
      <h1>活动列表</h1>
    </div>
    <div class="select-club" v-if="isAdmin || isUser">
      <el-form :model="clubForm" label-width="100px">
        <el-form-item v-if="isAdmin" label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchEvents">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="isUser" label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchEvents">
            <el-option v-for="club in userClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="搜索活动">
          <el-input v-model="searchQuery" placeholder="输入活动ID或活动名称进行搜索">
            <template #append>
              <el-button @click="fetchEvents">搜索</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <el-table :data="paginatedEvents" stripe class="full-width-table">
        <el-table-column prop="name" label="活动名称" min-width="100"></el-table-column>
        <el-table-column prop="club.name" label="所属社团" min-width="100"></el-table-column>
        <el-table-column prop="date" label="活动日期" min-width="150"></el-table-column>
        <el-table-column prop="location" label="活动地点" min-width="100"></el-table-column>
        <el-table-column prop="description" label="活动描述" min-width="200"></el-table-column>
        <el-table-column label="活动状态" min-width="100">
          <template #default="scope">
            <span>{{ getEventStatus(scope.row.date) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="200">
          <template #default="scope">
            <el-button v-if="isAdmin || isLeader" type="danger" size="mini" @click="remove(scope.row.eventId)">
              删除活动
            </el-button>
            <el-button
              v-if="isAdmin || isLeader"
              type="primary"
              size="mini"
              @click="openEditDialog(scope.row)"
              :disabled="isEventExpired(scope.row.date)"
            >
              修改活动
            </el-button>
            <el-button v-if="isUser" type="primary" size="mini" @click="joinActivity(scope.row.eventId)">报名参加</el-button>
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
        >
        </el-pagination>
      </div>
    </div>
    <!-- 修改活动弹窗 -->
    <el-dialog v-model="dialogVisible" title="修改活动">
      <el-form ref="editFormRef" :model="editForm" label-width="100px">
        <el-form-item label="活动名称">
          <el-input v-model="editForm.name" />
        </el-form-item>
        <el-form-item label="活动日期">
          <el-date-picker v-model="editForm.date" type="datetime" :disabled-date="disabledDate" />
        </el-form-item>
        <el-form-item label="活动地点">
          <el-input v-model="editForm.location" />
        </el-form-item>
        <el-form-item label="活动描述">
          <el-input v-model="editForm.description" type="textarea" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitEdit">保存修改</el-button>
          <el-button @click="dialogVisible = false">取消</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { listEventsByClubId, findEvent, updateEvent, removeEvent } from "@/api/modules/event";
import { getUserInfo } from "@/api/modules/user";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { findMembership } from "@/api/modules/membership";
import { joinEvent } from "@/api/modules/participation";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const userClubs = ref([]);
const selectedClubId = ref(null);
const events = ref([]);
const searchQuery = ref("");
const currentPage = ref(1);
const pageSize = ref(10);
const dialogVisible = ref(false);
const editForm = ref({
  eventId: null,
  name: "",
  date: "",
  location: "",
  description: "",
  status: "审核通过"
});
const editFormRef = ref(null);
const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";
const isUser = userStore.userRole === "用户";

const fetchEvents = async () => {
  try {
    const params = {};
    if (searchQuery.value && !isNaN(searchQuery.value)) {
      params.eventId = parseInt(searchQuery.value, 10);
    } else if (searchQuery.value) {
      params.name = searchQuery.value;
    }
    if (Object.keys(params).length > 0) {
      const response = await findEvent(params);
      if (response.code === 0) {
        events.value = [response.data];
      } else {
        events.value = [];
        ElMessage.error(response.message);
      }
    } else {
      const clubId = isAdmin || isUser ? selectedClubId.value : selectedClubId.value;
      const response = await listEventsByClubId(clubId);
      if (response.code === 0) {
        events.value = response.data;
      } else {
        events.value = [];
        ElMessage.error(response.message);
      }
    }
  } catch (error) {
    events.value = [];
    ElMessage.error("获取活动列表失败！");
  }
};

const remove = async eventId => {
  try {
    const response = await removeEvent(eventId);
    if (response.code === 0) {
      ElMessage.success("删除活动成功！");
      await fetchEvents();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("删除活动失败！");
  }
};

const openEditDialog = event => {
  editForm.value = { ...event, date: new Date(event.date) };
  dialogVisible.value = true;
};

const submitEdit = async () => {
  if (editForm.value.date < new Date()) {
    ElMessage.error("活动日期不能早于当前时间！");
    return;
  }
  try {
    const response = await updateEvent(editForm.value.eventId, {
      name: editForm.value.name,
      date: editForm.value.date,
      location: editForm.value.location,
      description: editForm.value.description,
      status: editForm.value.status
    });
    if (response.code === 0) {
      ElMessage.success("活动更新成功！");
      dialogVisible.value = false;
      fetchEvents();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("提交修改失败！");
  }
};

const joinActivity = async eventId => {
  try {
    let userUserId = null;
    const userResponse = await getUserInfo();
    if (userResponse.code === 0) {
      userUserId = userResponse.data.userId;
    }
    const response = await joinEvent(eventId, { userId: userUserId });
    if (response.code === 0) {
      ElMessage.success("报名参加成功！");
      await fetchEvents();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("报名参加失败！");
  }
};

const disabledDate = time => {
  return time.getTime() < Date.now();
};

const getEventStatus = date => {
  const eventDate = new Date(date);
  const currentDate = new Date();
  if (eventDate > currentDate) {
    return "活动还未开始";
  } else {
    return "活动已结束";
  }
};

const isEventExpired = date => {
  return new Date(date) < new Date();
};

const paginatedEvents = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return events.value.slice(start, end);
});

const handlePageChange = page => {
  currentPage.value = page;
};

onMounted(async () => {
  try {
    if (isAdmin) {
      const response = await getAllClubs();
      if (response.code === 0) {
        allClubs.value = response.data;
        if (allClubs.value.length > 0) {
          selectedClubId.value = allClubs.value[0].clubId;
          fetchEvents();
        }
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
    } else if (isUser) {
      const params = {};
      if (searchQuery.value && !isNaN(searchQuery.value)) {
        params.userId = parseInt(searchQuery.value, 10);
      } else if (searchQuery.value) {
        params.username = searchQuery.value;
      }
      params.username = userStore.userName;
      const response = await findMembership(params);
      if (response.code === 0) {
        userClubs.value = response.data.map(item => item.club);
        if (userClubs.value.length > 0) {
          selectedClubId.value = userClubs.value[0].clubId;
          fetchEvents();
        }
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
