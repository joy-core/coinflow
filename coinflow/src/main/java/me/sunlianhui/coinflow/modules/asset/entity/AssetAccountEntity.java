package me.sunlianhui.coinflow.modules.asset.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class AssetAccountEntity {

    private Long id;
    private Long userId;
    private Long accountBookId;
    private String name;
    private String type;
    private BigDecimal balance;
    private Boolean isHidden;
    private Integer sortOrder;
    private Date createdAt;
    private Date updatedAt;

}
