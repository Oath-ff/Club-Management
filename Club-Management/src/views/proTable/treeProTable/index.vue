<template>
  <div class="main-box">
    <TreeFilter
      label="name"
      title="部门列表(单选)"
      :multiple="false"
      :data="treeFilterData"
      :default-value="initParam.departmentId"
      @change="changeTreeFilter"
    />
    <div class="table-box">
      <ProTable
        ref="proTable"
        row-key="id"
        :indent="20"
        :columns="columns"
        :request-api="getUserTreeList"
        :init-param="initParam"
        :search-col="{ xs: 1, sm: 1, md: 2, lg: 3, xl: 3 }"
        :enable-cross-parents="true"
        :high-light="true"
        @action="openDrawer"
        @delete-action="deleteAccount"
        label-name="username"
        :graph="graph"
      >
        <!-- 表格 header 按钮 -->
        <template #tableHeader>
          <el-button type="primary" :icon="CirclePlus" @click="openDrawer('新增', null)">新增用户</el-button>
        </template>
        <!-- 表格操作 -->
        <template #operation="scope">
          <el-button type="primary" link :icon="View" @click="openDrawer('查看', scope.row)">查看</el-button>
          <el-button type="primary" link :icon="EditPen" @click="openDrawer('编辑', scope.row)">编辑</el-button>
          <el-button type="primary" link :icon="Delete" @click="deleteAccount(scope.row)">删除</el-button>
        </template>
      </ProTable>
      <UserDrawer ref="drawerRef" />
      <ImportExcel ref="dialogRef" />
    </div>
  </div>
</template>

<script setup lang="jsx" name="treeProTable">
import { onMounted, reactive, ref } from "vue";
import { genderType } from "@/utils/dict";
import { useHandleData } from "@/hooks/useHandleData";
import { ElMessage, ElNotification } from "element-plus";
import ProTable from "@/components/ProTable/index.vue";
import TreeFilter from "@/components/TreeFilter/index.vue";
import ImportExcel from "@/components/ImportExcel/index.vue";
import UserDrawer from "@/views/proTable/components/UserDrawer.vue";
import { CirclePlus, Delete, EditPen, View } from "@element-plus/icons-vue";
import { getUserTreeList, deleteUser, editUser, addUser, getUserStatus, getUserDepartment } from "@/api/modules/user";

// ProTable 实例
const proTable = ref();

// 图谱中被选择的节点
const graphSelectedNode = ref(null);

// 如果表格需要初始化请求参数，直接定义传给 ProTable(之后每次请求都会自动带上该参数，此参数更改之后也会一直带上，改变此参数会自动刷新表格数据)
const initParam = reactive({ departmentId: "" });

// 获取 treeFilter 数据
// 当 proTable 的 requestAuto 属性为 false，不会自动请求表格数据，等待 treeFilter 数据回来之后，更改 initParam.departmentId 的值，才会触发请求 proTable 数据
const treeFilterData = ref([]);

const graph = {
  layouts: [
    {
      force_node_repulsion: 0.3, // 取值范围 0 - 3
      force_line_elastic: 0.8 // 取值范围 0 - 3
    }
  ]
};

const getTreeFilter = async () => {
  // debugger;
  const { data } = await getUserDepartment();
  treeFilterData.value = data || [];
  initParam.departmentId = data[0]?.id || "";
};

// 树形筛选切换
const changeTreeFilter = val => {
  console.log("树形筛选切换", val);
  ElMessage.success("请注意查看请求参数变化 🤔");
  proTable.value.pageable.pageNum = 1;
  initParam.departmentId = val;
};

// 模拟远程加载性别搜索框数据
const loading = ref(false);
const filterGenderEnum = ref([]);
const remoteMethod = query => {
  filterGenderEnum.value = [];
  if (!query) return;
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    filterGenderEnum.value = genderType.filter(item => item.label.includes(query));
  }, 500);
};

// 表格配置项
const columns = reactive([
  { type: "index", label: "#", width: 80 },
  {
    prop: "username",
    label: "用户姓名",
    search: {
      el: "input",
      props: { placeholder: "请输入用户姓名查询" },
      key: "username"
    }
  },
  {
    prop: "gender",
    label: "性别",
    sortable: true,
    isFilterEnum: false,
    enum: filterGenderEnum.value,
    search: {
      el: "select",
      props: { placeholder: "请输入性别查询", filterable: true, remote: true, reserveKeyword: true, loading, remoteMethod }
    },
    render: scope => <>{scope.row.gender === 1 ? "男" : "女"}</>
  },
  { prop: "idCard", label: "身份证号" },
  { prop: "email", label: "邮箱" },
  { prop: "address", label: "居住地址" },
  {
    prop: "status",
    label: "用户状态",
    sortable: true,
    tag: true,
    enum: getUserStatus,
    search: { el: "tree-select" },
    render: scope => (
      <el-tag type={scope.row.status === 0 ? "success" : "danger"}>{scope.row.status === 0 ? "正常" : "禁用"}</el-tag>
    )
  },
  { prop: "createTime", label: "创建时间", width: 180 },
  { prop: "operation", label: "操作", width: 300, fixed: "right" }
]);

// 删除用户信息
const deleteAccount = async params => {
  await useHandleData(deleteUser, { id: [params.id] }, `删除【${params.username}】用户`);
  proTable.value?.getTableList();
};

// 打开 drawer(新增、查看、编辑)
const drawerRef = ref(null);
const openDrawer = (title, row) => {
  const params = {
    title,
    row: { ...row },
    isView: title === "查看",
    api: title === "新增" ? addUser : title === "编辑" ? editUser : undefined,
    getTableList: proTable.value?.getTableList
  };
  drawerRef.value?.acceptParams(params);
  graphSelectedNode.value = row;
};

onMounted(async () => {
  await getTreeFilter();

  const msg = [
    "该页面 ProTable 数据不会自动请求，需等待 treeFilter 数据请求完成之后，才会触发表格请求。",
    "该页面 ProTable 性别搜索框为远程数据搜索，详情可查看代码。",
    "该页面可切换为图谱展示，详情可查看代码。"
  ];

  msg.map(item => {
    setTimeout(() => {
      ElNotification({
        title: "提示",
        message: item,
        type: "info",
        duration: 10000
      });
    }, 100);
  });
});
</script>
