import request from "@/api/index.js";

/**
 * @name 活动管理模块
 */

// 添加活动
export const addEvent = eventData => {
  return request.post("/event/add", eventData);
};

// 删除活动
export const removeEvent = eventId => {
  return request.delete(`/event/remove/${eventId}`);
};

// 获取社团的所有通过审核的活动
export const listEventsByClubId = clubId => {
  return request.get(`/event/list/${clubId}`);
};

// 获取社团的所有状态的活动
export const getEventList = clubId => {
  return request.get(`/event/getList/${clubId}`);
};

// 查询活动详情
export const findEvent = params => {
  return request.get("/event/find", { params });
};

// 更新通过审核的活动信息
export const updateEvent = (eventId, eventData) => {
  return request.patch(`/event/update/${eventId}`, eventData);
};

// 查询社团所有待审核的活动
export const getpendingEvent = clubId => {
  return request.get(`/event/pending/${clubId}`);
};

// 审核活动
export const approveEvent = eventId => {
  return request.patch(`/event/approve/${eventId}`);
};

// 拒绝通过审核活动
export const rejectEvent = eventId => {
  return request.patch(`/event/reject/${eventId}`);
};
