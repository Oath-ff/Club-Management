<template>
  <div class="container">
    <div class="header">
      <h1>费用审批</h1>
    </div>
    <!-- 管理员角色 -->
    <div class="select-club" v-if="isAdmin">
      <el-form label-width="100px">
        <el-form-item label="选择社团">
          <el-select
            v-model="selectedClubId"
            placeholder="请选择社团"
            @change="fetchExpenseApplications"
            class="select-club-dropdown"
          >
            <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!-- 表格内容 -->
    <div class="content">
      <el-table :data="paginatedApplications" stripe class="full-width-table">
        <el-table-column prop="club.name" label="所属社团" min-width="150"></el-table-column>
        <el-table-column prop="club.leader.username" label="社团团长" min-width="150"></el-table-column>
        <el-table-column prop="amount" label="申请金额" min-width="120"></el-table-column>
        <el-table-column prop="date" label="申请时间" min-width="150"></el-table-column>
        <el-table-column prop="purpose" label="申请用途" min-width="200"></el-table-column>
        <el-table-column v-if="isLeader" prop="status" label="申请状态" min-width="200"></el-table-column>
        <el-table-column label="操作" min-width="200">
          <template #default="scope">
            <!-- 通过审批按钮 -->
            <el-button v-if="isAdmin" type="success" size="mini" @click="confirmApproval(scope.row)">通过审批</el-button>
            <!-- 拒绝审批按钮 -->
            <el-button v-if="isAdmin" type="danger" size="mini" @click="confirmRejection(scope.row)">审批不通过</el-button>
            <!-- 修改申请信息按钮 -->
            <el-button
              v-if="isLeader"
              :disabled="scope.row.status !== '待审核'"
              :class="scope.row.status !== '待审核' ? 'disabled-button' : ''"
              type="warning"
              size="mini"
              @click="openEditDialog(scope.row)"
            >
              修改申请信息
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="applications.length === 0" class="no-applications">当前没有相关申请记录。</div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="applications.length"
        ></el-pagination>
      </div>
    </div>
    <!-- 审核确认弹窗 -->
    <el-dialog v-model="approvalDialogVisible" title="确认审批">
      <span>确定要通过此费用申请吗？</span>
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="approveApplication">确认</el-button>
      </template>
    </el-dialog>
    <!-- 拒绝确认弹窗 -->
    <el-dialog v-model="rejectionDialogVisible" title="确认拒绝">
      <span>确定要拒绝此费用申请吗？</span>
      <template #footer>
        <el-button @click="rejectionDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="rejectApplication">确认</el-button>
      </template>
    </el-dialog>
    <!-- 修改费用申请信息的弹窗（团长） -->
    <el-dialog v-model="editDialogVisible" title="修改申请信息">
      <el-form :model="editForm" label-width="100px">
        <el-form-item label="申请金额">
          <el-input v-model="editForm.amount" />
        </el-form-item>
        <el-form-item label="申请用途">
          <el-input v-model="editForm.purpose" />
        </el-form-item>
      </el-form>
      <!-- 保存和取消按钮 -->
      <template #footer>
        <el-button @click="editDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdit">保存修改</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { ElMessage } from "element-plus";
import {
  findPendingListByClubId,
  approveExpenseApplication,
  rejectExpenseApplication,
  updateExpenseApplication,
  listExpenseApplicationsByClubId
} from "@/api/modules/expenseApplication";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { getUserInfo } from "@/api/modules/user";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

const allClubs = ref([]);
const applications = ref([]);
const selectedClubId = ref(null);
const currentPage = ref(1);
const pageSize = ref(10);
const approvalDialogVisible = ref(false);
const rejectionDialogVisible = ref(false);
const editDialogVisible = ref(false);
const applicationToApprove = ref(null);
const editForm = ref({
  applicationId: null,
  amount: "",
  date: "",
  purpose: "",
  status: ""
});
const clubId = ref(null);

// 分页后的申请列表
const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return applications.value.slice(start, end);
});

// 获取管理员的费用申请列表
const fetchExpenseApplications = async () => {
  try {
    const response = await findPendingListByClubId(selectedClubId.value);
    if (response.code === 0) {
      applications.value = response.data;
    } else {
      applications.value = [];
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取费用申请列表失败！");
  }
};

// 获取团长的费用申请列表
const fetchLeaderApplications = async () => {
  try {
    const response = await listExpenseApplicationsByClubId(clubId.value);
    if (response.code === 0) {
      applications.value = response.data.filter(application => application.club.leader.username === userStore.userName);
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取费用申请列表失败！");
  }
};

// 确认通过审批
const confirmApproval = application => {
  applicationToApprove.value = application;
  approvalDialogVisible.value = true;
};

// 执行通过审批
const approveApplication = async () => {
  try {
    const response = await approveExpenseApplication(applicationToApprove.value.expenseApplicationId, "审核通过");
    if (response.code === 0) {
      ElMessage.success("费用申请已通过审批！");
      approvalDialogVisible.value = false;
      fetchExpenseApplications();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("审批失败！");
  }
};

// 确认拒绝审批
const confirmRejection = application => {
  applicationToApprove.value = application;
  rejectionDialogVisible.value = true;
};

// 执行拒绝审批
const rejectApplication = async () => {
  try {
    const response = await rejectExpenseApplication(applicationToApprove.value.expenseApplicationId, "审核未通过");
    if (response.code === 0) {
      ElMessage.success("费用申请已拒绝！");
      rejectionDialogVisible.value = false;
      fetchExpenseApplications();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("拒绝审批失败！");
  }
};

// 打开修改申请信息弹窗
const openEditDialog = application => {
  editForm.value = {
    applicationId: application.expenseApplicationId,
    amount: application.amount,
    date: application.date,
    purpose: application.purpose,
    status: application.status
  };
  editDialogVisible.value = true;
};

// 提交修改申请信息
const submitEdit = async () => {
  try {
    const response = await updateExpenseApplication(editForm.value.applicationId, {
      amount: editForm.value.amount,
      purpose: editForm.value.purpose,
      date: editForm.value.date
    });
    if (response.code === 0) {
      ElMessage.success("申请信息已更新！");
      editDialogVisible.value = false;
      fetchLeaderApplications();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("更新申请信息失败！");
  }
};

// 分页函数
const handlePageChange = page => {
  currentPage.value = page;
};

// 初始化函数
onMounted(async () => {
  try {
    if (isAdmin) {
      const response = await getAllClubs();
      if (response.code === 0) {
        allClubs.value = response.data;
        if (allClubs.value.length > 0) {
          selectedClubId.value = allClubs.value[0].clubId;
          fetchExpenseApplications();
        }
      } else {
        ElMessage.error(response.message);
      }
    } else if (isLeader) {
      const userResponse = await getUserInfo(userStore.userName);
      const userId = userResponse.data.userId;
      const clubResponse = await getClubByLeaderID(userId);
      if (clubResponse.code === 0) {
        clubId.value = clubResponse.data[0].clubId;
        fetchLeaderApplications();
      } else {
        ElMessage.error("未能获取团长所属的社团信息！");
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
