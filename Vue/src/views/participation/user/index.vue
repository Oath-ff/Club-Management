<template>
  <div class="container">
    <div class="header">
      <h1>成员参与活动记录</h1>
    </div>
    <div class="search-select-box" v-if="isAdmin || isLeader">
      <el-form ref="searchFormRef" :model="searchForm" label-width="100px">
        <el-form-item v-if="isAdmin" label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchMembers">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="搜索成员" class="search-member">
          <el-input v-model="searchForm.query" placeholder="请输入用户ID或用户名">
            <template #append>
              <el-button type="primary" @click="fetchMembers">搜索</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <el-table :data="paginatedMembers" stripe class="full-width-table" v-if="isAdmin || isLeader">
        <el-table-column prop="user.username" label="用户名" min-width="100"></el-table-column>
        <el-table-column prop="club.name" label="所属社团" min-width="100"></el-table-column>
        <el-table-column prop="totalParticipations" label="参加活动总数" min-width="100"></el-table-column>
        <el-table-column prop="signInCount" label="已签到活动数" min-width="100"></el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="scope">
            <el-button type="primary" size="mini" @click="viewParticipationList(scope.row)">查看参加活动列表</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-table :data="userParticipations" stripe class="full-width-table" v-if="isUser">
        <el-table-column prop="event.name" label="活动名称" min-width="150"></el-table-column>
        <el-table-column prop="event.club.name" label="所属社团" min-width="100"></el-table-column>
        <el-table-column prop="event.date" label="活动时间" min-width="150" :formatter="formatTime"></el-table-column>
        <el-table-column prop="status" label="活动状态" min-width="100" :formatter="formatStatus"></el-table-column>
        <el-table-column prop="participationStatus" label="参加状态" min-width="100"></el-table-column>
      </el-table>
      <div v-if="members.length === 0 && (isAdmin || isLeader)" class="no-members">当前没有成员。</div>
      <div v-if="userParticipations.length === 0 && isUser" class="no-events">当前没有参与的活动。</div>
      <!-- 分页 -->
      <div class="pagination" v-if="isAdmin || isLeader">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="members.length"
        >
        </el-pagination>
      </div>
    </div>
    <!-- 查看参与活动列表弹窗 -->
    <el-dialog v-model="participationListDialogVisible" title="参加活动列表">
      <el-table :data="participations" stripe class="full-width-table">
        <el-table-column prop="event.name" label="活动名称" min-width="150"></el-table-column>
        <el-table-column prop="status" label="状态" min-width="100"></el-table-column>
      </el-table>
      <div v-if="participations.length === 0" class="no-participations">当前没有参与的活动。</div>
      <template #footer>
        <el-button @click="participationListDialogVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { listParticipationsByUserId } from "@/api/modules/participation";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { findMembership, listMembers } from "@/api/modules/membership";
import { useUserStore } from "@/stores/modules/user";
import { getUserInfoByName } from "@/api/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const members = ref([]);
const participations = ref([]);
const userParticipations = ref([]);
const selectedClubId = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const searchForm = ref({ query: "" });
const participationListDialogVisible = ref(false);
const participationListUser = ref(null);

const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";
const isUser = userStore.userRole === "用户";

const formatTime = (row, column, cellValue) => {
  return cellValue ? new Date(cellValue.replace(" ", "T")).toLocaleString() : "";
};

const formatStatus = (row, column, cellValue) => {
  const eventDate = new Date(row.event.date);
  const currentDate = new Date();
  if (eventDate > currentDate) {
    return "活动还未开始";
  } else {
    return "活动已结束";
  }
};

const fetchMembers = async () => {
  try {
    const params = {};
    if (searchForm.value.query && !isNaN(searchForm.value.query)) {
      params.userId = parseInt(searchForm.value.query, 10);
    } else if (searchForm.value.query) {
      params.username = searchForm.value.query;
    }
    if (Object.keys(params).length > 0) {
      const response = await findMembership(params);
      if (response.code === 0) {
        const member = response.data;
        const participationResponse = await listParticipationsByUserId(member.user.userId);
        if (participationResponse.code === 0) {
          member.totalParticipations = participationResponse.data.length;
          member.signInCount = participationResponse.data.filter(p => p.status === "签到").length;
        } else {
          member.totalParticipations = 0;
          member.signInCount = 0;
        }
        members.value = [member];
      } else {
        members.value = [];
        ElMessage.error(response.message);
      }
    } else {
      const clubId = isAdmin ? selectedClubId.value : selectedClubId.value;
      const response = await listMembers(clubId);
      if (response.code === 0) {
        const memberData = await Promise.all(
          response.data.map(async member => {
            const participationResponse = await listParticipationsByUserId(member.user.userId);
            if (participationResponse.code === 0) {
              return {
                ...member,
                totalParticipations: participationResponse.data.length,
                signInCount: participationResponse.data.filter(p => p.status === "签到").length
              };
            } else {
              return {
                ...member,
                totalParticipations: 0,
                signInCount: 0
              };
            }
          })
        );
        members.value = memberData;
      } else {
        members.value = [];
        ElMessage.error(response.message);
      }
    }
  } catch (error) {
    members.value = [];
    ElMessage.error("获取成员列表失败！");
  }
};

const fetchUserParticipations = async () => {
  try {
    const userResponse = await getUserInfoByName({ username: userStore.userName });
    if (userResponse.code === 0) {
      const response = await listParticipationsByUserId(userResponse.data.userId);
      if (response.code === 0) {
        userParticipations.value = response.data.map(participation => ({
          ...participation,
          event: participation.event,
          status: formatStatus(participation),
          participationStatus: participation.status === "签到" ? "已签到" : "未签到"
        }));
      } else {
        userParticipations.value = [];
        ElMessage.error(response.message);
      }
    } else {
      ElMessage.error(userResponse.message);
    }
  } catch (error) {
    userParticipations.value = [];
    ElMessage.error("获取参与记录失败！");
  }
};

const viewParticipationList = async member => {
  participationListUser.value = member.user;
  try {
    const response = await listParticipationsByUserId(member.user.userId);
    if (response.code === 0) {
      participations.value = response.data;
      participationListDialogVisible.value = true;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取参与记录失败！");
  }
};

// 计算属性，当前页显示的成员列表
const paginatedMembers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return members.value.slice(start, end);
});

// 处理页码改变的函数
const handlePageChange = page => {
  currentPage.value = page;
};

onMounted(async () => {
  if (isAdmin) {
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
      if (allClubs.value.length > 0) {
        selectedClubId.value = allClubs.value[0].clubId;
        fetchMembers();
      }
    } else {
      ElMessage.error(response.message);
    }
  } else if (isLeader) {
    const response = await getClubByLeaderID();
    if (response.code === 0) {
      selectedClubId.value = response.data[0].clubId;
      fetchMembers();
    } else {
      ElMessage.error(response.message);
    }
  } else if (isUser) {
    await fetchUserParticipations();
  }
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
