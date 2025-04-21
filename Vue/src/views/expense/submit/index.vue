<template>
  <div class="container">
    <div class="header">
      <h1>费用申请</h1>
    </div>
    <el-form :model="expenseForm" ref="expenseFormRef" label-width="100px">
      <el-form-item label="申请金额" prop="amount">
        <el-input v-model="expenseForm.amount" placeholder="请输入申请金额" />
      </el-form-item>
      <el-form-item label="申请时间" prop="date">
        <el-input v-model="formattedDate" disabled />
      </el-form-item>
      <el-form-item label="申请用途" prop="purpose">
        <el-input v-model="expenseForm.purpose" type="textarea" placeholder="请输入申请用途" />
      </el-form-item>
      <el-form-item v-if="isAdmin" label="社团" prop="clubId">
        <el-select v-model="expenseForm.clubId" placeholder="请选择社团">
          <el-option v-for="club in allClubs" :key="club.clubId" :label="club.name" :value="club.clubId"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitApplication">提交</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { submitExpenseApplication } from "@/api/modules/expenseApplication";
import { getAllClubs, getClubByLeaderID } from "@/api/modules/club";
import { useUserStore } from "@/stores/modules/user";

const userStore = useUserStore();
const allClubs = ref([]);
const expenseFormRef = ref(null);
const expenseForm = ref({
  amount: "",
  date: new Date().toISOString(),
  purpose: "",
  club: {
    clubId: ""
  },
  status: "待审核"
});

const formattedDate = ref(new Date().toLocaleString("zh-CN", { hour12: false }));

const isAdmin = userStore.userRole === "管理员";
const isLeader = userStore.userRole === "团长";

const fetchClubInfo = async () => {
  if (isAdmin) {
    const response = await getAllClubs();
    if (response.code === 0) {
      allClubs.value = response.data;
    } else {
      ElMessage.error(response.message);
    }
  } else if (isLeader) {
    const response = await getClubByLeaderID();
    if (response.code === 0) {
      expenseForm.value.club.clubId = response.data[0].clubId;
    } else {
      ElMessage.error(response.message);
    }
  }
};

const submitApplication = async () => {
  try {
    await expenseFormRef.value.validate();
    if (!expenseForm.value.club.clubId) {
      ElMessage.error("请选择社团！");
      return;
    }
    const response = await submitExpenseApplication(expenseForm.value);
    if (response.code === 0) {
      ElMessage.success("费用申请提交成功，等待管理员审核！");
      const now = new Date();
      expenseForm.value = {
        amount: "",
        date: now.toISOString(),
        purpose: "",
        club: {
          clubId: ""
        },
        status: "待审核"
      };
      formattedDate.value = now.toLocaleString("zh-CN", { hour12: false });
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("提交费用申请失败！");
  }
};

onMounted(fetchClubInfo);
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
