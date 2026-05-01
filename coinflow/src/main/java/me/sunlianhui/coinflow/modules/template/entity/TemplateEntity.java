package me.sunlianhui.coinflow.modules.template.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class TemplateEntity {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private Long assetAccountId;
    private Long categoryId;
    private String type;
    private BigDecimal amount;
    private String remark;
    private String name;
    private Date createdAt;
    private Date updatedAt;

}
