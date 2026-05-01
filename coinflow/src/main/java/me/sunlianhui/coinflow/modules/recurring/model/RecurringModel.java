package me.sunlianhui.coinflow.modules.recurring.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class RecurringModel {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private Long assetAccountId;
    private Long categoryId;
    private String type;
    private BigDecimal amount;
    private String remark;
    private Date startDate;
    private Date endDate;
    private String frequency;
    private Boolean isActive;

}
