import request from "@/api/index.js";

/**
 * @name 活动管理模块
 */

// 添加活动
export const addEvent = eventData => request.post("/event/add", eventData);

// 删除活动
export const removeEvent = eventId => request.delete(`/event/remove/${eventId}`);

// 获取社团的所有活动
export const listEventsByClubId = clubId => request.get(`/event/list/${clubId}`);

// 根据活动ID获取活动详情
export const findEventById = eventId => request.get(`/event/find/${eventId}`);

// 更新活动信息
export const updateEvent = (eventId, eventData) => request.patch(`/event/update/${eventId}`, eventData);
