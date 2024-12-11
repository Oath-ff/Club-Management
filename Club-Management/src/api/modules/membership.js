import request from "../request"; // 引入封装的 Request 实例

/**
 * @name 成员管理模块
 */

// 添加成员
export const addMember = membershipData => request.post("/membership/add", membershipData);

// 查看成员列表
export const listMembers = clubId => request.get(`/membership/list/${clubId}`);

// 根据用户ID查找成员信息
export const findMembershipByUserId = userId => request.get("/membership/find", { params: { userId } });

// 拒绝用户加入社团
export const rejectMember = userId => request.patch(`/membership/reject`, null, { params: { userId } });

// 同意用户加入社团
export const approveMember = userId => request.patch(`/membership/approve`, null, { params: { userId } });

// 移除成员
export const removeMember = membershipId => request.delete(`/membership/remove/${membershipId}`);
