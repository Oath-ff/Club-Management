<template>
  <div class="container">
    <div class="header">
      <h1>审批成员列表</h1>
    </div>
    <div class="select-club" v-if="isAdmin">
      <el-form :model="clubForm" label-width="100px" class="select-club-form">
        <el-form-item label="社团：" class="select-club-item">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchPendingMembers">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <el-table :data="paginatedPendingMembers" stripe class="full-width-table">
        <el-table-column prop="user.username" label="用户名" min-width="100"></el-table-column>
        <el-table-column prop="user.nickname" label="姓名" min-width="100"></el-table-column>
        <el-table-column prop="user.phoneNumber" label="联系电话" min-width="150"></el-table-column>
        <el-table-column prop="club.name" label="社团名称" min-width="100"></el-table-column>
        <el-table-column prop="joinDate" label="申请日期" min-width="150"></el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="scope">
            <el-button type="success" size="mini" @click="approve(scope.row.user.userId, selectedClubId)">同意</el-button>
            <el-button type="danger" size="mini" @click="reject(scope.row.user.userId, selectedClubId)">拒绝</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="pendingMembers.length"
        >
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { pendingList, approveMember, rejectMember } from "@/api/modules/membership";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const currentClub = ref({});
const selectedClubId = ref(null);
const pendingMembers = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

// 获取待审批成员
const fetchPendingMembers = async () => {
  try {
    const clubId = isAdmin ? selectedClubId.value : currentClub.value[0].clubId;
    const response = await pendingList(clubId);
    if (response.code === 0) {
      pendingMembers.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取待审成员列表失败！");
  }
};

// 同意操作
const approve = async (userId, clubId) => {
  try {
    const response = await approveMember(userId, clubId);
    if (response.code === 0) {
      ElMessage.success("同意加入成功！");
      fetchPendingMembers();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("同意加入失败！");
  }
};

// 拒绝操作
const reject = async (userId, clubId) => {
  try {
    const response = await rejectMember(userId, clubId);
    if (response.code === 0) {
      ElMessage.success("拒绝加入成功！");
      fetchPendingMembers();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("拒绝加入失败！");
  }
};

// 当前页待审批成员列表
const paginatedPendingMembers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return pendingMembers.value.slice(start, end);
});

// 处理页码改变
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
          fetchPendingMembers();
        }
      } else {
        ElMessage.error(response.message);
      }
    } else if (isLeader) {
      const response = await getClubByLeaderID();
      if (response.code === 0) {
        currentClub.value = response.data;
        selectedClubId.value = currentClub.value[0].clubId;
        fetchPendingMembers();
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
