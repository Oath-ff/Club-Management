<template>
  <div class="container">
    <div class="header">
      <h1 v-if="userRole === '管理员'">审批社团列表</h1>
      <h1 v-else>我的社团申请记录</h1>
    </div>
    <div class="content">
      <el-table :data="paginatedClubs" stripe class="full-width-table">
        <el-table-column prop="name" label="社团名称" min-width="150"></el-table-column>
        <el-table-column prop="leader.username" label="社长" min-width="100"></el-table-column>
        <el-table-column prop="type" label="社团类型" min-width="150"></el-table-column>
        <el-table-column prop="description" label="社团描述" min-width="200"></el-table-column>
        <el-table-column prop="status" label="社团状态" min-width="100"></el-table-column>
        <el-table-column v-if="userRole === '管理员'" label="操作" min-width="150">
          <template #default="scope">
            <el-button type="success" size="mini" @click="confirmApproval(scope.row.clubId)">通过审核</el-button>
            <el-button type="danger" size="mini" @click="confirmRejection(scope.row.clubId)">不通过审核</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="clubs.length === 0" class="no-clubs">当前没有社团记录。</div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="clubs.length"
        >
        </el-pagination>
      </div>
    </div>
    <!-- 确认通过审核弹窗 -->
    <el-dialog title="确认通过审核" v-model="approvalDialogVisible">
      <p>确定要通过审核这个社团吗？</p>
      <template #footer>
        <el-button @click="approvalDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="approve">确定</el-button>
      </template>
    </el-dialog>
    <!-- 确认不通过审核弹窗 -->
    <el-dialog title="确认不通过审核" v-model="rejectionDialogVisible">
      <p>确定要不通过审核这个社团吗？</p>
      <template #footer>
        <el-button @click="rejectionDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="reject">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { getpendingClubs, approveClub, getCreateClubRecord, rejectClub } from "@/api/modules/club";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const userRole = userStore.userRole;
const clubs = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const approvalDialogVisible = ref(false);
const rejectionDialogVisible = ref(false);
const selectedClubId = ref(null);

const fetchPendingClubs = async () => {
  try {
    const response = await getpendingClubs();
    if (response.code === 0) {
      clubs.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取待审核社团列表失败！");
  }
};

const fetchCreateClubRecord = async () => {
  try {
    const response = await getCreateClubRecord();
    if (response.code === 0) {
      clubs.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("获取社团申请记录失败！");
  }
};

const confirmApproval = clubId => {
  selectedClubId.value = clubId;
  approvalDialogVisible.value = true;
};

const confirmRejection = clubId => {
  selectedClubId.value = clubId;
  rejectionDialogVisible.value = true;
};

const approve = async () => {
  try {
    const response = await approveClub(selectedClubId.value);
    if (response.code === 0) {
      ElMessage.success("审核通过成功！");
      approvalDialogVisible.value = false;
      fetchPendingClubs();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("审核通过失败！");
  }
};

const reject = async () => {
  try {
    const response = await rejectClub(selectedClubId.value);
    if (response.code === 0) {
      ElMessage.success("不通过审核成功！");
      rejectionDialogVisible.value = false;
      fetchPendingClubs();
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("不通过审核失败！");
  }
};

// 计算属性，当前页显示的社团列表
const paginatedClubs = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return clubs.value.slice(start, end);
});

// 处理页码改变的函数
const handlePageChange = page => {
  currentPage.value = page;
};

onMounted(() => {
  if (userRole === "管理员") {
    fetchPendingClubs();
  } else if (userRole === "团长" || userRole === "用户") {
    fetchCreateClubRecord();
  }
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
