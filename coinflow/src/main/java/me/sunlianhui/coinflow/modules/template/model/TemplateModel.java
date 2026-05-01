package me.sunlianhui.coinflow.modules.template.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TemplateModel {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private Long assetAccountId;
    private Long categoryId;
    private String type;
    private BigDecimal amount;
    private String remark;
    private String name;

}
