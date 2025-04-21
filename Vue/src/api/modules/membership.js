import request from "@/api/index.js";

/**
 * @name 成员管理模块
 */

// 添加成员
export const addMember = membershipData => {
  return request.post("/membership/add", membershipData);
};

// 查看成员列表
export const listMembers = clubId => {
  return request.get(`/membership/list/${clubId}`);
};

// 查看审批成员列表
export const pendingList = clubId => {
  return request.get(`/membership/pendinglist/${clubId}`);
};

// 查找成员信息
export const findMembership = params => {
  return request.get("/membership/find", { params });
};

// 拒绝用户加入社团
export const rejectMember = (userId, clubId) => {
  return request.patch(`/membership/reject`, null, { params: { userId, clubId } });
};

// 同意用户加入社团
export const approveMember = (userId, clubId) => {
  return request.patch(`/membership/approve`, null, { params: { userId, clubId } });
};

// 移除成员
export const removeMember = membershipId => {
  return request.delete(`/membership/remove/${membershipId}`);
};

// 获取当前用户申请加入的社团记录
export const getApplicationRecord = () => {
  return request.get("/membership/applicationRecords");
};
