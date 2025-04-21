<template>
  <div class="container">
    <div class="header">
      <h1>成员列表</h1>
    </div>
    <div class="select-club">
      <el-form :model="clubForm" label-width="100px">
        <el-form-item v-if="isAdmin" label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchMembers">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <el-table :data="paginatedMembers" stripe class="full-width-table">
        <el-table-column prop="user.username" label="用户名" min-width="100"></el-table-column>
        <el-table-column prop="user.nickname" label="姓名" min-width="100"></el-table-column>
        <el-table-column prop="user.phoneNumber" label="联系电话" min-width="150"></el-table-column>
        <el-table-column prop="club.name" label="所属社团" min-width="100"></el-table-column>
        <el-table-column prop="joinDate" label="加入时间" min-width="150"></el-table-column>
        <el-table-column v-if="isAdmin || isLeader" label="操作" min-width="120">
          <template #default="scope">
            <el-button type="danger" size="mini" @click="remove(scope.row.membershipId)">删除成员</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="members.length === 0" class="no-members">当前没有成员。</div>
      <div class="pagination">
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { listMembers, removeMember } from "@/api/modules/membership";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const userClubs = ref([]);
const selectedClubId = ref(null);
const members = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

const fetchMembers = async () => {
  try {
    const clubId = isLeader ? userClubs.value[0]?.clubId : selectedClubId.value;
    if (!clubId) {
      ElMessage.warning("未选择社团！");
      return;
    }
    const response = await listMembers(clubId);
    if (response.code === 0) {
      members.value = response.data;
    } else {
      members.value = [];
      ElMessage.error(response.message);
    }
  } catch (error) {
    members.value = [];
    ElMessage.error("获取成员列表失败！");
  }
};

const remove = async membershipId => {
  try {
    const response = await removeMember(membershipId);
    if (response.code === 0) {
      ElMessage.success("删除成员成功！");
      await fetchMembers();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("删除成员失败！");
  }
};

// 计算属性，分页显示成员列表
const paginatedMembers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return members.value.slice(start, end);
});

// 处理页码变化
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
          await fetchMembers();
        }
      } else {
        ElMessage.error(response.message);
      }
    } else if (isLeader) {
      const response = await getClubByLeaderID();
      if (response.code === 0) {
        userClubs.value = response.data;
        if (userClubs.value.length > 0) {
          selectedClubId.value = userClubs.value[0].clubId;
          await fetchMembers();
        }
      } else {
        ElMessage.error("获取社团信息失败！");
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
