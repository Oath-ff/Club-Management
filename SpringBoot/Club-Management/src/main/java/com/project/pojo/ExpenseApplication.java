package com.project.pojo;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "expense_applications")
public class ExpenseApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int expenseApplicationId;

    @ManyToOne
    @JoinColumn(name = "club_id")
    private Club club;

    @NotNull(message = "申请金额不能为空")
    @DecimalMin(value = "0.0", inclusive = false, message = "申请金额必须大于0")
    private BigDecimal amount;

    @NotNull(message = "申请日期不能为空")
    private LocalDateTime date;

    @NotBlank(message = "申请用途不能为空")
    private String purpose;

    @Size(max = 20, message = "状态不能超过20个字符")
    private String status = "待审核";
}
