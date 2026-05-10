package me.sunlianhui.coinflow.modules.transferrecord.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TransferRecordEntity {
    private Long id;
    private Long userId;
    private Long accountBookId;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private BigDecimal fee;
    private String remark;
    private Date transferredAt;
    private Date createdAt;
}
