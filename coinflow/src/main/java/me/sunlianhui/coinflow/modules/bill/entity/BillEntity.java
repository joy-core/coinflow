package me.sunlianhui.coinflow.modules.bill.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillEntity {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private Long assetAccountId;
    private Long categoryId;
    private String type;
    private BigDecimal amount;
    private String remark;
    private String imageUrl;
    private String location;
    private Date happenedAt;
    private String tags;
    private Boolean isReimbursed;
    private Date createdAt;
    private Date updatedAt;

}
