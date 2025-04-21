<template>
  <div class="container">
    <div v-if="isUser" class="content">
      <div class="header"><h1>申请加入社团</h1></div>
      <div class="search-box">
        <el-form ref="searchFormRef" :model="searchForm" label-width="100px">
          <el-form-item label="搜索社团">
            <el-input v-model="searchForm.query" placeholder="请输入社团名称或类型" />
          </el-form-item>
        </el-form>
      </div>
      <el-table :data="paginatedClubs" stripe class="full-width-table">
        <el-table-column prop="name" label="社团名称" width="180"></el-table-column>
        <el-table-column prop="type" label="社团类型" width="180"></el-table-column>
        <el-table-column prop="leader.username" label="团长名称" width="180"></el-table-column>
        <el-table-column prop="description" label="社团描述"></el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" size="mini" @click="applyToJoin(scope.row.clubId)">申请加入</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="filteredClubs.length"
        >
        </el-pagination>
      </div>
    </div>
    <div v-else-if="isLeader" class="content">
      <div class="header"><h1>添加成员</h1></div>
      <el-form ref="applyFormRef" :model="applyForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="applyForm.username" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitApplication">提交申请</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div v-else-if="isAdmin" class="content">
      <div class="header"><h1>添加成员</h1></div>
      <el-form ref="applyFormRef" :model="applyForm" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="applyForm.username" />
        </el-form-item>
        <el-form-item label="社团">
          <el-select v-model="applyForm.club.clubId" placeholder="请选择社团">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitApplication">提交申请</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { addMember } from "@/api/modules/membership";
import { getUserInfoByName } from "@/api/modules/user";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const searchFormRef = ref();
const applyFormRef = ref();
const searchForm = reactive({
  query: ""
});
const applyList = reactive({
  club: {
    clubId: ""
  },
  user: {
    userId: ""
  },
  status: "待审"
});
const applyForm = reactive({
  username: "",
  club: {
    clubId: ""
  },
  status: "待审"
});
const allClubs = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const isUser = userStore.userRole === "用户";
const isLeader = userStore.userRole === "团长";
const isAdmin = userStore.userRole === "管理员";

// 获取所有社团信息
const getClubs = async () => {
  try {
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("查询社团列表失败！");
  }
};

const applyToJoin = async clubId => {
  try {
    const userInfoResponse = await getUserInfoByName({ username: userStore.userName });
    if (userInfoResponse.code === 0) {
      applyList.user.userId = userInfoResponse.data.userId;
      applyList.club.clubId = clubId;
      const response = await addMember(applyList);
      if (response.code === 0) {
        ElMessage.success("申请提交成功！");
      } else {
        ElMessage.error(response.message);
      }
    } else {
      ElMessage.error("获取用户信息失败！");
    }
  } catch (error) {
    ElMessage.error("提交申请失败！");
  }
};

const submitApplication = async () => {
  try {
    const params = {};
    params.username = applyForm.username;
    const userInfoResponse = await getUserInfoByName(params);
    if (userInfoResponse.code === 0) {
      if (isAdmin) {
        applyList.user.userId = userInfoResponse.data.userId;
        applyList.club.clubId = applyForm.club.clubId;
        const response = await addMember(applyList);
        if (response.code === 0) {
          ElMessage.success("申请提交成功！");
        } else {
          ElMessage.error(response.message);
        }
      } else if (isLeader) {
        applyList.user.userId = userInfoResponse.data.userId;
        const clubresponse = await getClubByLeaderID();
        applyList.club.clubId = clubresponse.data.clubId;
        const response = await addMember(applyList);
        if (response.code === 0) {
          ElMessage.success("申请提交成功！");
        } else {
          ElMessage.error(response.message);
        }
      }
    } else {
      ElMessage.error("获取用户信息失败！");
    }
  } catch (error) {
    ElMessage.error("提交申请失败！");
  }
};

onMounted(async () => {
  await getClubs();
});

// 计算属性，过滤后的社团列表
const filteredClubs = computed(() => {
  if (!searchForm.query) {
    return allClubs.value;
  }
  return allClubs.value.filter(club => club.name.includes(searchForm.query) || club.type.includes(searchForm.query));
});

// 计算属性，当前页显示的社团列表
const paginatedClubs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredClubs.value.slice(start, end);
});

// 处理页码改变的函数
const handlePageChange = page => {
  currentPage.value = page;
};
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
