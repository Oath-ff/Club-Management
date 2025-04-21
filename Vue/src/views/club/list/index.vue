<template>
  <div class="container">
    <!-- 页面标题 -->
    <div class="header">
      <h1>社团列表</h1>
    </div>
    <!-- 搜索社团表单 -->
    <div class="search-box">
      <el-form ref="searchFormRef" :model="searchForm" label-width="100px">
        <el-form-item label="搜索社团">
          <el-input v-model="searchForm.query" placeholder="请输入社团名称或类型">
            <template #append>
              <el-button @click="fetchMembers">搜索</el-button>
            </template>
          </el-input>
        </el-form-item>
      </el-form>
    </div>
    <!-- 社团列表展示 -->
    <div class="content">
      <el-table :data="paginatedClubs" stripe class="full-width-table">
        <el-table-column prop="name" label="社团名称" min-width="180"></el-table-column>
        <el-table-column prop="leader.username" label="社长" min-width="180"></el-table-column>
        <el-table-column prop="type" label="社团类型" min-width="180"></el-table-column>
        <el-table-column prop="description" label="社团描述" min-width="300">
          <template #default="scope">
            <el-tooltip class="item" effect="dark" :content="scope.row.description" placement="top">
              <span class="description">{{ scope.row.description }}</span>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column label="操作" min-width="300">
          <template #default="scope">
            <!-- 查看详细按钮 -->
            <el-button type="primary" size="mini" @click="viewDetails(scope.row.clubId)">查看详细</el-button>
            <!-- 删除社团按钮（仅管理员可见） -->
            <el-button v-if="userRole === '管理员'" type="danger" size="mini" @click="confirmDelete(scope.row.clubId)">
              删除社团
            </el-button>
            <!-- 修改信息按钮（管理员和团长自己所属社团可见） -->
            <el-button
              v-if="
                userRole === '管理员' ||
                (userRole === '团长' && scope.row.leader && scope.row.leader.username === userStore.userName)
              "
              type="warning"
              size="mini"
              @click="confirmEdit(scope.row)"
            >
              修改信息
            </el-button>
            <el-button
              v-if="
                (userRole === '用户' || userRole === '团长') &&
                !(userRole === '团长' && scope.row.leader && scope.row.leader.username === userStore.userName)
              "
              type="success"
              size="mini"
              @click="handleJoin(scope.row.clubId)"
            >
              申请加入
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="paginatedClubs.length === 0" class="no-clubs">当前没有社团记录。</div>
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
    <!-- 确认删除社团弹窗 -->
    <el-dialog title="确认删除社团" v-model="deleteDialogVisible">
      <p>确定要删除这个社团吗？这将会删除与当前社团相关的所有成员以及活动！此操作不可恢复。</p>
      <template #footer>
        <el-button @click="deleteDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="deleteClubConfirm">删除</el-button>
      </template>
    </el-dialog>
    <!-- 修改社团信息弹窗 -->
    <el-dialog title="修改社团信息" v-model="editDialogVisible">
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="120px">
        <el-form-item label="社团名称" prop="name">
          <el-input v-model="editForm.name" placeholder="请输入社团名称"></el-input>
        </el-form-item>
        <el-form-item v-if="userRole === '管理员'" label="社团类型" prop="type">
          <!-- 管理员用下拉框选择社团类型 -->
          <el-select v-model="editForm.type" placeholder="请选择社团类型">
            <el-option v-for="type in clubTypes" :key="type" :label="type" :value="type"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item v-else-if="userRole === '团长'" label="社团类型">
          <!-- 团长只能查看当前社团类型，不可修改 -->
          <el-input v-model="editForm.type" disabled placeholder="不可修改社团类型"></el-input>
        </el-form-item>
        <el-form-item label="社团描述" prop="description">
          <el-input type="textarea" v-model="editForm.description" placeholder="请输入社团描述"></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="editClubConfirm">确认修改</el-button>
      </template>
    </el-dialog>

    <!-- 确认加入社团弹窗 -->
    <el-dialog title="确认加入社团" v-model="joinDialogVisible">
      <p>确定要申请加入此社团吗？</p>
      <template #footer>
        <el-button @click="joinDialogVisible = false">取消</el-button>
        <el-button type="success" @click="joinClubConfirm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { getAllClubs, updateClub, deleteClub, getClubDetails } from "@/api/modules/club";
import { addMember, findMembership } from "@/api/modules/membership";
import { getUserInfoByName } from "@/api/modules/user";
import { useRouter } from "vue-router";
import { useUserStore } from "@/stores/modules/user";

