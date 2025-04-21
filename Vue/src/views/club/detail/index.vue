<template>
  <div class="container">
    <div class="header">
      <h1>查询社团详情信息</h1>
    </div>
    <div class="search-box">
      <el-form ref="searchFormRef" :model="searchForm" label-width="100px">
        <el-form-item label="搜索社团">
          <el-input v-model="searchForm.query" placeholder="请输入社团ID或名称" />
          <el-button type="primary" @click="searchClub">搜索</el-button>
        </el-form-item>
        <el-form-item v-if="isAdmin" label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchClubDetails">
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-if="isUser" label="社团">
          <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchClubDetails">
            <el-option v-for="club in userClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <div class="content">
      <!-- 社团详情展示 -->
      <div v-if="clubDetails" class="details-box">
        <el-descriptions title="社团详细信息" :border="true" column="1" label-width="150px">
          <el-descriptions-item label="社团ID">{{ clubDetails.clubId }}</el-descriptions-item>
          <el-descriptions-item label="社团名称">{{ clubDetails.name }}</el-descriptions-item>
          <el-descriptions-item label="社长">{{ clubDetails.leader.username }}</el-descriptions-item>
          <el-descriptions-item label="社团类型">{{ clubDetails.type }}</el-descriptions-item>
          <el-descriptions-item label="社团描述" class="break-word">{{ clubDetails.description }}</el-descriptions-item>
        </el-descriptions>
        <!-- 团长权限，展示修改按钮 -->
        <el-button
          v-if="isLeader && clubDetails.leader.username === userStore.userName"
          type="primary"
          @click="editClubDialogVisible = true"
        >
          修改社团信息
        </el-button>
      </div>
      <div v-else class="no-club">您还未加入任何社团。</div>
    </div>

    <!-- 修改社团信息弹窗 -->
    <el-dialog title="修改社团信息" v-model="editClubDialogVisible">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="120px">
        <el-form-item label="社团名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入社团名称"></el-input>
        </el-form-item>
        <el-form-item label="社团类型" prop="type">
          <el-input v-model="editForm.type" placeholder="请输入社团类型"></el-input>
        </el-form-item>
        <el-form-item label="社团描述" prop="description">
          <el-input type="textarea" v-model="editForm.description" placeholder="请输入社团描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editClubDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmEditClub">确认修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { ElMessage } from "element-plus";
import { getClubDetails, getClubByLeaderID, getAllClubs, updateClub } from "@/api/modules/club";
import { findMembership } from "@/api/modules/membership";
import { useUserStore } from "@/stores/modules/user";

// 获取用户状态管理
const userStore = useUserStore();
const searchFormRef = ref();
const route = useRoute();
const listParams = route.query.clubId;
const searchForm = reactive({
  query: ""
});
const clubDetails = ref(null);
const allClubs = ref([]);
const userClubs = ref([]);
const selectedClubId = ref(null);
const editClubDialogVisible = ref(false);
const editFormRef = ref(null);
const editForm = reactive({
  clubId: null,
  name: "",
  type: "",
  description: "",
  leader: {
    username: ""
  },
  status: "已审核"
});
const editRules = reactive({
  name: [{ required: true, message: "请输入社团名称", trigger: "blur" }],
  type: [{ required: true, message: "请输入社团类型", trigger: "blur" }],
  description: [{ required: true, message: "请输入社团描述", trigger: "blur" }]
});

const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";
const isUser = userStore.userRole === "用户";

// 搜索社团的函数
const searchClub = async () => {
  try {
    const params = {};
    if (searchForm.query && !isNaN(searchForm.query)) {
      params.clubId = parseInt(searchForm.query, 10);
    } else if (searchForm.query) {
      params.clubName = searchForm.query;
    } else if (listParams) {
      params.clubId = listParams;
    }
    const response = await getClubDetails(params);
    if (response.code === 0) {
      clubDetails.value = Array.isArray(response.data) ? response.data[0] : response.data;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("查询社团信息失败！");
  }
};

// 获取社团详情的函数
const fetchClubDetails = async () => {
  if (!selectedClubId.value) return;
  try {
    const response = await getClubDetails({ clubId: selectedClubId.value });
    if (response.code === 0) {
      clubDetails.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("查询社团信息失败！");
  }
};

// 确认修改社团信息的函数
const confirmEditClub = async () => {
  if (!editFormRef.value) return;
  editFormRef.value.validate(async valid => {
    if (valid) {
      try {
        console.log(editForm);
        const response = await updateClub(editForm.clubId, editForm);
        if (response.code === 0) {
          ElMessage.success("社团信息修改成功！");
          editClubDialogVisible.value = false;
          fetchClubDetails();
        } else {
          ElMessage.error(response.message);
        }
      } catch (error) {
        ElMessage.error("修改社团信息失败！");
      }
    }
  });
};

// 页面加载时获取当前用户的社团信息
onMounted(async () => {
  if (listParams) {
    // 如果是从社团列表页面跳转过来，展示指定社团的详情信息
    searchForm.query = listParams;
    await searchClub();
  } else if (isAdmin) {
    // 管理员获取所有社团
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } else if (isLeader) {
    // 团长获取自己创建的社团详情
    const response = await getClubByLeaderID();
    if (response.code === 0) {
      clubDetails.value = Array.isArray(response.data) ? response.data[0] : response.data;
      if (clubDetails.value) {
        editForm.clubId = clubDetails.value.clubId;
        editForm.name = clubDetails.value.name;
        editForm.type = clubDetails.value.type;
        editForm.description = clubDetails.value.description;
        editForm.leader.username = clubDetails.value.leader.username;
      }
    } else {
      ElMessage.info("您当前没有创建社团或不是任何社团的团长");
    }
  } else if (isUser) {
    // 普通用户获取自己加入的社团
    const params = { username: userStore.userName };
    const response = await findMembership(params);
    if (response.code === 0) {
      userClubs.value = response.data.map(item => item.club);
      if (userClubs.value.length > 0) {
        selectedClubId.value = userClubs.value[0].clubId;
        await fetchClubDetails();
      } else {
        ElMessage.info("您当前没有加入任何社团");
      }
    }
  }
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
