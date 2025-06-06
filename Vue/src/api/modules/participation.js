import request from "@/api/index.js";

/**
 * @name 活动记录管理模块
 */

// 报名参加活动
export const joinEvent = (eventId, participationData) => {
  return request.post(`/participation/join`, participationData, { params: { eventId } });
};

// 签到
export const signIn = participationId => {
  return request.patch(`/participation/signIn`, null, { params: { participationId } });
};

// 查看活动的参与记录
export const listParticipationsByEventId = eventId => {
  return request.get(`/participation/event/${eventId}`);
};

// 查看成员的参与记录
export const listParticipationsByUserId = userId => {
  return request.get(`/participation/user/${userId}`);
};
