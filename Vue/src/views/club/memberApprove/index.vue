<template>
  <div class="container">
    <div class="header">
      <h1>加入社团记录</h1>
    </div>
    <div class="content">
      <el-table :data="paginatedRecords" stripe>
        <el-table-column prop="clubName" label="社团名称" min-width="180"></el-table-column>
        <el-table-column prop="leaderName" label="社团团长" min-width="180"></el-table-column>
        <el-table-column prop="applicationTime" label="申请时间" min-width="180"></el-table-column>
        <el-table-column prop="status" label="申请状态" min-width="180"></el-table-column>
      </el-table>
      <div v-if="paginatedRecords.length === 0" class="no-records">当前没有申请记录。</div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="records.length"
        >
        </el-pagination>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { ElMessage } from "element-plus";
import { getApplicationRecord } from "@/api/modules/membership";

const records = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);

// 获取申请记录的函数
const fetchRecords = async () => {
  try {
    const response = await getApplicationRecord();
    console.log(response);
    if (response.code === 0) {
      records.value = response.data.map(record => ({
        clubName: record.club.name,
        leaderName: record.club.leader.username,
        applicationTime: record.joinDate,
        status: record.status
      }));
      console.log(records.value);
    } else {
      ElMessage.error(response.message);
    }
  } catch (error) {
    ElMessage.error("查询申请记录失败！");
  }
};

// 计算属性，当前页显示的记录
const paginatedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return records.value.slice(start, end);
});

// 处理页码改变的函数
const handlePageChange = page => {
  currentPage.value = page;
};

// 页面加载时获取申请记录
onMounted(fetchRecords);
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