const router = useRouter();
const userStore = useUserStore();
const userRole = userStore.userRole;
const userInfo = reactive({
  nickname: "",
  phoneNumber: ""
});
const allClubs = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const deleteDialogVisible = ref(false);
const editDialogVisible = ref(false);
const joinDialogVisible = ref(false);
const selectedClubId = ref(null);
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
const applyForm = reactive({
  club: {
    clubId: ""
  },
  user: {
    userId: ""
  },
  status: "待审"
});
const clubTypes = ["学术", "文艺", "体育", "公益", "科技创新", "语言交流", "户外活动", "宗教文化", "综合娱乐"];
const editRules = reactive({
  name: [{ required: true, message: "请输入社团名称", trigger: "blur" }],
  type: [{ required: true, message: "请选择社团类型", trigger: "blur" }],
  description: [{ required: true, message: "请输入社团描述", trigger: "blur" }]
});
const searchFormRef = ref();
const searchForm = reactive({
  query: ""
});

// 计算属性，过滤后的社团列表
const filteredClubs = computed(() => {
  if (!searchForm.query) {
    return allClubs.value;
  }
  return allClubs.value.filter(club => club.name.includes(searchForm.query) || club.type.includes(searchForm.query));
});

// 当前页显示的社团列表
const paginatedClubs = computed(() => {
  const sorted = filteredClubs.value.slice().sort((a, b) => {
    if (userRole === "团长" && a.leader && a.leader.username === userStore.userName) return -1;
    if (userRole === "团长" && b.leader && b.leader.username === userStore.userName) return 1;
    return 0;
  });
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return sorted.slice(start, end);
});

// 获取用户信息（昵称和联系电话）
const fetchUserInfo = async () => {
  try {
    const params = { username: userStore.userName };
    const response = await getUserInfoByName(params);
    if (response.code === 0) {
      userInfo.nickname = response.data.nickname;
      userInfo.phoneNumber = response.data.phoneNumber;
    } else {
      ElMessage.error("获取用户信息失败！");
    }
  } catch (error) {
    ElMessage.error("获取用户信息失败！");
  }
};

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

// 查看社团详情的函数
const viewDetails = clubId => {
  router.push({ path: `/club/detail`, query: { clubId } });
};

// 确认删除社团的函数
const confirmDelete = clubId => {
  selectedClubId.value = clubId;
  deleteDialogVisible.value = true;
};

// 删除社团的函数
const deleteClubConfirm = async () => {
  try {
    const response = await deleteClub(selectedClubId.value);
    if (response.code === 0) {
      ElMessage.success("删除社团成功！");
      deleteDialogVisible.value = false;
      getClubs();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("删除社团失败！");
  }
};

// 获取社团详情并打开编辑弹窗
const confirmEdit = async club => {
  try {
    const response = await getClubDetails({ clubId: club.clubId });
    if (response.code === 0) {
      const clubDetails = response.data;
      editForm.clubId = clubDetails.clubId;
      editForm.name = clubDetails.name;
      editForm.type = clubDetails.type;
      editForm.description = clubDetails.description;
      editForm.leader.username = clubDetails.leader?.username || "";
      editDialogVisible.value = true;
    } else {
      ElMessage.error("获取社团详细信息失败！");
    }
  } catch (error) {
    ElMessage.error("获取社团详细信息失败！");
  }
};

// 修改社团信息确认
const editClubConfirm = async () => {
  if (editFormRef.value) {
    editFormRef.value.validate(async valid => {
      if (valid) {
        try {
          const response = await updateClub(editForm.clubId, editForm);
          if (response.code === 0) {
            ElMessage.success("社团信息修改成功！");
            editDialogVisible.value = false;
            await getClubs();
          } else {
            ElMessage.error(response.message);
          }
        } catch (error) {
          ElMessage.error("修改社团信息失败！");
        }
      }
    });
  }
};

// 申请加入的逻辑
const handleJoin = clubId => {
  if (!userInfo.nickname || !userInfo.phoneNumber) {
    ElMessage.error("您当前还没有设置昵称和联系电话！");
    return;
  }
  selectedClubId.value = clubId;
  joinDialogVisible.value = true;
};

// 申请加入函数
const confirmJoin = clubId => {
  selectedClubId.value = clubId;
  joinDialogVisible.value = true;
};

const joinClubConfirm = async () => {
  try {
    const params = {};
    params.username = userStore.userName;
    const userInfoResponse = await getUserInfoByName(params);
    applyForm.user.userId = userInfoResponse.data.userId;
    applyForm.club.clubId = selectedClubId.value;
    const response = await addMember(applyForm);
    if (response.code === 0) {
      ElMessage.success("申请加入成功！");
      joinDialogVisible.value = false;
    } else {
      ElMessage.error("您已申请加入该社团或已是该社团成员，请勿重复申请！");
    }
  } catch (error) {
    ElMessage.error("申请加入失败！");
  }
};

// 处理页码改变的函数
const handlePageChange = page => {
  currentPage.value = page;
};

onMounted(async () => {
  await fetchUserInfo();
  await getClubs();
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
