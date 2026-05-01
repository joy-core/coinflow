package me.sunlianhui.coinflow.modules.asset.model;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AssetAccountModel {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private String name;
    private String type;
    private BigDecimal balance;
    private Boolean isHidden;
    private Integer sortOrder;

}
