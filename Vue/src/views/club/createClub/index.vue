<template>
  <div class="card content-box">
    <el-form ref="clubFormRef" :model="clubForm" :rules="rules" label-width="140px">
      <el-form-item label="社团名称" prop="name">
        <el-input v-model="clubForm.name" placeholder="请输入社团名称,不能超过50个字符" />
      </el-form-item>
      <el-form-item label="社团类型" prop="type">
        <el-select v-model="clubForm.type" placeholder="请选择社团类型">
          <el-option v-for="type in clubTypes" :key="type" :label="type" :value="type"></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="社团描述" prop="description">
        <el-input v-model="clubForm.description" type="textarea" placeholder="请输入社团描述,不能超过255个字符" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="submitForm(clubFormRef)">创建</el-button>
        <el-button @click="resetForm(clubFormRef)">重置</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import { ElMessage } from "element-plus";
import { createClub } from "@/api/modules/club";
import { useRouter } from "vue-router";
import { HOME_URL } from "@/config";

// 社团类型选项（去掉“类社团”后缀）
const clubTypes = ["学术", "文艺", "体育", "公益", "科技创新", "语言交流", "户外活动", "宗教文化", "综合娱乐"];

const router = useRouter();
const clubFormRef = ref();
const clubForm = reactive({
  name: "",
  type: "",
  description: "",
  status: "待审核"
});

const rules = reactive({
  name: [
    { required: true, message: "请输入社团名称", trigger: "blur" },
    { max: 10, message: "社团名称不能超过10个字符", trigger: "blur" }
  ],
  type: [{ required: true, message: "请选择社团类型", trigger: "blur" }],
  description: [{ max: 255, message: "社团描述不能超过255个字符", trigger: "blur" }]
});

const submitForm = async formEl => {
  if (!formEl) return;
  await formEl.validate(async valid => {
    if (valid) {
      try {
        await createClub(clubForm);
        ElMessage.success("请等待管理员审核");
        resetForm(formEl);
        // 跳转到首页
        setTimeout(() => {
          router.push({ path: HOME_URL });
        }, 1000);
      } catch (error) {
        ElMessage.error("社团创建失败！");
      }
    } else {
      console.log("表单验证失败！");
    }
  });
};

const resetForm = formEl => {
  if (!formEl) return;
  formEl.resetFields();
};
</script>

<style scoped lang="scss">
@import "./index.scss";
</style>
