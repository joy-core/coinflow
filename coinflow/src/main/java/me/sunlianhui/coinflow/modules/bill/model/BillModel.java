package me.sunlianhui.coinflow.modules.bill.model;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class BillModel {

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

}
