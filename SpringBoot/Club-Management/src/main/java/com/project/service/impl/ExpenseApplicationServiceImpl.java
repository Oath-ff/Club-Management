package com.project.service.impl;

import com.project.mapper.ExpenseApplicationMapper;
import com.project.pojo.ExpenseApplication;
import com.project.service.ExpenseApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExpenseApplicationServiceImpl implements ExpenseApplicationService {

    @Autowired
    private ExpenseApplicationMapper expenseApplicationMapper;

    @Override
    public void submitExpenseApplication(ExpenseApplication expenseApplication) {
        expenseApplication.setStatus("待审核"); // 设置费用申请状态为待审核
        expenseApplicationMapper.submitExpenseApplication(expenseApplication);
    }

    @Override
    public void approveExpenseApplication(Integer expenseApplicationId) {
        expenseApplicationMapper.approveExpenseApplication(expenseApplicationId);
    }

    @Override
    public void rejectExpenseApplication(Integer expenseApplicationId) {
        expenseApplicationMapper.rejectExpenseApplication(expenseApplicationId);
    }

    @Override
    public List<ExpenseApplication> listExpenseApplications() {
        return expenseApplicationMapper.listExpenseApplications();
    }

    @Override
    public List<ExpenseApplication> listExpenseApplicationsByClubId(Integer clubId) {
        return expenseApplicationMapper.listExpenseApplicationsByClubId(clubId);
    }

    @Override
    public List<ExpenseApplication> listPendingExpenseApplicationsByClubId(Integer clubId) {
        return expenseApplicationMapper.listPendingExpenseApplicationsByClubId(clubId);
    }

    @Override
    public void updateExpenseApplication(ExpenseApplication expenseApplication) {
        expenseApplicationMapper.updateExpenseApplication(expenseApplication);
    }
}
