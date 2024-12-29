import request from "@/api/index.js";
/**
 * @name 用户管理模块
 */

// 用户注册
export const registerUser = registerForm => {
  //借助于UrlSearchParams完成传递
  const params = new URLSearchParams();
  for (let key in registerForm) {
    params.append(key, registerForm[key]);
  }
  return request.post("/user/register", params);
};

// 用户登录
export const loginUser = loginForm => {
  //借助于UrlSearchParams完成传递
  const params = new URLSearchParams();
  for (let key in loginForm) {
    params.append(key, loginForm[key]);
  }
  return request.post("/user/login", params);
};

// 获取用户详细信息
export const getUserInfo = () => {
  return request.get("/user/userInfo");
};

// 更新头像
export const updateAvatar = avatarUrl => {
  //借助于UrlSearchParams完成传递
  const params = new URLSearchParams();
  params.append("avatarUrl", avatarUrl);
  return request.patch("/user/updateAvatar", params);
};

// 更新用户密码
export const updatePassword = params => request.patch("/user/updatePwd", params);

// 用户注销
export const logoutUser = () => {
  return request.post("/user/logout");
};
