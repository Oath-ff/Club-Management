import { defineStore } from "pinia";
import { getUserInfo } from "@/api/modules/user";
import { getFlatMenuList, getShowMenuList, getAllBreadcrumbList } from "@/utils";

// 角色权限映射对象
const rolePermissions = {
  ADMIN: {
    buttons: [
      "club:create", // 创建社团
      "club:update", // 更新社团信息
      "club:delete", // 删除社团
      "club:detail", // 获取社团详情
      "club:list", // 获取所有社团信息
      "event:add", // 添加活动
      "event:update", // 更新活动
      "event:remove", // 删除活动
      "event:list", // 获取活动列表
      "event:find", // 获取活动详情
      "expense:update", // 更新费用申请
      "expense:approve", // 审批费用申请
      "expense:list", // 获取所有费用
      "expense:find", // 获取费用申请详情
      "membership:approve", // 批准成员
      "membership:list", // 获取成员列表
      "membership:find", // 查找成员
      "membership:reject", // 拒绝成员
      "membership:remove", // 移除成员
      "notification:send", // 发送通知
      "participation:signIn", // 活动签到
      "participation:viewEvent", // 查看活动参与记录
      "participation:viewUser" // 查看用户参与记录
    ],
    menus: [
      { name: "系统首页", path: "/home" },
      { name: "用户管理", path: "/user/**" },
      { name: "社团管理", path: "/club/**" },
      { name: "活动管理", path: "/event/**" },
      { name: "费用管理", path: "/expense/**" },
      { name: "成员管理", path: "/membership/**" },
      { name: "通知管理", path: "/notification/**" },
      { name: "活动记录", path: "/participation/**" }
    ]
  },
  LEADER: {
    buttons: [
      "club:update", // 更新社团信息
      "club:detail", // 获取社团详情
      "club:list", // 获取所有社团信息
      "event:add", // 添加活动
      "event:update", // 更新活动
      "event:remove", // 删除活动
      "event:list", // 获取活动列表
      "event:find", // 获取活动详情
      "expense:update", // 更新费用申请
      "expense:approve", // 审批费用申请
      "expense:list", // 获取所有费用
      "expense:find", // 获取费用申请详情
      "membership:approve", // 批准成员
      "membership:list", // 获取成员列表
      "membership:find", // 查找成员
      "membership:reject", // 拒绝成员
      "membership:remove", // 移除成员
      "notification:send", // 发送通知
      "participation:signIn", // 活动签到
      "participation:viewEvent", // 查看活动参与记录
      "participation:viewUser" // 查看用户参与记录
    ],
    menus: [
      { name: "系统首页", path: "/home" },
      { name: "社团管理", path: "/club/**" },
      { name: "活动管理", path: "/event/**" },
      { name: "费用管理", path: "/expense/**" },
      { name: "成员管理", path: "/membership/**" },
      { name: "通知管理", path: "/notification/**" },
      { name: "参与管理", path: "/participation/**" }
    ]
  },
  USER: {
    buttons: [
      "club:create", // 创建社团
      "club:detail", // 获取社团详情
      "club:list", // 获取所有社团信息
      "event:list", // 获取活动列表
      "event:find", // 获取活动详情
      "membership:add", // 申请入团\
      "membership:list", // 获取成员列表
      "membership:find", // 查找成员
      "participation:join", // 报名参加活动
      "participation:viewUser" // 查看用户参与记录
    ],
    menus: [
      { name: "系统首页", path: "/home" },
      { name: "社团管理", path: "/club/**" },
      { name: "活动浏览", path: "/event/**" },
      { name: "费用记录", path: "/expense/**" },
      { name: "活动记录", path: "/participation/**" }
    ]
  },
  VISITOR: {
    buttons: [
      "club:detail", // 获取社团详情
      "club:list", // 获取所有社团信息
      "event:list", // 获取活动列表
      "event:find" // 获取活动详情
    ],
    menus: [
      { name: "系统首页", path: "/home" },
      { name: "社团浏览", path: "/club/**" },
      { name: "活动查询", path: "/event/**" }
    ]
  }
};

export const useAuthStore = defineStore({
  id: "club-auth",
  state: () => ({
    // 按钮权限列表
    authButtonList: {},
    // 菜单权限列表
    authMenuList: [],
    // 当前页面的 router name，用来做按钮权限筛选
    routeName: "",
    //用户角色，用于权限管理
    userRoles: []
  }),
  getters: {
    // 按钮权限列表
    authButtonListGet: state => state.authButtonList,
    // 菜单权限列表 ==> 这里的菜单没有经过任何处理
    authMenuListGet: state => state.authMenuList,
    // 菜单权限列表 ==> 左侧菜单栏渲染，需要剔除 isHide == true
    showMenuListGet: state => getShowMenuList(state.authMenuList),
    // 菜单权限列表 ==> 扁平化之后的一维数组菜单，主要用来添加动态路由
    flatMenuListGet: state => getFlatMenuList(state.authMenuList),
    // 递归处理后的所有面包屑导航列表
    breadcrumbListGet: state => getAllBreadcrumbList(state.authMenuList)
  },
  actions: {
    // 获取用户的信息和权限角色
    async fetchUserInfo() {
      const { data } = await getUserInfo();
      console.log("User Info:", data);
      this.userRoles = data.role || [];
      this.setAuthPermissions(data.role);
    },
    // 设置按钮和菜单权限
    setAuthPermissions(role) {
      // 初始化权限
      let buttons = {};
      let menus = [];
      // 遍历角色，设置相应的按钮和菜单权限
      role.forEach(r => {
        if (rolePermissions[r]) {
          rolePermissions[r].buttons.forEach(button => {
            buttons[button] = true;
          });
          rolePermissions[r].menus.forEach(menu => {
            // 避免重复添加相同的菜单路径
            if (!menus.some(m => m.path === menu.path)) {
              menus.push(menu);
            }
          });
        }
      });
      this.authButtonList = buttons;
      this.authMenuList = menus;
    },
    // 设置路由名称
    async setRouteName(name) {
      this.routeName = name;
    }
  }
});
