import request from "@/api/index.js";

/**
 * @name 消息管理模块
 */

// 发送消息
export const sendMsg = notificationData => {
  return request.post("/notification/send", notificationData);
};

// 查询用户的所有通知
export const userMsgList = userId => {
  return request.get(`/notification/user/${userId}`);
};
