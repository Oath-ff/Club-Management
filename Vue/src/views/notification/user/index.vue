<template>
  <div class="container">
    <div class="header">
      <h1>通知列表</h1>
    </div>
    <div class="content">
      <el-table :data="messages" stripe class="full-width-table">
        <el-table-column prop="title" label="通知标题" min-width="100"></el-table-column>
        <el-table-column prop="content" label="通知内容" min-width="200"></el-table-column>
        <el-table-column prop="date" label="通知时间" min-width="150"></el-table-column>
        <el-table-column prop="club.name" label="所属社团" min-width="100"></el-table-column>
        <el-table-column prop="sender.userName" label="发送者" min-width="100"></el-table-column>
      </el-table>
      <div v-if="messages.length === 0" class="no-messages">当前没有通知。</div>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          @current-change="handlePageChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="totalMessages"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { userMsgList } from "@/api/modules/notification";
import { useUserStore } from "@/stores/modules/user";
import { getUserInfoByName } from "@/api/modules/user";

const userStore = useUserStore();
const messages = ref([]);
const currentPage = ref(1);
const pageSize = ref(10);
const totalMessages = ref(0);

const fetchMessages = async (page = 1) => {
  try {
    const userResponse = await getUserInfoByName({ username: userStore.userName });
    if (userResponse.code === 0) {
      const userId = userResponse.data.userId;
      const response = await userMsgList(userId, page, pageSize.value);
      if (response.code === 0) {
        messages.value = response.data;
        totalMessages.value = response.total;
      } else {
        ElMessage.error(response.message);
      }
    } else {
      ElMessage.error(userResponse.message);
    }
  } catch (error) {
    ElMessage.error("获取通知失败！");
  }
};

const handlePageChange = async page => {
  currentPage.value = page;
  await fetchMessages(page);
};

onMounted(() => {
  fetchMessages();
});
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
