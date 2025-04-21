import request from "@/api/index.js";

/**
 * @name 社团管理模块
 */

// 创建新社团
export const createClub = clubData => {
  return request.post("/club/createClub", clubData);
};

// 查询社团详情
export const getClubDetails = params => {
  return request.get("/club/detail", { params });
};

// 查询社长社团详情
export const getClubByLeaderID = () => {
  return request.get("/club/detailByLeaderID");
};

// 修改社团信息
export const updateClub = (clubId, clubData) => {
  return request.patch(`/club/update/${clubId}`, clubData);
};

// 删除社团
export const deleteClub = clubId => {
  return request.delete(`/club/delete/${clubId}`);
};

// 查询所有已审核社团
export const getAllClubs = () => {
  return request.get("/club/list");
};

// 查询所有待审核社团
export const getpendingClubs = () => {
  return request.get("/club/pending");
};

// 审核社团
export const approveClub = clubId => {
  return request.patch(`/club/approve/${clubId}`);
};

// 查询创建社团记录
export const getCreateClubRecord = () => {
  return request.get("/club/createdClubs");
};

// 不通过社团审核
export const rejectClub = clubId => {
  return request.patch(`/club/reject/${clubId}`);
};
