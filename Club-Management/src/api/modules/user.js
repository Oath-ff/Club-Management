import request from "../request"; // 引入封装的 Request 实例

/**
 * @name 用户管理模块
 */

// 用户注册
export const registerUser = params => request.post("/user/register", params);

// 用户登录
export const loginUser = params => request.post("/user/login", params);

// 获取用户详细信息
export const getUserInfo = () => request.get("/user/userInfo");

// 更新头像
export const updateAvatar = avatarUrl => request.patch(`/user/updateAvatar`, null, { params: { avatarUrl } });

// 更新用户密码
export const updatePassword = params => request.patch("/user/updatePwd", params);

// 用户注销
export const logoutUser = () => request.post("/user/logout");
