import request from "@/api/index.js";

/**
 * @name 社团管理模块
 */

// 创建新社团
export const createClub = clubData => request.post("/club/createClub", clubData);

// 查询社团详情
export const getClubDetails = clubName => request.get("/club/detail", { params: { clubName } });

// 修改社团信息
export const updateClub = (clubId, clubData) => request.patch(`/club/update/${clubId}`, clubData);

// 删除社团
export const deleteClub = clubId => request.delete(`/club/delete/${clubId}`);

// 查询所有社团
export const getAllClubs = () => request.get("/club/list");
