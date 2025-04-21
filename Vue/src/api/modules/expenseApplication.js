import request from "@/api/index.js";

/**
 * @name 费用申请管理模块
 */

// 提交费用申请
export const submitExpenseApplication = applicationData => {
  return request.post("/expense/submit", applicationData);
};

// 审批费用申请
export const approveExpenseApplication = (applicationId, status) => {
  return request.patch(`/expense/approve/${applicationId}`, null, { params: { status } });
};

// 获取所有费用申请
export const listExpenseApplications = () => {
  return request.get("/expense/list");
};

// 根据社团ID获取费用申请
export const listExpenseApplicationsByClubId = clubId => {
  return request.get(`/expense/list/${clubId}`);
};

// 根据社团ID获取待审申请
export const findPendingListByClubId = clubId => {
  return request.get(`/expense/list/pending/${clubId}`);
};

// 更新费用申请信息
export const updateExpenseApplication = (applicationId, applicationData) => {
  return request.patch(`/expense/update/${applicationId}`, applicationData);
};

// 拒绝通过审核费用申请
export const rejectExpenseApplication = (applicationId, status) => {
  return request.patch(`/expense/reject/${applicationId}`, null, { params: { status } });
};
