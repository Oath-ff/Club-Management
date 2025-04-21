package com.project.controller;

import com.project.pojo.ExpenseApplication;
import com.project.pojo.Result;
import com.project.service.ExpenseApplicationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/expense")
@Validated // Spring验证框架
public class ExpenseApplicationController {

    @Autowired
    private ExpenseApplicationService expenseApplicationService;

    @PostMapping("/submit") // 提交费用申请
    public Result submitExpenseApplication(@Valid @RequestBody ExpenseApplication expenseApplication) {
        expenseApplication.setStatus("待审核"); // 设置费用申请状态为待审核
        expenseApplicationService.submitExpenseApplication(expenseApplication);
        return Result.success();
    }

    @PatchMapping("/update/{expenseApplicationId}") // 更新费用申请
    public Result updateExpenseApplication(@PathVariable Integer expenseApplicationId, @Valid @RequestBody ExpenseApplication expenseApplication) {
        expenseApplication.setExpenseApplicationId(expenseApplicationId);
        expenseApplicationService.updateExpenseApplication(expenseApplication);
        return Result.success();
    }

    @PatchMapping("/approve/{expenseApplicationId}") // 审批费用申请
    public Result approveExpenseApplication(@PathVariable Integer expenseApplicationId) {
        expenseApplicationService.approveExpenseApplication(expenseApplicationId);
        return Result.success();
    }

    @PatchMapping("/reject/{expenseApplicationId}") // 拒绝费用申请
    public Result rejectExpenseApplication(@PathVariable Integer expenseApplicationId) {
        expenseApplicationService.rejectExpenseApplication(expenseApplicationId);
        return Result.success();
    }

    @GetMapping("/list") // 获取所有费用申请
    public Result<List<ExpenseApplication>> listExpenseApplications() {
        List<ExpenseApplication> applications = expenseApplicationService.listExpenseApplications();
        return Result.success(applications);
    }

    @GetMapping("/list/{clubId}") // 根据社团ID获取费用申请
    public Result<List<ExpenseApplication>> listExpenseApplicationsByClubId(@PathVariable Integer clubId) {
        List<ExpenseApplication> applications = expenseApplicationService.listExpenseApplicationsByClubId(clubId);
        return Result.success(applications);
    }

    @GetMapping("/list/pending/{clubId}") // 根据社团ID获取待审费用申请
    public Result<List<ExpenseApplication>> listPendingExpenseApplicationsByClubId(@PathVariable Integer clubId) {
        List<ExpenseApplication> applications = expenseApplicationService.listPendingExpenseApplicationsByClubId(clubId);
        return Result.success(applications);
    }
}
