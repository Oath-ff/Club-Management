package com.project.mapper;

import com.project.pojo.ExpenseApplication;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ExpenseApplicationMapper {

    // 提交费用申请
    @Insert("INSERT INTO expense_applications(club_id, amount, date, purpose, status) VALUES(#{club.clubId}, #{amount}, #{date}, #{purpose}, #{status})")
    void submitExpenseApplication(ExpenseApplication expenseApplication);

    // 审批费用申请
    @Update("UPDATE expense_applications SET status = '审核通过' WHERE expense_application_id = #{expenseApplicationId}")
    void approveExpenseApplication(Integer expenseApplicationId);

    // 拒绝费用申请
    @Update("UPDATE expense_applications SET status = '审核未通过' WHERE expense_application_id = #{expenseApplicationId}")
    void rejectExpenseApplication(Integer expenseApplicationId);

    // 获取所有费用申请
    @Select("SELECT ea.expense_application_id, ea.amount, ea.date, ea.purpose, ea.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM expense_applications ea " +
            "LEFT JOIN clubs c ON ea.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id ")
    @Results({
            @Result(property = "expenseApplicationId", column = "expense_application_id"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "date", column = "date"),
            @Result(property = "purpose", column = "purpose"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    List<ExpenseApplication> listExpenseApplications();

    // 根据社团ID获取费用申请
    @Select("SELECT ea.expense_application_id, ea.amount, ea.date, ea.purpose, ea.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM expense_applications ea " +
            "LEFT JOIN clubs c ON ea.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE ea.club_id = #{clubId}")
    @Results({
            @Result(property = "expenseApplicationId", column = "expense_application_id"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "date", column = "date"),
            @Result(property = "purpose", column = "purpose"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    List<ExpenseApplication> listExpenseApplicationsByClubId(@Param("clubId") Integer clubId);

    // 根据社团ID获取待审费用申请
    @Select("SELECT ea.expense_application_id, ea.amount, ea.date, ea.purpose, ea.status, " +
            "c.club_id, c.name AS club_name, c.type, c.description AS club_description, " +
            "u.user_id AS leader_id, u.username AS leader_name " +
            "FROM expense_applications ea " +
            "LEFT JOIN clubs c ON ea.club_id = c.club_id " +
            "LEFT JOIN users u ON c.leader_id = u.user_id " +
            "WHERE ea.club_id = #{clubId} AND ea.status = '待审核'")
    @Results({
            @Result(property = "expenseApplicationId", column = "expense_application_id"),
            @Result(property = "amount", column = "amount"),
            @Result(property = "date", column = "date"),
            @Result(property = "purpose", column = "purpose"),
            @Result(property = "status", column = "status"),
            @Result(property = "club.clubId", column = "club_id"),
            @Result(property = "club.name", column = "club_name"),
            @Result(property = "club.type", column = "type"),
            @Result(property = "club.description", column = "club_description"),
            @Result(property = "club.leader.userId", column = "leader_id"),
            @Result(property = "club.leader.username", column = "leader_name")
    })
    List<ExpenseApplication> listPendingExpenseApplicationsByClubId(@Param("clubId") Integer clubId);

    // 更新费用申请
    @Update("UPDATE expense_applications SET amount = #{amount}, date = #{date}, purpose = #{purpose}, status = #{status} WHERE expense_application_id = #{expenseApplicationId}")
    void updateExpenseApplication(ExpenseApplication expenseApplication);
}
