<template>
  <div class="container">
    <div class="header">
      <h1>费用申请列表</h1>
    </div>
    <div class="select-club" v-if="isAdmin">
      <el-select v-model="selectedClubId" placeholder="请选择社团" @change="fetchExpenseApplications">
        <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
      </el-select>
    </div>
    <div class="content">
      <el-table :data="paginatedApplications" stripe class="full-width-table">
        <el-table-column prop="club.name" label="所属社团" min-width="100"></el-table-column>
        <el-table-column prop="amount" label="申请金额" min-width="100"></el-table-column>
        <el-table-column prop="date" label="申请时间" min-width="150"></el-table-column>
        <el-table-column prop="purpose" label="申请用途" min-width="200"></el-table-column>
        <el-table-column prop="status" label="申请状态" min-width="100"></el-table-column>
        <el-table-column label="操作" min-width="120">
          <template #default="scope">
            <el-button type="primary" size="mini" @click="openEditDialog(scope.row)" :disabled="scope.row.status === '已审核'">
              修改信息
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="applications.length === 0" class="no-applications">当前没有申请。</div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="applications.length"
        >
        </el-pagination>
      </div>
    </div>
    <!-- 修改申请弹窗 -->
    <el-dialog v-model="dialogVisible" title="修改申请">
      <el-form ref="editFormRef" :model="editForm" label-width="100px">
        <el-form-item label="申请金额">
          <el-input v-model="editForm.amount" />
        </el-form-item>
        <el-form-item label="申请时间">
          <el-date-picker v-model="editForm.date" type="datetime" :disabled-date="disabledDate"></el-date-picker>
        </el-form-item>
        <el-form-item label="申请用途">
          <el-input v-model="editForm.purpose" type="textarea" />
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
import {
  listExpenseApplicationsByClubId,
  findPendingListByClubId,
  updateExpenseApplication
} from "@/api/modules/expenseApplication";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const selectedClubId = ref(null);
const applications = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";
const dialogVisible = ref(false);
const editForm = ref({
  amount: "",
  date: "",
  purpose: "",
  status: ""
});
const editFormRef = ref(null);

const fetchExpenseApplications = async () => {
  try {
    const response = isAdmin
      ? await findPendingListByClubId(selectedClubId.value)
      : await listExpenseApplicationsByClubId(selectedClubId.value);
    if (response.code === 0) {
      applications.value = response.data;
    } else {
      applications.value = [];
      ElMessage.error(response.message);
    }
  } catch (error) {
    applications.value = [];
    ElMessage.error("获取费用申请列表失败！");
  }
};

const openEditDialog = application => {
  editForm.value = { ...application, date: new Date(application.date) };
  dialogVisible.value = true;
};

const submitEdit = async () => {
  try {
    await editFormRef.value.validate();
    const response = await updateExpenseApplication(editForm.value.expenseApplicationId, {
      amount: editForm.value.amount,
      date: editForm.value.date,
      purpose: editForm.value.purpose,
      status: editForm.value.status
    });
    if (response.code === 0) {
      ElMessage.success("申请更新成功！");
      dialogVisible.value = false;
      fetchExpenseApplications();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("提交修改失败！");
  }
};

const disabledDate = time => {
  const currentDate = new Date();
  return (
    time.getTime() < currentDate.setHours(0, 0, 0, 0) || (time.getTime() < Date.now() && time.getDate() === currentDate.getDate())
  );
};

// 计算属性，当前页显示的申请列表
const paginatedApplications = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return applications.value.slice(start, end);
});

// 处理页码改变的函数
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
          fetchExpenseApplications();
        }
      } else {
        ElMessage.error(response.message);
      }
    } else if (isLeader) {
      const response = await getClubByLeaderID();
      if (response.code === 0) {
        selectedClubId.value = response.data.clubId;
        fetchExpenseApplications();
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
