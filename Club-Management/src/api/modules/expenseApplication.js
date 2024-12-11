import request from "../request"; // 引入封装的 Request 实例

/**
 * @name 费用申请管理模块
 */

// 提交费用申请
export const submitExpenseApplication = applicationData => request.post("/expense/submit", applicationData);

// 审批费用申请
export const approveExpenseApplication = (applicationId, status) =>
  request.patch(`/expense/approve/${applicationId}`, null, { params: { status } });

// 获取所有费用申请
export const listExpenseApplications = () => request.get("/expense/list");

// 根据社团ID获取费用申请
export const listExpenseApplicationsByClubId = clubId => request.get(`/expense/list/${clubId}`);

// 根据费用申请ID获取费用申请详情
export const findExpenseApplicationById = applicationId => request.get(`/expense/find/${applicationId}`);

// 更新费用申请信息
export const updateExpenseApplication = (applicationId, applicationData) =>
  request.patch(`/expense/update/${applicationId}`, applicationData);
