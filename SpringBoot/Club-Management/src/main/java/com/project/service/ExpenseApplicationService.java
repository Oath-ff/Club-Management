package com.project.service;

import com.project.pojo.ExpenseApplication;

import java.util.List;

public interface ExpenseApplicationService {
    void submitExpenseApplication(ExpenseApplication expenseApplication);
    void approveExpenseApplication(Integer expenseApplicationId);
    void rejectExpenseApplication(Integer expenseApplicationId);
    List<ExpenseApplication> listExpenseApplications();
    List<ExpenseApplication> listExpenseApplicationsByClubId(Integer clubId);
    List<ExpenseApplication> listPendingExpenseApplicationsByClubId(Integer clubId);
    void updateExpenseApplication(ExpenseApplication expenseApplication);
}
